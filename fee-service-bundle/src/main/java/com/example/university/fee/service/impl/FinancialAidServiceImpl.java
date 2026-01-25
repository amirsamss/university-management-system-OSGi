package com.example.university.fee.service.impl;

import com.example.university.fee.model.AccountStatement;
import com.example.university.fee.model.FinancialAid;
import com.example.university.fee.model.Invoice;
import com.example.university.fee.service.AccountStatementService;
import com.example.university.fee.service.FinancialAidService;
import com.example.university.fee.service.InvoiceService;
import com.example.university.fee.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Financial Aid Service Implementation - JPA with Supabase
 * UC-26: Manage Financial Aid
 */
public class FinancialAidServiceImpl implements FinancialAidService {
    
    private InvoiceService invoiceService;
    private AccountStatementService accountStatementService;

    public void setInvoiceService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public void setAccountStatementService(AccountStatementService accountStatementService) {
        this.accountStatementService = accountStatementService;
    }

    @Override
    public FinancialAid createFinancialAid(FinancialAid financialAid) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.beginTransaction();
            em.persist(financialAid);
            JpaUtil.commitTransaction();
            return financialAid;
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to create financial aid: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FinancialAid> allocateFinancialAid() {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<FinancialAid> query = em.createQuery(
            "SELECT f FROM FinancialAid f WHERE f.status = :status", FinancialAid.class);
        query.setParameter("status", FinancialAid.AidStatus.PENDING);
        List<FinancialAid> pendingAids = query.getResultList();
        
        for (FinancialAid aid : pendingAids) {
            try {
                applyFinancialAidToStudent(aid.getStudentId(), aid.getAcademicYear(), aid.getSemester());
            } catch (Exception e) {
                // Silently continue with next aid
            }
        }
        
        return pendingAids;
    }

    @Override
    public FinancialAid applyFinancialAidToStudent(String studentId, String academicYear, String semester) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.beginTransaction();
            
            // Get pending financial aids for the student
            TypedQuery<FinancialAid> query = em.createQuery(
                "SELECT f FROM FinancialAid f WHERE f.studentId = :studentId " +
                "AND f.academicYear = :year AND f.semester = :semester " +
                "AND f.status = :status", FinancialAid.class);
            query.setParameter("studentId", studentId);
            query.setParameter("year", academicYear);
            query.setParameter("semester", semester);
            query.setParameter("status", FinancialAid.AidStatus.PENDING);
            
            List<FinancialAid> aids = query.getResultList();
            
            if (aids.isEmpty()) {
                JpaUtil.commitTransaction();
                throw new RuntimeException("No pending financial aid found for student " + studentId + 
                    " for " + semester + " " + academicYear);
            }
            
            // Get outstanding invoices
            List<Invoice> invoices = invoiceService.getInvoicesByStudent(studentId);
            Invoice targetInvoice = invoices.stream()
                .filter(inv -> inv.getAcademicYear() != null && inv.getAcademicYear().equals(academicYear) && 
                              inv.getSemester() != null && inv.getSemester().equals(semester) &&
                              inv.getOutstandingBalance() != null &&
                              inv.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0)
                .findFirst()
                .orElse(null);
            
            if (targetInvoice == null) {
                JpaUtil.commitTransaction();
                throw new RuntimeException("No outstanding invoice found for student " + studentId + 
                    " for " + semester + " " + academicYear + ". Please generate an invoice first.");
            }
            
            // Refresh the invoice to get the latest outstanding balance
            targetInvoice = em.find(Invoice.class, targetInvoice.getId());
            if (targetInvoice == null) {
                JpaUtil.commitTransaction();
                throw new RuntimeException("Invoice not found in database");
            }
            
            // Ensure outstanding balance is calculated
            if (targetInvoice.getOutstandingBalance() == null) {
                targetInvoice.calculateOutstandingBalance();
            }
            
            BigDecimal totalAidAmount = BigDecimal.ZERO;
            FinancialAid firstAppliedAid = null;
            
            for (FinancialAid aid : aids) {
                // Check eligibility (e.g., GPA, max aid cap)
                if (isEligible(aid)) {
                    BigDecimal aidAmount = aid.getAmount();
                    if (aidAmount == null) {
                        aidAmount = BigDecimal.ZERO;
                    }
                    
                    BigDecimal outstandingBalance = targetInvoice.getOutstandingBalance();
                    if (outstandingBalance == null || outstandingBalance.compareTo(BigDecimal.ZERO) <= 0) {
                        // No more outstanding balance, skip this aid
                        continue;
                    }
                    
                    // If aid exceeds total bill and not refundable, cap it
                    if (aidAmount.compareTo(outstandingBalance) > 0 && !aid.getIsRefundable()) {
                        aidAmount = outstandingBalance;
                    }
                    
                    // Only apply if there's something to apply
                    if (aidAmount.compareTo(BigDecimal.ZERO) > 0) {
                        aid.setAmount(aidAmount);
                        aid.setStatus(FinancialAid.AidStatus.APPLIED);
                        aid.setAppliedToInvoiceId(targetInvoice.getId());
                        aid.setAppliedDate(LocalDateTime.now());
                        
                        em.merge(aid);
                        
                        // Apply to invoice
                        targetInvoice.applyFinancialAid(aidAmount);
                        em.merge(targetInvoice);
                        em.flush(); // Flush to ensure changes are persisted
                        
                        // Refresh invoice to get updated balance
                        targetInvoice = em.find(Invoice.class, targetInvoice.getId());
                        targetInvoice.calculateOutstandingBalance();
                        
                        // Create account statement entry for financial aid
                        AccountStatement statement = new AccountStatement();
                        statement.setStudentId(studentId);
                        statement.setTransactionType(AccountStatement.TransactionType.FINANCIAL_AID);
                        statement.setReferenceId(aid.getId());
                        statement.setReferenceType(AccountStatement.ReferenceType.FINANCIAL_AID);
                        statement.setDescription("Financial Aid - " + aid.getAidType() + 
                            (aid.getName() != null ? " (" + aid.getName() + ")" : ""));
                        // Financial Aid = money IN (student receives) = DEBIT
                        statement.setDebitAmount(aidAmount);
                        statement.setCreditAmount(BigDecimal.ZERO);
                        statement.setAcademicYear(academicYear);
                        statement.setSemester(semester);
                        statement.setRecordedBy("SYSTEM");
                        
                        // Calculate running balance
                        // Balance: +balance = has money (can refund), -balance = owes university
                        // Financial Aid (DEBIT) = money IN → balance increases
                        BigDecimal previousBalance = calculatePreviousBalance(studentId);
                        statement.setRunningBalance(previousBalance.add(aidAmount)); // DEBIT increases balance
                        
                        accountStatementService.createStatementEntry(statement);
                        
                        totalAidAmount = totalAidAmount.add(aidAmount);
                        
                        if (firstAppliedAid == null) {
                            firstAppliedAid = aid;
                        }
                    }
                } else {
                    // Mark as revoked if eligibility breached (without creating reversal entry)
                    aid.setStatus(FinancialAid.AidStatus.REVOKED);
                    aid.setRevokedReason("Eligibility criteria not met");
                    em.merge(aid);
                }
            }
            
            JpaUtil.commitTransaction();
            
            if (firstAppliedAid == null) {
                throw new RuntimeException("Financial aid could not be applied. All aid amounts may have been capped or invoice already paid.");
            }
            
            return firstAppliedAid;
        } catch (RuntimeException e) {
            JpaUtil.rollbackTransaction();
            throw e; // Re-throw runtime exceptions as-is
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to apply financial aid: " + e.getMessage(), e);
        }
    }

    private boolean isEligible(FinancialAid aid) {
        // TODO: Implement eligibility checks (GPA, max aid cap, etc.)
        return true;
    }

    @Override
    public FinancialAid getFinancialAidById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        return em.find(FinancialAid.class, id);
    }

    @Override
    public List<FinancialAid> getFinancialAidByStudent(String studentId) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<FinancialAid> query = em.createQuery(
            "SELECT f FROM FinancialAid f WHERE f.studentId = :studentId ORDER BY f.createdAt DESC", 
            FinancialAid.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }

    @Override
    public FinancialAid revokeFinancialAid(Long id, String reason) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.beginTransaction();
            FinancialAid aid = em.find(FinancialAid.class, id);
            if (aid == null) {
                JpaUtil.commitTransaction();
                throw new RuntimeException("Financial aid with ID " + id + " not found");
            }
            
            if (aid.getStatus() == FinancialAid.AidStatus.REVOKED) {
                JpaUtil.commitTransaction();
                throw new RuntimeException("Financial aid is already revoked");
            }
            
            aid.setStatus(FinancialAid.AidStatus.REVOKED);
            aid.setRevokedReason(reason);
            
            // If already applied to invoice, reverse it
            if (aid.getAppliedToInvoiceId() != null) {
                Invoice invoice = invoiceService.getInvoiceById(aid.getAppliedToInvoiceId());
                if (invoice != null) {
                    BigDecimal aidAmount = aid.getAmount();
                    if (aidAmount == null) {
                        aidAmount = BigDecimal.ZERO;
                    }
                    
                    BigDecimal currentFinancialAid = invoice.getFinancialAidAmount();
                    if (currentFinancialAid == null) {
                        currentFinancialAid = BigDecimal.ZERO;
                    }
                    
                    invoice.setFinancialAidAmount(currentFinancialAid.subtract(aidAmount));
                    invoice.calculateOutstandingBalance();
                    em.merge(invoice);
                    
                    // Create reversal entry in account statement
                    AccountStatement reversal = new AccountStatement();
                    reversal.setStudentId(aid.getStudentId());
                    reversal.setTransactionType(AccountStatement.TransactionType.REVERSAL);
                    reversal.setReferenceId(aid.getId());
                    reversal.setReferenceType(AccountStatement.ReferenceType.FINANCIAL_AID);
                    reversal.setDescription("Financial Aid Revoked - " + aid.getAidType() + 
                        (aid.getName() != null ? " (" + aid.getName() + ")" : "") + " - Reason: " + reason);
                    // REVERSAL = money OUT (taking back aid) = CREDIT
                    reversal.setDebitAmount(BigDecimal.ZERO);
                    reversal.setCreditAmount(aidAmount);
                    reversal.setAcademicYear(aid.getAcademicYear());
                    reversal.setSemester(aid.getSemester());
                    reversal.setRecordedBy("SYSTEM");
                    
                    // Balance: +balance = has money (can refund), -balance = owes university
                    // REVERSAL (CREDIT) = money OUT → balance decreases
                    BigDecimal previousBalance = calculatePreviousBalance(aid.getStudentId());
                    reversal.setRunningBalance(previousBalance.subtract(aidAmount)); // CREDIT decreases balance
                    
                    accountStatementService.createStatementEntry(reversal);
                }
            }
            
            em.merge(aid);
            JpaUtil.commitTransaction();
            return aid;
        } catch (RuntimeException e) {
            JpaUtil.rollbackTransaction();
            throw e;
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to revoke financial aid: " + e.getMessage(), e);
        }
    }

    @Override
    public FinancialAid updateFinancialAid(FinancialAid financialAid) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.beginTransaction();
            financialAid = em.merge(financialAid);
            JpaUtil.commitTransaction();
            return financialAid;
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to update financial aid: " + e.getMessage(), e);
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
