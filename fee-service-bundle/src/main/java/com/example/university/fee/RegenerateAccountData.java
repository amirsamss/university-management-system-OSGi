package com.example.university.fee;

import com.example.university.fee.model.*;
import com.example.university.fee.service.*;
import com.example.university.fee.service.impl.*;
import com.example.university.fee.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Script to clear existing AccountStatement data and regenerate transactions with correct debit/credit logic
 */
public class RegenerateAccountData {
    
    public static void main(String[] args) {
        String studentId = "STU001";
        
        System.out.println("=== Regenerating Account Data for " + studentId + " ===");
        
        // Step 1: Clear existing AccountStatement entries
        clearAccountStatements(studentId);
        System.out.println("✓ Cleared existing account statements");
        
        // Step 2: Regenerate transactions in correct order
        try {
            // Initialize services
            FeeStructureService feeStructureService = new FeeStructureServiceImpl();
            InvoiceService invoiceService = new InvoiceServiceImpl();
            PaymentService paymentService = new PaymentServiceImpl();
            FinancialAidService financialAidService = new FinancialAidServiceImpl();
            RefundService refundService = new RefundServiceImpl();
            AccountStatementService accountStatementService = new AccountStatementServiceImpl();
            
            // Inject dependencies
            ((InvoiceServiceImpl) invoiceService).setFeeStructureService(feeStructureService);
            ((InvoiceServiceImpl) invoiceService).setAccountStatementService(accountStatementService);
            ((PaymentServiceImpl) paymentService).setInvoiceService(invoiceService);
            ((PaymentServiceImpl) paymentService).setAccountStatementService(accountStatementService);
            ((FinancialAidServiceImpl) financialAidService).setInvoiceService(invoiceService);
            ((FinancialAidServiceImpl) financialAidService).setAccountStatementService(accountStatementService);
            ((RefundServiceImpl) refundService).setInvoiceService(invoiceService);
            ((RefundServiceImpl) refundService).setAccountStatementService(accountStatementService);
            
            EntityManager em = JpaUtil.getEntityManager();
            
            // Transaction 1: Payment - ONLINE (Ref: PAY-2026-003) - $6000
            Payment payment1 = new Payment();
            payment1.setStudentId(studentId);
            payment1.setAmount(new BigDecimal("6000.00"));
            payment1.setPaymentMethod(Payment.PaymentMethod.ONLINE);
            payment1.setReferenceNumber("PAY-2026-003");
            payment1.setPaymentDate(LocalDate.now());
            paymentService.recordPayment(payment1);
            System.out.println("✓ Payment PAY-2026-003: $6000.00");
            
            // Transaction 2: Payment - CHECK (Ref: PAY-2026-004) - $6000
            Payment payment2 = new Payment();
            payment2.setStudentId(studentId);
            payment2.setAmount(new BigDecimal("6000.00"));
            payment2.setPaymentMethod(Payment.PaymentMethod.CHECK);
            payment2.setReferenceNumber("PAY-2026-004");
            payment2.setPaymentDate(LocalDate.now());
            paymentService.recordPayment(payment2);
            System.out.println("✓ Payment PAY-2026-004: $6000.00");
            
            // Transaction 3: Invoice INV-8744FAF8 - 2 2025/2026 - $3000
            Invoice invoice1 = createInvoiceDirectly(em, studentId, "INV-8744FAF8", "2025/2026", "2", new BigDecimal("3000.00"), accountStatementService);
            System.out.println("✓ Invoice INV-8744FAF8: $3000.00");
            
            // Transaction 4: Invoice INV-BEE32092 - Fall 2024-2025 - $7750
            Invoice invoice2 = createInvoiceDirectly(em, studentId, "INV-BEE32092", "2024-2025", "Fall", new BigDecimal("7750.00"), accountStatementService);
            System.out.println("✓ Invoice INV-BEE32092: $7750.00");
            
            // Transaction 5: Payment - ONLINE (Ref: PAY-TEST-001) - $5000
            Payment payment3 = new Payment();
            payment3.setStudentId(studentId);
            payment3.setAmount(new BigDecimal("5000.00"));
            payment3.setPaymentMethod(Payment.PaymentMethod.ONLINE);
            payment3.setReferenceNumber("PAY-TEST-001");
            payment3.setPaymentDate(LocalDate.now());
            paymentService.recordPayment(payment3);
            System.out.println("✓ Payment PAY-TEST-001: $5000.00");
            
            // Transaction 6: Invoice INV-D242EE49 - Spring 2024-2025 - $6250
            Invoice invoice3 = createInvoiceDirectly(em, studentId, "INV-D242EE49", "2024-2025", "Spring", new BigDecimal("6250.00"), accountStatementService);
            System.out.println("✓ Invoice INV-D242EE49: $6250.00");
            
            // Transaction 7: Payment - BANK_TRANSFER (Ref: PAY-TEST-002) - $20000 (with $3000 overpayment)
            Payment payment4 = new Payment();
            payment4.setStudentId(studentId);
            payment4.setAmount(new BigDecimal("20000.00"));
            payment4.setPaymentMethod(Payment.PaymentMethod.BANK_TRANSFER);
            payment4.setReferenceNumber("PAY-TEST-002");
            payment4.setInvoice(invoice1); // Apply to first invoice
            payment4.setPaymentDate(LocalDate.now());
            paymentService.recordPayment(payment4);
            System.out.println("✓ Payment PAY-TEST-002: $20000.00 (with $3000 overpayment)");
            
            // Transaction 8: Invoice INV-28F7AB9A - 2 2025/2026 - $2900
            Invoice invoice4 = createInvoiceDirectly(em, studentId, "INV-28F7AB9A", "2025/2026", "2", new BigDecimal("2900.00"), accountStatementService);
            System.out.println("✓ Invoice INV-28F7AB9A: $2900.00");
            
            // Transaction 9: Financial Aid - SCHOLARSHIP (Yayasan Tenaga Nasional (YTN)) - $5000
            FinancialAid aid1 = new FinancialAid();
            aid1.setStudentId(studentId);
            aid1.setAidType(FinancialAid.AidType.SCHOLARSHIP);
            aid1.setName("Yayasan Tenaga Nasional (YTN)");
            aid1.setAmount(new BigDecimal("5000.00"));
            aid1.setAcademicYear("2025/2026");
            aid1.setSemester("2");
            aid1.setStatus(FinancialAid.AidStatus.PENDING);
            financialAidService.createFinancialAid(aid1);
            // Then apply it
            financialAidService.applyFinancialAidToStudent(studentId, "2025/2026", "2");
            System.out.println("✓ Financial Aid YTN: $5000.00");
            
            // Transaction 10: Invoice INV-31D9E7A4 - 2 2025/2026 - $2900
            Invoice invoice5 = createInvoiceDirectly(em, studentId, "INV-31D9E7A4", "2025/2026", "2", new BigDecimal("2900.00"), accountStatementService);
            System.out.println("✓ Invoice INV-31D9E7A4: $2900.00");
            
            // Transaction 11: Invoice INV-C75573BA - 1 2025/2026 - $2900
            Invoice invoice6 = createInvoiceDirectly(em, studentId, "INV-C75573BA", "2025/2026", "1", new BigDecimal("2900.00"), accountStatementService);
            System.out.println("✓ Invoice INV-C75573BA: $2900.00");
            
            // Transaction 12: Financial Aid - SCHOLARSHIP (Yayasan Khazanah) - $2900
            FinancialAid aid2 = new FinancialAid();
            aid2.setStudentId(studentId);
            aid2.setAidType(FinancialAid.AidType.SCHOLARSHIP);
            aid2.setName("Yayasan Khazanah");
            aid2.setAmount(new BigDecimal("2900.00"));
            aid2.setAcademicYear("2025/2026");
            aid2.setSemester("1");
            aid2.setStatus(FinancialAid.AidStatus.PENDING);
            financialAidService.createFinancialAid(aid2);
            // Then apply it
            financialAidService.applyFinancialAidToStudent(studentId, "2025/2026", "1");
            System.out.println("✓ Financial Aid Yayasan Khazanah: $2900.00");
            
            // Transaction 13: Financial Aid Revoked - SCHOLARSHIP (Yayasan Khazanah) - $2900
            java.util.List<FinancialAid> aids = financialAidService.getFinancialAidByStudent(studentId);
            for (FinancialAid aid : aids) {
                if (aid.getName() != null && aid.getName().equals("Yayasan Khazanah") && 
                    aid.getStatus() == FinancialAid.AidStatus.APPLIED) {
                    financialAidService.revokeFinancialAid(aid.getId(), "Student's drop in performance");
                    System.out.println("✓ Financial Aid Revoked (Yayasan Khazanah): $2900.00");
                    break;
                }
            }
            
            // Transaction 14: Payment - ONLINE (Ref: PAY-2026-006) - $3000
            Payment payment5 = new Payment();
            payment5.setStudentId(studentId);
            payment5.setAmount(new BigDecimal("3000.00"));
            payment5.setPaymentMethod(Payment.PaymentMethod.ONLINE);
            payment5.setReferenceNumber("PAY-2026-006");
            payment5.setInvoice(invoice5); // Apply to invoice INV-31D9E7A4
            payment5.setPaymentDate(LocalDate.now());
            paymentService.recordPayment(payment5);
            System.out.println("✓ Payment PAY-2026-006: $3000.00");
            
            // Transaction 15: Payment - ONLINE (Ref: ) - Invoice: INV-C75573BA - $3000 (with $200 overpayment)
            Payment payment6 = new Payment();
            payment6.setStudentId(studentId);
            payment6.setAmount(new BigDecimal("3000.00"));
            payment6.setPaymentMethod(Payment.PaymentMethod.ONLINE);
            payment6.setReferenceNumber(""); // Empty reference
            payment6.setInvoice(invoice6); // Apply to invoice INV-C75573BA
            payment6.setPaymentDate(LocalDate.now());
            paymentService.recordPayment(payment6);
            System.out.println("✓ Payment (empty ref) for INV-C75573BA: $3000.00 (with $200 overpayment)");
            
            // Transaction 16: Refund - BANK_TRANSFER (overpayment) - $200
            BigDecimal creditBalance = refundService.getCreditBalance(studentId);
            if (creditBalance.compareTo(new BigDecimal("200.00")) >= 0) {
                refundService.processRefund(studentId, new BigDecimal("200.00"), 
                    "overpayment", Refund.RefundType.BANK_TRANSFER, null);
                System.out.println("✓ Refund: $200.00");
            } else {
                System.out.println("⚠ Not enough credit for refund of $200.00");
            }
            
            System.out.println("\n=== Regeneration Complete ===");
            System.out.println("Final Balance: " + getCurrentBalance(studentId));
            
        } catch (Exception e) {
            System.err.println("Error regenerating data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            JpaUtil.shutdown();
        }
    }
    
    private static Invoice createInvoiceDirectly(EntityManager em, String studentId, String invoiceNumber, 
                                                 String academicYear, String semester, BigDecimal amount,
                                                 AccountStatementService accountStatementService) {
        try {
            JpaUtil.beginTransaction();
            
            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setStudentId(studentId);
            invoice.setSemester(semester);
            invoice.setAcademicYear(academicYear);
            invoice.setTotalAmount(amount);
            invoice.setTuitionAmount(amount);
            invoice.setDueDate(LocalDate.now().plusDays(30));
            invoice.setStatus(Invoice.InvoiceStatus.UNPAID);
            invoice.calculateOutstandingBalance();
            
            em.persist(invoice);
            em.flush();
            
            // Create account statement entry
            AccountStatement statement = new AccountStatement();
            statement.setStudentId(studentId);
            statement.setTransactionType(AccountStatement.TransactionType.CHARGE);
            statement.setReferenceId(invoice.getId());
            statement.setReferenceType(AccountStatement.ReferenceType.INVOICE);
            statement.setDescription("Invoice " + invoiceNumber + " - " + semester + " " + academicYear);
            // Invoice = money OUT (student is charged) = CREDIT
            statement.setDebitAmount(BigDecimal.ZERO);
            statement.setCreditAmount(amount);
            statement.setAcademicYear(academicYear);
            statement.setSemester(semester);
            statement.setRecordedBy("SYSTEM");
            
            // Calculate running balance
            BigDecimal previousBalance = calculatePreviousBalance(studentId);
            statement.setRunningBalance(previousBalance.subtract(amount)); // CREDIT decreases balance
            
            accountStatementService.createStatementEntry(statement);
            
            JpaUtil.commitTransaction();
            return invoice;
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to create invoice: " + e.getMessage(), e);
        }
    }
    
    private static BigDecimal calculatePreviousBalance(String studentId) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<AccountStatement> query = em.createQuery(
            "SELECT s FROM AccountStatement s WHERE s.studentId = :studentId " +
            "ORDER BY s.transactionDate DESC, s.id DESC", AccountStatement.class);
        query.setParameter("studentId", studentId);
        query.setMaxResults(1);
        java.util.List<AccountStatement> results = query.getResultList();
        if (results.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal balance = results.get(0).getRunningBalance();
        return balance != null ? balance : BigDecimal.ZERO;
    }
    
    private static void clearAccountStatements(String studentId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.beginTransaction();
            TypedQuery<AccountStatement> query = em.createQuery(
                "SELECT s FROM AccountStatement s WHERE s.studentId = :studentId", 
                AccountStatement.class);
            query.setParameter("studentId", studentId);
            java.util.List<AccountStatement> statements = query.getResultList();
            for (AccountStatement stmt : statements) {
                em.remove(stmt);
            }
            JpaUtil.commitTransaction();
            System.out.println("Cleared " + statements.size() + " account statement entries");
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to clear account statements: " + e.getMessage(), e);
        }
    }
    
    private static BigDecimal getCurrentBalance(String studentId) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<AccountStatement> query = em.createQuery(
            "SELECT s FROM AccountStatement s WHERE s.studentId = :studentId " +
            "ORDER BY s.transactionDate DESC, s.id DESC", AccountStatement.class);
        query.setParameter("studentId", studentId);
        query.setMaxResults(1);
        java.util.List<AccountStatement> results = query.getResultList();
        if (results.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal balance = results.get(0).getRunningBalance();
        return balance != null ? balance : BigDecimal.ZERO;
    }
}
