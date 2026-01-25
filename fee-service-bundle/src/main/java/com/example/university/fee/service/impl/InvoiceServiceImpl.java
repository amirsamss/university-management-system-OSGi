package com.example.university.fee.service.impl;

import com.example.university.fee.model.*;
import com.example.university.fee.service.AccountStatementService;
import com.example.university.fee.service.FeeStructureService;
import com.example.university.fee.service.InvoiceService;
import com.example.university.fee.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Invoice Service Implementation - JPA with Supabase
 * UC-24: Generate Invoices
 * UC-25: Calculate Tuition
 */
public class InvoiceServiceImpl implements InvoiceService {
    
    private FeeStructureService feeStructureService;
    private AccountStatementService accountStatementService;

    public void setFeeStructureService(FeeStructureService feeStructureService) {
        this.feeStructureService = feeStructureService;
    }

    public void setAccountStatementService(AccountStatementService accountStatementService) {
        this.accountStatementService = accountStatementService;
    }

    @Override
    public BigDecimal calculateTuition(String studentId, String academicYear, String semester,
                                     String department, String studentType, Integer credits) {
        // Get fee structure
        FeeStructure feeStructure = feeStructureService.getFeeStructure(academicYear, department, studentType);
        if (feeStructure == null) {
            throw new RuntimeException("Fee structure not found for " + academicYear + 
                                     " - " + department + " - " + studentType);
        }
        
        // Calculate: (Credits * Cost Per Credit) + Fixed Fees
        BigDecimal tuitionAmount = feeStructure.getCostPerCredit()
            .multiply(BigDecimal.valueOf(credits));
        
        BigDecimal fixedFees = feeStructure.getTotalFixedFees();
        
        // TODO: Add housing/meal plan costs if applicable
        BigDecimal otherCharges = BigDecimal.ZERO;
        
        BigDecimal total = tuitionAmount.add(fixedFees).add(otherCharges);
        
        return total;
    }

    @Override
    public Invoice generateInvoice(String studentId, String academicYear, String semester,
                                  String department, String studentType, Integer credits) {
        BigDecimal totalAmount = calculateTuition(studentId, academicYear, semester, 
                                                   department, studentType, credits);
        
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.beginTransaction();
            
            // If total is $0, generate Statement of Account with SETTLED status
            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            invoice.setStudentId(studentId);
            invoice.setSemester(semester);
            invoice.setAcademicYear(academicYear);
            invoice.setTotalCredits(credits);
            
            FeeStructure feeStructure = feeStructureService.getFeeStructure(academicYear, department, studentType);
            if (feeStructure != null) {
                invoice.setTuitionAmount(feeStructure.getCostPerCredit()
                    .multiply(BigDecimal.valueOf(credits)));
                invoice.setFixedFeesAmount(feeStructure.getTotalFixedFees());
            }
            
            invoice.setTotalAmount(totalAmount);
            invoice.setDueDate(LocalDate.now().plusDays(30));
            
            if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
                invoice.setStatus(Invoice.InvoiceStatus.SETTLED);
            } else {
                invoice.setStatus(Invoice.InvoiceStatus.UNPAID);
            }
            
            // Create line items
            if (feeStructure != null) {
                InvoiceLineItem tuitionItem = new InvoiceLineItem();
                tuitionItem.setDescription("Tuition (" + credits + " credits)");
                tuitionItem.setAmount(invoice.getTuitionAmount());
                tuitionItem.setItemType(InvoiceLineItem.LineItemType.TUITION);
                tuitionItem.setQuantity(credits);
                tuitionItem.setUnitPrice(feeStructure.getCostPerCredit());
                invoice.addLineItem(tuitionItem);
                
                for (FeeItem feeItem : feeStructure.getFeeItems()) {
                    InvoiceLineItem lineItem = new InvoiceLineItem();
                    lineItem.setDescription(feeItem.getName());
                    lineItem.setAmount(feeItem.getAmount());
                    lineItem.setItemType(InvoiceLineItem.LineItemType.OTHER);
                    invoice.addLineItem(lineItem);
                }
            }
            
            em.persist(invoice);
            em.flush(); // Flush to get the invoice ID
            
            // Create account statement entry for the invoice charge
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                AccountStatement statement = new AccountStatement();
                statement.setStudentId(studentId);
                statement.setTransactionType(AccountStatement.TransactionType.CHARGE);
                statement.setReferenceId(invoice.getId());
                statement.setReferenceType(AccountStatement.ReferenceType.INVOICE);
                statement.setDescription("Invoice " + invoice.getInvoiceNumber() + " - " + semester + " " + academicYear);
                // Invoice = money OUT (student is charged) = CREDIT
                statement.setDebitAmount(BigDecimal.ZERO);
                statement.setCreditAmount(totalAmount);
                statement.setAcademicYear(academicYear);
                statement.setSemester(semester);
                statement.setRecordedBy("SYSTEM");
                
                // Calculate running balance
                // Balance: +balance = has money (can refund), -balance = owes university
                // Invoice (CREDIT) = money OUT → balance decreases
                BigDecimal previousBalance = calculatePreviousBalance(studentId);
                BigDecimal safeTotalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
                statement.setRunningBalance(previousBalance.subtract(safeTotalAmount)); // CREDIT decreases balance
                
                accountStatementService.createStatementEntry(statement);
            }
            
            JpaUtil.commitTransaction();
            
            return invoice;
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to generate invoice: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Invoice> generateInvoicesBatch(String academicYear, String semester) {
        // TODO: In a real system, this would query registered students from student service
        throw new UnsupportedOperationException(
            "Batch invoice generation requires integration with student service");
    }

    @Override
    public Invoice getInvoiceById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        return em.find(Invoice.class, id);
    }

    @Override
    public Invoice getInvoiceByNumber(String invoiceNumber) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Invoice> query = em.createQuery(
            "SELECT i FROM Invoice i WHERE i.invoiceNumber = :number", Invoice.class);
        query.setParameter("number", invoiceNumber);
        List<Invoice> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<Invoice> getInvoicesByStudent(String studentId) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Invoice> query = em.createQuery(
            "SELECT i FROM Invoice i WHERE i.studentId = :studentId ORDER BY i.generatedAt DESC", 
            Invoice.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }

    @Override
    public List<Invoice> getInvoicesByStatus(Invoice.InvoiceStatus status) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Invoice> query = em.createQuery(
            "SELECT i FROM Invoice i WHERE i.status = :status ORDER BY i.generatedAt DESC", 
            Invoice.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<Invoice> getOutstandingInvoices() {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<Invoice> query = em.createQuery(
            "SELECT i FROM Invoice i WHERE i.status IN :statuses ORDER BY i.dueDate ASC", 
            Invoice.class);
        query.setParameter("statuses", List.of(
            Invoice.InvoiceStatus.UNPAID, 
            Invoice.InvoiceStatus.PARTIALLY_PAID,
            Invoice.InvoiceStatus.OVERDUE
        ));
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
