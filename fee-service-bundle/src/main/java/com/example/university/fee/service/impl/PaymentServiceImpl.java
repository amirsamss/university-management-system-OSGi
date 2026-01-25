package com.example.university.fee.service.impl;

import com.example.university.fee.model.AccountStatement;
import com.example.university.fee.model.Invoice;
import com.example.university.fee.model.Payment;
import com.example.university.fee.service.AccountStatementService;
import com.example.university.fee.service.InvoiceService;
import com.example.university.fee.service.PaymentService;
import com.example.university.fee.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Payment Service Implementation - JPA with Supabase
 * UC-27: Track Payments & Outstanding Fees
 */
public class PaymentServiceImpl implements PaymentService {
    
    private InvoiceService invoiceService;
    private AccountStatementService accountStatementService;

    public void setInvoiceService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public void setAccountStatementService(AccountStatementService accountStatementService) {
        this.accountStatementService = accountStatementService;
    }

    @Override
    public Payment recordPayment(Payment payment) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.beginTransaction();
            
            // Check for duplicate reference number
            Payment existing = getPaymentByReferenceNumber(payment.getReferenceNumber());
            if (existing != null) {
                throw new RuntimeException("Duplicate Transaction Detected: Reference number already exists");
            }
            
            BigDecimal remainingPayment = payment.getAmount();
            Invoice primaryInvoice = null;
            
            // If invoice is specified, apply to that invoice first
            if (payment.getInvoice() != null && payment.getInvoice().getId() != null) {
                primaryInvoice = invoiceService.getInvoiceById(payment.getInvoice().getId());
                if (primaryInvoice != null) {
                    BigDecimal invoiceOutstanding = primaryInvoice.getOutstandingBalance();
                    if (invoiceOutstanding != null && invoiceOutstanding.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal amountToApply = remainingPayment.min(invoiceOutstanding);
                        primaryInvoice.recordPayment(amountToApply);
                        em.merge(primaryInvoice);
                        remainingPayment = remainingPayment.subtract(amountToApply);
                    }
                }
            }
            
            // Apply remaining payment to other outstanding invoices (FIFO - oldest first)
            if (remainingPayment.compareTo(BigDecimal.ZERO) > 0) {
                List<Invoice> outstandingInvoices = invoiceService.getInvoicesByStudent(payment.getStudentId());
                // Sort by due date (oldest first), then by invoice number
                outstandingInvoices.sort((i1, i2) -> {
                    if (i1.getDueDate() != null && i2.getDueDate() != null) {
                        int dateCompare = i1.getDueDate().compareTo(i2.getDueDate());
                        if (dateCompare != 0) return dateCompare;
                    }
                    return i1.getInvoiceNumber().compareTo(i2.getInvoiceNumber());
                });
                
                for (Invoice invoice : outstandingInvoices) {
                    if (remainingPayment.compareTo(BigDecimal.ZERO) <= 0) break;
                    
                    // Skip if this is the primary invoice already processed
                    if (primaryInvoice != null && invoice.getId().equals(primaryInvoice.getId())) {
                        continue;
                    }
                    
                    BigDecimal invoiceOutstanding = invoice.getOutstandingBalance();
                    if (invoiceOutstanding != null && invoiceOutstanding.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal amountToApply = remainingPayment.min(invoiceOutstanding);
                        invoice.recordPayment(amountToApply);
                        em.merge(invoice);
                        remainingPayment = remainingPayment.subtract(amountToApply);
                        
                        // Set primary invoice if not set yet (for reference)
                        if (primaryInvoice == null) {
                            primaryInvoice = invoice;
                        }
                    }
                }
            }
            
            // Set the primary invoice as the payment's invoice reference
            if (primaryInvoice != null) {
                payment.setInvoice(primaryInvoice);
            }
            
            em.persist(payment);
            em.flush(); // Flush to get the payment ID
            
            // Create account statement entry for the payment
            AccountStatement statement = new AccountStatement();
            statement.setStudentId(payment.getStudentId());
            statement.setTransactionType(AccountStatement.TransactionType.PAYMENT);
            statement.setReferenceId(payment.getId());
            statement.setReferenceType(AccountStatement.ReferenceType.PAYMENT);
            
            // Build description with invoice info if available
            String description = "Payment - " + payment.getPaymentMethod();
            if (payment.getReferenceNumber() != null) {
                description += " (Ref: " + payment.getReferenceNumber() + ")";
            }
            if (primaryInvoice != null) {
                description += " - Invoice: " + primaryInvoice.getInvoiceNumber();
            }
            if (remainingPayment.compareTo(BigDecimal.ZERO) > 0) {
                description += " [Credit: $" + remainingPayment + "]";
            }
            statement.setDescription(description);
            
            // Payment = money IN (from external source) = DEBIT
            statement.setDebitAmount(payment.getAmount());
            statement.setCreditAmount(BigDecimal.ZERO);
            
            // Get invoice info if available
            if (primaryInvoice != null) {
                statement.setAcademicYear(primaryInvoice.getAcademicYear());
                statement.setSemester(primaryInvoice.getSemester());
            }
            
            statement.setRecordedBy("SYSTEM");
            
            // Calculate running balance
            // Balance: +balance = has money (can refund), -balance = owes university
            // Payment (DEBIT) = money IN → balance increases
            BigDecimal previousBalance = calculatePreviousBalance(payment.getStudentId());
            BigDecimal paymentAmount = payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO;
            statement.setRunningBalance(previousBalance.add(paymentAmount)); // DEBIT increases balance
            
            accountStatementService.createStatementEntry(statement);
            
            // Note: Overpayment is already reflected in the balance calculation above
            // The full payment amount (including overpayment) is recorded as DEBIT
            // No separate ADJUSTMENT entry needed - it would double-count the overpayment
            
            JpaUtil.commitTransaction();
            
            return payment;
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to record payment: " + e.getMessage(), e);
        }
    }

    @Override
    public Payment getPaymentById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        return em.find(Payment.class, id);
    }

    @Override
    public Payment getPaymentByReferenceNumber(String referenceNumber) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Payment> query = em.createQuery(
            "SELECT p FROM Payment p WHERE p.referenceNumber = :ref", Payment.class);
        query.setParameter("ref", referenceNumber);
        List<Payment> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<Payment> getPaymentsByStudent(String studentId) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Payment> query = em.createQuery(
            "SELECT p FROM Payment p WHERE p.studentId = :studentId ORDER BY p.paymentDate DESC", 
            Payment.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }

    @Override
    public List<Payment> getPaymentsByInvoice(Long invoiceId) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Payment> query = em.createQuery(
            "SELECT p FROM Payment p WHERE p.invoice.id = :invoiceId ORDER BY p.paymentDate DESC", 
            Payment.class);
        query.setParameter("invoiceId", invoiceId);
        return query.getResultList();
    }

    @Override
    public List<Payment> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Payment> query = em.createQuery(
            "SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end ORDER BY p.paymentDate DESC", 
            Payment.class);
        query.setParameter("start", startDate);
        query.setParameter("end", endDate);
        return query.getResultList();
    }

    @Override
    public Payment reversePayment(Long id, String reason) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.beginTransaction();
            Payment payment = em.find(Payment.class, id);
            if (payment != null) {
                payment.setStatus(Payment.PaymentStatus.REVERSED);
                payment.setNotes((payment.getNotes() != null ? payment.getNotes() + "\n" : "") + 
                               "Reversed: " + reason);
                
                // Reverse payment on invoice
                if (payment.getInvoice() != null) {
                    Invoice invoice = invoiceService.getInvoiceById(payment.getInvoice().getId());
                    if (invoice != null) {
                        invoice.setAmountPaid(invoice.getAmountPaid().subtract(payment.getAmount()));
                        invoice.calculateOutstandingBalance();
                        em.merge(invoice);
                    }
                }
                
                em.merge(payment);
                JpaUtil.commitTransaction();
            }
            return payment;
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to reverse payment: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Object[]> getOutstandingFeesReport() {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Object[]> query = em.createQuery(
            "SELECT i.studentId, SUM(i.outstandingBalance) " +
            "FROM Invoice i " +
            "WHERE i.outstandingBalance > 0 " +
            "GROUP BY i.studentId " +
            "ORDER BY SUM(i.outstandingBalance) DESC", 
            Object[].class);
        return query.getResultList();
    }

    private BigDecimal calculatePreviousBalance(String studentId) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<AccountStatement> query = em.createQuery(
            "SELECT s FROM AccountStatement s WHERE s.studentId = :studentId " +
            "ORDER BY s.transactionDate DESC, s.id DESC", AccountStatement.class);
        query.setParameter("studentId", studentId);
        query.setMaxResults(1);
        List<AccountStatement> results = query.getResultList();
        if (results.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal balance = results.get(0).getRunningBalance();
        return balance != null ? balance : BigDecimal.ZERO;
    }
}
