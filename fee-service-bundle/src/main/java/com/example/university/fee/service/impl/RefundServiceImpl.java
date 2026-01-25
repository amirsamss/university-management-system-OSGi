package com.example.university.fee.service.impl;

import com.example.university.fee.model.AccountStatement;
import com.example.university.fee.model.Invoice;
import com.example.university.fee.model.Refund;
import com.example.university.fee.service.AccountStatementService;
import com.example.university.fee.service.InvoiceService;
import com.example.university.fee.service.RefundService;
import com.example.university.fee.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Refund Service Implementation - JPA with Supabase
 * UC-28: Process Refund
 */
public class RefundServiceImpl implements RefundService {
    
    private InvoiceService invoiceService;
    private AccountStatementService accountStatementService;

    public void setInvoiceService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public void setAccountStatementService(AccountStatementService accountStatementService) {
        this.accountStatementService = accountStatementService;
    }

    @Override
    public Refund processRefund(String studentId, BigDecimal amount, String reason,
                               Refund.RefundType refundType, String holdingSemester) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Refund amount must be greater than zero");
        }
        
        // Get credit balance
        BigDecimal creditBalance = getCreditBalance(studentId);
        
        if (creditBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("No credit balance available for refund. Current credit balance: $0.00");
        }
        
        if (amount.compareTo(creditBalance) > 0) {
            throw new RuntimeException("Refund amount ($" + amount + ") exceeds available credit balance ($" + creditBalance + ")");
        }
        
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.beginTransaction();
            
            Refund refund = new Refund();
            refund.setStudentId(studentId);
            refund.setAmount(amount);
            refund.setReason(reason);
            refund.setRefundType(refundType);
            refund.setStatus(Refund.RefundStatus.PENDING);
            
            if (refundType == Refund.RefundType.TRANSFER_NEXT_SEMESTER) {
                refund.setHoldingSemester(holdingSemester);
                refund.setStatus(Refund.RefundStatus.HELD);
            }
            
            em.persist(refund);
            em.flush(); // Flush to get the refund ID
            
            // Create account statement entry for the refund
            AccountStatement statement = new AccountStatement();
            statement.setStudentId(studentId);
            statement.setTransactionType(AccountStatement.TransactionType.REFUND);
            statement.setReferenceId(refund.getId());
            statement.setReferenceType(AccountStatement.ReferenceType.REFUND);
            statement.setDescription("Refund - " + refundType + 
                (reason != null ? " (" + reason + ")" : ""));
            // Refund = money OUT (student receives money back) = CREDIT
            statement.setDebitAmount(BigDecimal.ZERO);
            statement.setCreditAmount(amount);
            statement.setRecordedBy("SYSTEM");
            
            // Calculate running balance
            // Balance: +balance = has money (can refund), -balance = owes university
            // Refund (CREDIT) = money OUT → balance decreases
            BigDecimal previousBalance = calculatePreviousBalance(studentId);
            statement.setRunningBalance(previousBalance.subtract(amount)); // CREDIT decreases balance
            
            accountStatementService.createStatementEntry(statement);
            
            JpaUtil.commitTransaction();
            
            return refund;
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to process refund: " + e.getMessage(), e);
        }
    }

    @Override
    public Refund getRefundById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        return em.find(Refund.class, id);
    }

    @Override
    public List<Refund> getRefundsByStudent(String studentId) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Refund> query = em.createQuery(
            "SELECT r FROM Refund r WHERE r.studentId = :studentId ORDER BY r.createdAt DESC", 
            Refund.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }

    @Override
    public List<Refund> getRefundsByStatus(Refund.RefundStatus status) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Refund> query = em.createQuery(
            "SELECT r FROM Refund r WHERE r.status = :status ORDER BY r.createdAt DESC", 
            Refund.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public BigDecimal getCreditBalance(String studentId) {
        // Balance: +balance = has money (can refund), -balance = owes university
        // Method 1: Get the latest running balance from AccountStatement
        // If positive, that's the credit balance available for refund
        BigDecimal runningBalance = calculatePreviousBalance(studentId);
        if (runningBalance != null && runningBalance.compareTo(BigDecimal.ZERO) > 0) {
            // Positive balance means student has credit (can refund)
            return runningBalance;
        }
        
        // Method 2: Fallback - Calculate from invoices if balance is zero or negative
        // This handles edge cases where balance calculation might be off
        List<Invoice> invoices = invoiceService.getInvoicesByStudent(studentId);
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalFinancialAid = BigDecimal.ZERO;
        BigDecimal totalCharges = BigDecimal.ZERO;
        BigDecimal totalLateFees = BigDecimal.ZERO;
        
        for (Invoice invoice : invoices) {
            BigDecimal paid = invoice.getAmountPaid() != null ? invoice.getAmountPaid() : BigDecimal.ZERO;
            BigDecimal aid = invoice.getFinancialAidAmount() != null ? invoice.getFinancialAidAmount() : BigDecimal.ZERO;
            BigDecimal charges = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;
            BigDecimal lateFees = invoice.getLateFeeAmount() != null ? invoice.getLateFeeAmount() : BigDecimal.ZERO;
            
            totalPaid = totalPaid.add(paid);
            totalFinancialAid = totalFinancialAid.add(aid);
            totalCharges = totalCharges.add(charges);
            totalLateFees = totalLateFees.add(lateFees);
        }
        
        // Credit = (Payments + Financial Aid) - (Charges + Late Fees)
        BigDecimal totalOwed = totalCharges.add(totalLateFees);
        BigDecimal totalCredits = totalPaid.add(totalFinancialAid);
        BigDecimal calculatedCredit = totalCredits.subtract(totalOwed);
        
        // Only return positive credit (if they've overpaid)
        return calculatedCredit.compareTo(BigDecimal.ZERO) > 0 ? calculatedCredit : BigDecimal.ZERO;
    }

    @Override
    public Refund approveRefund(Long id, String approvedBy) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.beginTransaction();
            Refund refund = em.find(Refund.class, id);
            if (refund != null) {
                refund.setStatus(Refund.RefundStatus.APPROVED);
                refund.setApprovedBy(approvedBy);
                em.merge(refund);
                JpaUtil.commitTransaction();
            }
            return refund;
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to approve refund: " + e.getMessage(), e);
        }
    }

    @Override
    public Refund completeRefund(Long id, String processedBy, String bankReference) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.beginTransaction();
            Refund refund = em.find(Refund.class, id);
            if (refund != null) {
                if (refund.getStatus() != Refund.RefundStatus.APPROVED && 
                    refund.getStatus() != Refund.RefundStatus.HELD) {
                    throw new RuntimeException("Refund must be approved before processing");
                }
                
                refund.setStatus(Refund.RefundStatus.PROCESSED);
                refund.setProcessedBy(processedBy);
                refund.setProcessedDate(LocalDateTime.now());
                refund.setBankReference(bankReference);
                
                // Note: Refund processing doesn't need to modify invoices
                // The credit balance is tracked in AccountStatement running balance
                // When refund is processed, it reduces the credit balance
                // No need to modify invoice amounts as the credit is already reflected in AccountStatement
                
                em.merge(refund);
                JpaUtil.commitTransaction();
            }
            return refund;
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to complete refund: " + e.getMessage(), e);
        }
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
