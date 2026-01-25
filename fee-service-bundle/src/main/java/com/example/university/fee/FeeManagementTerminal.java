package com.example.university.fee;

import com.example.university.fee.model.*;
import com.example.university.fee.service.*;
import com.example.university.fee.service.impl.*;
import com.example.university.fee.util.JpaUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Interactive Terminal Application for Fee & Billing Management
 * Implements UC-23 to UC-29
 * Connected to Supabase PostgreSQL database
 */
public class FeeManagementTerminal {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    private final FeeStructureService feeStructureService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final FinancialAidService financialAidService;
    private final RefundService refundService;
    private final AccountStatementService accountStatementService;
    
    public FeeManagementTerminal() {
        // Initialize services
        feeStructureService = new FeeStructureServiceImpl();
        invoiceService = new InvoiceServiceImpl();
        paymentService = new PaymentServiceImpl();
        financialAidService = new FinancialAidServiceImpl();
        refundService = new RefundServiceImpl();
        accountStatementService = new AccountStatementServiceImpl();
        
        // Wire dependencies
        ((InvoiceServiceImpl) invoiceService).setFeeStructureService(feeStructureService);
        ((InvoiceServiceImpl) invoiceService).setAccountStatementService(accountStatementService);
        ((PaymentServiceImpl) paymentService).setInvoiceService(invoiceService);
        ((PaymentServiceImpl) paymentService).setAccountStatementService(accountStatementService);
        ((FinancialAidServiceImpl) financialAidService).setInvoiceService(invoiceService);
        ((FinancialAidServiceImpl) financialAidService).setAccountStatementService(accountStatementService);
        ((RefundServiceImpl) refundService).setInvoiceService(invoiceService);
        ((RefundServiceImpl) refundService).setAccountStatementService(accountStatementService);
    }
    
    public static void main(String[] args) {
        // Suppress ALL logs - only show user-facing messages
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");
        System.setProperty("org.slf4j.simpleLogger.log.org.hibernate", "off");
        System.setProperty("org.slf4j.simpleLogger.log.org.postgresql", "off");
        System.setProperty("org.slf4j.simpleLogger.log.org.hibernate.engine", "off");
        System.setProperty("org.slf4j.simpleLogger.log.org.hibernate.jdbc", "off");
        System.setProperty("org.slf4j.simpleLogger.log.org.hibernate.connection", "off");
        System.setProperty("org.slf4j.simpleLogger.log.org.hibernate.SQL", "off");
        System.setProperty("org.slf4j.simpleLogger.log.org.hibernate.type", "off");
        System.setProperty("org.slf4j.simpleLogger.log.com.example.university.fee", "off");
        
        // Suppress java.util.logging for Hibernate
        java.util.logging.Logger hibernateLogger = java.util.logging.Logger.getLogger("org.hibernate");
        hibernateLogger.setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger postgresLogger = java.util.logging.Logger.getLogger("org.postgresql");
        postgresLogger.setLevel(java.util.logging.Level.OFF);
        
        FeeManagementTerminal terminal = null;
        try {
            terminal = new FeeManagementTerminal();
            terminal.run();
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
        } finally {
            // Cleanup JPA resources
            if (terminal != null) {
                JpaUtil.shutdown();
            }
        }
    }
    
    public void run() {
        System.out.println("========================================");
        System.out.println("University Fee & Billing Management");
        System.out.println("Interactive Terminal Application");
        System.out.println("========================================");
        System.out.println();
        
        while (true) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            
            try {
                switch (choice) {
                    case "1":
                        handleConfigureFee();
                        break;
                    case "2":
                        handleGenerateInvoice();
                        break;
                    case "3":
                        handleCalculateTuition();
                        break;
                    case "4":
                        handleManageFinancialAid();
                        break;
                    case "5":
                        handleTrackPayments();
                        break;
                    case "6":
                        handleProcessRefund();
                        break;
                    case "7":
                        handleViewAccountStatement();
                        break;
                    case "0":
                        System.out.println("Exiting...");
                        JpaUtil.shutdown();
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
    
    private void printMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. UC-23: Configure Tuition Fee");
        System.out.println("2. UC-24: Generate Invoices");
        System.out.println("3. UC-25: Calculate Tuition");
        System.out.println("4. UC-26: Manage Financial Aid");
        System.out.println("5. UC-27: Track Payments & Outstanding Fees");
        System.out.println("6. UC-28: Process Refund");
        System.out.println("7. UC-29: View Account Statements");
        System.out.println("0. Exit");
        System.out.print("\nEnter your choice: ");
    }
    
    // UC-23: Configure Tuition Fee
    private void handleConfigureFee() {
        System.out.println("\n=== UC-23: Configure Tuition Fee ===");
        System.out.println("1. Create Fee Structure");
        System.out.println("2. List Fee Structures");
        System.out.println("3. View Fee Structure");
        System.out.println("4. Delete Fee Structure");
        System.out.print("Choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                createFeeStructure();
                break;
            case "2":
                listFeeStructures();
                break;
            case "3":
                viewFeeStructure();
                break;
            case "4":
                deleteFeeStructure();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private void createFeeStructure() {
        System.out.println("\n--- Create Fee Structure ---");
        
        System.out.print("Academic Year (e.g., 2024-2025): ");
        String academicYear = scanner.nextLine().trim();
        
        System.out.print("Department (e.g., Engineering, CS): ");
        String department = scanner.nextLine().trim();
        
        System.out.print("Student Type (e.g., Undergraduate, Graduate): ");
        String studentType = scanner.nextLine().trim();
        
        System.out.print("Cost Per Credit: ");
        BigDecimal costPerCredit = readBigDecimal();
        
        // Check if exists
        FeeStructure existing = feeStructureService.getFeeStructure(academicYear, department, studentType);
        if (existing != null) {
            System.out.print("Fee structure already exists. Overwrite? (y/n): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                return;
            }
        }
        
        FeeStructure feeStructure = new FeeStructure();
        feeStructure.setAcademicYear(academicYear);
        feeStructure.setDepartment(department);
        feeStructure.setStudentType(studentType);
        feeStructure.setCostPerCredit(costPerCredit);
        feeStructure.setStatus(FeeStructure.FeeStructureStatus.ACTIVE);
        feeStructure.setCreatedBy("Officer");
        
        // Add fixed fees
        System.out.println("\nAdd Fixed Fee Items (press Enter with empty name to finish):");
        while (true) {
            System.out.print("Fee Item Name (or Enter to finish): ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) break;
            
            System.out.print("Amount: ");
            BigDecimal amount = readBigDecimal();
            
            FeeItem feeItem = new FeeItem();
            feeItem.setName(name);
            feeItem.setAmount(amount);
            feeItem.setFeeType(FeeItem.FeeItemType.FIXED);
            feeStructure.addFeeItem(feeItem);
        }
        
        feeStructure = feeStructureService.saveFeeStructure(feeStructure);
        System.out.println("\nFee structure created successfully! ID: " + feeStructure.getId());
    }
    
    private void listFeeStructures() {
        System.out.println("\n--- Fee Structures ---");
        List<FeeStructure> structures = feeStructureService.getAllFeeStructures();
        if (structures.isEmpty()) {
            System.out.println("No fee structures found.");
            return;
        }
        
        for (FeeStructure fs : structures) {
            System.out.printf("ID: %d | %s | %s | %s | Cost/Credit: $%.2f | Status: %s%n",
                fs.getId(), fs.getAcademicYear(), fs.getDepartment(), 
                fs.getStudentType(), fs.getCostPerCredit(), fs.getStatus());
        }
    }
    
    private void viewFeeStructure() {
        System.out.print("\nEnter Fee Structure ID: ");
        Long id = readLong();
        FeeStructure fs = feeStructureService.getFeeStructureById(id);
        
        if (fs == null) {
            System.out.println("Fee structure not found.");
            return;
        }
        
        System.out.println("\n=== Fee Structure Details ===");
        System.out.println("ID: " + fs.getId());
        System.out.println("Academic Year: " + fs.getAcademicYear());
        System.out.println("Department: " + fs.getDepartment());
        System.out.println("Student Type: " + fs.getStudentType());
        System.out.println("Cost Per Credit: $" + fs.getCostPerCredit());
        System.out.println("Status: " + fs.getStatus());
        System.out.println("\nFixed Fees:");
        for (FeeItem item : fs.getFeeItems()) {
            System.out.printf("  - %s: $%.2f%n", item.getName(), item.getAmount());
        }
        System.out.println("Total Fixed Fees: $" + fs.getTotalFixedFees());
    }
    
    private void deleteFeeStructure() {
        System.out.print("\nEnter Fee Structure ID to delete: ");
        Long id = readLong();
        feeStructureService.deleteFeeStructure(id);
        System.out.println("\nFee structure deleted successfully.");
    }
    
    // UC-24: Generate Invoices
    private void handleGenerateInvoice() {
        System.out.println("\n=== UC-24: Generate Invoices ===");
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        
        System.out.print("Academic Year: ");
        String academicYear = scanner.nextLine().trim();
        
        System.out.print("Semester: ");
        String semester = scanner.nextLine().trim();
        
        System.out.print("Department: ");
        String department = scanner.nextLine().trim();
        
        System.out.print("Student Type: ");
        String studentType = scanner.nextLine().trim();
        
        System.out.print("Total Credits: ");
        Integer credits = readInteger();
        
        try {
            Invoice invoice = invoiceService.generateInvoice(studentId, academicYear, semester, 
                                                             department, studentType, credits);
            
            System.out.println("\nInvoice successfully generated!");
            System.out.println("Invoice Number: " + invoice.getInvoiceNumber());
            System.out.println("Student ID: " + invoice.getStudentId());
            System.out.println("Total Amount: $" + invoice.getTotalAmount());
            System.out.println("Status: " + invoice.getStatus());
            System.out.println("Due Date: " + invoice.getDueDate());
            
            if (invoice.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
                System.out.println("Note: Statement of Account marked as SETTLED (fully funded student)");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // UC-25: Calculate Tuition
    private void handleCalculateTuition() {
        System.out.println("\n=== UC-25: Calculate Tuition ===");
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        
        System.out.print("Academic Year: ");
        String academicYear = scanner.nextLine().trim();
        
        System.out.print("Semester: ");
        String semester = scanner.nextLine().trim();
        
        System.out.print("Department: ");
        String department = scanner.nextLine().trim();
        
        System.out.print("Student Type: ");
        String studentType = scanner.nextLine().trim();
        
        System.out.print("Total Credits: ");
        Integer credits = readInteger();
        
        try {
            BigDecimal total = invoiceService.calculateTuition(studentId, academicYear, semester, 
                                                               department, studentType, credits);
            
            FeeStructure fs = feeStructureService.getFeeStructure(academicYear, department, studentType);
            if (fs != null) {
                BigDecimal tuition = fs.getCostPerCredit().multiply(BigDecimal.valueOf(credits));
                BigDecimal fixedFees = fs.getTotalFixedFees();
                
                System.out.println("\n=== Tuition Calculation ===");
                System.out.println("Credits: " + credits);
                System.out.println("Cost Per Credit: $" + fs.getCostPerCredit());
                System.out.println("Tuition Amount: $" + tuition);
                System.out.println("Fixed Fees: $" + fixedFees);
                System.out.println("Total Payable: $" + total);
            } else {
                System.out.println("Total Payable: $" + total);
            }
        } catch (Exception e) {
            System.out.println("Error calculating tuition: " + e.getMessage());
            System.out.println("Fee structure not found. Student record flagged for manual review.");
        }
    }
    
    // UC-26: Manage Financial Aid
    private void handleManageFinancialAid() {
        System.out.println("\n=== UC-26: Manage Financial Aid ===");
        System.out.println("1. Create Financial Aid");
        System.out.println("2. Allocate Financial Aid (Batch)");
        System.out.println("3. Apply Financial Aid to Student");
        System.out.println("4. List Financial Aid by Student");
        System.out.println("5. Revoke Financial Aid");
        System.out.print("Choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                createFinancialAid();
                break;
            case "2":
                allocateFinancialAid();
                break;
            case "3":
                applyFinancialAid();
                break;
            case "4":
                listFinancialAid();
                break;
            case "5":
                revokeFinancialAid();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private void createFinancialAid() {
        System.out.println("\n--- Create Financial Aid ---");
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        
        System.out.print("Aid Type (SCHOLARSHIP, GRANT, WAIVER, LOAN, BURSARY, SPONSORSHIP): ");
        FinancialAid.AidType aidType = FinancialAid.AidType.valueOf(scanner.nextLine().trim().toUpperCase());
        
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Amount: ");
        BigDecimal amount = readBigDecimal();
        
        System.out.print("Academic Year: ");
        String academicYear = scanner.nextLine().trim();
        
        System.out.print("Semester: ");
        String semester = scanner.nextLine().trim();
        
        System.out.print("Min GPA Required (optional, press Enter to skip): ");
        String gpaStr = scanner.nextLine().trim();
        BigDecimal minGpa = gpaStr.isEmpty() ? null : new BigDecimal(gpaStr);
        
        System.out.print("Is Refundable? (y/n): ");
        boolean isRefundable = scanner.nextLine().trim().equalsIgnoreCase("y");
        
        FinancialAid aid = new FinancialAid();
        aid.setStudentId(studentId);
        aid.setAidType(aidType);
        aid.setName(name);
        aid.setAmount(amount);
        aid.setAcademicYear(academicYear);
        aid.setSemester(semester);
        aid.setMinGpaRequired(minGpa);
        aid.setIsRefundable(isRefundable);
        aid.setStatus(FinancialAid.AidStatus.PENDING);
        
            aid = financialAidService.createFinancialAid(aid);
        System.out.println("\nFinancial aid created successfully! ID: " + aid.getId());
    }
    
    private void allocateFinancialAid() {
        System.out.println("\n--- Allocate Financial Aid (Batch) ---");
        try {
            List<FinancialAid> aids = financialAidService.allocateFinancialAid();
            if (aids.isEmpty()) {
                System.out.println("\nNo pending financial aid records to allocate.");
            } else {
                System.out.println("\nFinancial aid allocation completed. Processed " + aids.size() + " records.");
                int appliedCount = 0;
                for (FinancialAid aid : aids) {
                    if (aid.getStatus() == FinancialAid.AidStatus.APPLIED) {
                        appliedCount++;
                    }
                }
                System.out.println("Successfully applied: " + appliedCount + " financial aid(s).");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void applyFinancialAid() {
        System.out.println("\n--- Apply Financial Aid to Student ---");
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        
        System.out.print("Academic Year: ");
        String academicYear = scanner.nextLine().trim();
        
        System.out.print("Semester: ");
        String semester = scanner.nextLine().trim();
        
        try {
            FinancialAid aid = financialAidService.applyFinancialAidToStudent(studentId, academicYear, semester);
            if (aid != null) {
                System.out.println("\nFinancial aid applied successfully!");
                System.out.println("Aid: " + aid.getName() + " - $" + aid.getAmount());
                
                // Show updated invoice status
                List<Invoice> invoices = invoiceService.getInvoicesByStudent(studentId);
                Invoice targetInvoice = invoices.stream()
                    .filter(inv -> inv.getAcademicYear() != null && inv.getAcademicYear().equals(academicYear) &&
                                  inv.getSemester() != null && inv.getSemester().equals(semester))
                    .findFirst()
                    .orElse(null);
                
                if (targetInvoice != null) {
                    System.out.println("Invoice Outstanding Balance: $" + targetInvoice.getOutstandingBalance());
                    System.out.println("Invoice Status: " + targetInvoice.getStatus());
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void listFinancialAid() {
        System.out.print("\nStudent ID: ");
        String studentId = scanner.nextLine().trim();
        
        try {
            List<FinancialAid> aids = financialAidService.getFinancialAidByStudent(studentId);
            if (aids.isEmpty()) {
                System.out.println("No financial aid records found.");
                return;
            }
            
            System.out.println("\n=== Financial Aid Records ===");
            System.out.printf("%-5s %-30s %-12s %-15s %-12s %-10s%n", 
                "ID", "Name", "Amount", "Type", "Year/Semester", "Status");
            System.out.println("--------------------------------------------------------------------------------");
            for (FinancialAid aid : aids) {
                String yearSem = (aid.getAcademicYear() != null ? aid.getAcademicYear() : "") + 
                                (aid.getSemester() != null ? "/" + aid.getSemester() : "");
                System.out.printf("%-5d %-30s $%-11.2f %-15s %-12s %-10s%n",
                    aid.getId(), 
                    aid.getName() != null ? aid.getName() : "N/A",
                    aid.getAmount() != null ? aid.getAmount() : BigDecimal.ZERO,
                    aid.getAidType(),
                    yearSem,
                    aid.getStatus());
                
                if (aid.getStatus() == FinancialAid.AidStatus.REVOKED && aid.getRevokedReason() != null) {
                    System.out.println("      Revoked Reason: " + aid.getRevokedReason());
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void revokeFinancialAid() {
        System.out.print("\nFinancial Aid ID: ");
        Long id = readLong();
        
        System.out.print("Reason: ");
        String reason = scanner.nextLine().trim();
        
        if (reason.isEmpty()) {
            System.out.println("Error: Reason is required for revoking financial aid.");
            return;
        }
        
        try {
            FinancialAid aid = financialAidService.revokeFinancialAid(id, reason);
            if (aid != null) {
                System.out.println("\nFinancial aid revoked successfully.");
                System.out.println("Aid: " + aid.getName() + " - $" + aid.getAmount());
                
                // Show updated invoice status if aid was applied
                if (aid.getAppliedToInvoiceId() != null) {
                    Invoice invoice = invoiceService.getInvoiceById(aid.getAppliedToInvoiceId());
                    if (invoice != null) {
                        System.out.println("Invoice Outstanding Balance: $" + invoice.getOutstandingBalance());
                        System.out.println("Invoice Status: " + invoice.getStatus());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // UC-27: Track Payments & Outstanding Fees
    private void handleTrackPayments() {
        System.out.println("\n=== UC-27: Track Payments & Outstanding Fees ===");
        System.out.println("1. Record Payment");
        System.out.println("2. View Payments by Student");
        System.out.println("3. Outstanding Fees Report");
        System.out.print("Choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                recordPayment();
                break;
            case "2":
                viewPayments();
                break;
            case "3":
                outstandingFeesReport();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private void recordPayment() {
        System.out.println("\n--- Record Payment ---");
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        
        System.out.print("Amount: ");
        BigDecimal amount = readBigDecimal();
        
        System.out.print("Reference Number: ");
        String refNumber = scanner.nextLine().trim();
        
        System.out.print("Payment Method (BANK_TRANSFER, CHECK, CASH, ONLINE): ");
        Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(scanner.nextLine().trim().toUpperCase());
        
        System.out.print("Recorded By: ");
        String recordedBy = scanner.nextLine().trim();
        
        Payment payment = new Payment();
        payment.setStudentId(studentId);
        payment.setAmount(amount);
        payment.setReferenceNumber(refNumber);
        payment.setPaymentMethod(method);
        payment.setRecordedBy(recordedBy);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        
        try {
            payment = paymentService.recordPayment(payment);
            
            // Update invoice if applicable
            List<Invoice> invoices = invoiceService.getInvoicesByStudent(studentId);
            Invoice targetInvoice = invoices.stream()
                .filter(inv -> inv.getOutstandingBalance().compareTo(amount) >= 0)
                .findFirst()
                .orElse(null);
            
            if (targetInvoice != null) {
                if (amount.compareTo(targetInvoice.getOutstandingBalance()) < 0) {
                    System.out.println("\nPayment recorded successfully! Status: PARTIALLY PAID");
                } else {
                    System.out.println("\nPayment recorded successfully! Status: PAID");
                }
            } else {
                System.out.println("\nPayment recorded successfully! ID: " + payment.getId());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void viewPayments() {
        System.out.print("\nStudent ID: ");
        String studentId = scanner.nextLine().trim();
        
        List<Payment> payments = paymentService.getPaymentsByStudent(studentId);
        if (payments.isEmpty()) {
            System.out.println("No payments found.");
            return;
        }
        
        System.out.println("\n=== Payment History ===");
        for (Payment p : payments) {
            System.out.printf("ID: %d | Date: %s | Amount: $%.2f | Ref: %s | Method: %s | Status: %s%n",
                p.getId(), p.getPaymentDate(), p.getAmount(), p.getReferenceNumber(), 
                p.getPaymentMethod(), p.getStatus());
        }
    }
    
    private void outstandingFeesReport() {
        System.out.println("\n=== Outstanding Fees Report ===");
        List<Object[]> report = paymentService.getOutstandingFeesReport();
        
        if (report.isEmpty()) {
            System.out.println("No outstanding fees.");
            return;
        }
        
        System.out.printf("%-15s %-20s%n", "Student ID", "Outstanding Balance");
        System.out.println("----------------------------------------");
        for (Object[] row : report) {
            System.out.printf("%-15s $%-19.2f%n", row[0], row[1]);
        }
    }
    
    // UC-28: Process Refund
    private void handleProcessRefund() {
        System.out.println("\n=== UC-28: Process Refund ===");
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        
        try {
            BigDecimal creditBalance = refundService.getCreditBalance(studentId);
            System.out.println("Current Credit Balance: $" + creditBalance);
            
            if (creditBalance.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("No credit balance available for refund.");
                
                // Show account statement balance for debugging
                List<AccountStatement> statements = accountStatementService.getAccountStatement(studentId);
                if (!statements.isEmpty()) {
                    BigDecimal runningBalance = statements.get(0).getRunningBalance();
                    if (runningBalance != null) {
                        System.out.println("Account Statement Running Balance: $" + runningBalance);
                        if (runningBalance.compareTo(BigDecimal.ZERO) < 0) {
                            System.out.println("Note: Negative balance indicates credit. Checking AccountStatement...");
                        }
                    }
                }
                return;
            }
        
            System.out.print("Refund Amount: ");
            BigDecimal amount = readBigDecimal();
            
            if (amount.compareTo(creditBalance) > 0) {
                System.out.println("Error: Refund amount ($" + amount + ") exceeds available credit balance ($" + creditBalance + ")");
                return;
            }
            
            System.out.print("Reason: ");
            String reason = scanner.nextLine().trim();
            
            System.out.print("Refund Type (BANK_TRANSFER, CHECK, TRANSFER_NEXT_SEMESTER, CREDIT_TO_ACCOUNT): ");
            Refund.RefundType refundType = Refund.RefundType.valueOf(scanner.nextLine().trim().toUpperCase());
            
            String holdingSemester = null;
            if (refundType == Refund.RefundType.TRANSFER_NEXT_SEMESTER) {
                System.out.print("Holding Semester: ");
                holdingSemester = scanner.nextLine().trim();
            }
            
            Refund refund = refundService.processRefund(studentId, amount, reason, refundType, holdingSemester);
            System.out.println("\nRefund created successfully! ID: " + refund.getId());
            System.out.println("Status: " + refund.getStatus());
            System.out.println("Amount: $" + refund.getAmount());
            
            if (refundType == Refund.RefundType.TRANSFER_NEXT_SEMESTER) {
                System.out.println("Credit transferred to next semester: " + holdingSemester);
            }
            
            // Show updated credit balance
            BigDecimal newCreditBalance = refundService.getCreditBalance(studentId);
            System.out.println("Remaining Credit Balance: $" + newCreditBalance);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid refund type. Please use: BANK_TRANSFER, CHECK, TRANSFER_NEXT_SEMESTER, or CREDIT_TO_ACCOUNT");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // UC-29: View Account Statements
    private void handleViewAccountStatement() {
        System.out.println("\n=== UC-29: View Account Statements ===");
        System.out.println("1. View Full Statement");
        System.out.println("2. View by Term");
        System.out.println("3. View by Date Range");
        System.out.println("4. Generate Tax Form");
        System.out.print("Choice: ");
        
        String choice = scanner.nextLine().trim();
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        
        List<AccountStatement> statements = null;
        
        switch (choice) {
            case "1":
                statements = accountStatementService.getAccountStatement(studentId);
                break;
            case "2":
                System.out.print("Academic Year: ");
                String academicYear = scanner.nextLine().trim();
                System.out.print("Semester: ");
                String semester = scanner.nextLine().trim();
                statements = accountStatementService.getAccountStatementByTerm(studentId, academicYear, semester);
                break;
            case "3":
                System.out.print("Start Date (YYYY-MM-DD): ");
                LocalDate startDate = readDate();
                System.out.print("End Date (YYYY-MM-DD): ");
                LocalDate endDate = readDate();
                statements = accountStatementService.getAccountStatementByDateRange(studentId, startDate, endDate);
                break;
            case "4":
                System.out.print("Tax Year (YYYY): ");
                String taxYear = scanner.nextLine().trim();
                statements = accountStatementService.generateTaxForm(studentId, taxYear);
                if (statements.isEmpty()) {
                    System.out.println("No records found for tax year: " + taxYear);
                    return;
                }
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        if (statements == null || statements.isEmpty()) {
            System.out.println("No statements found.");
            return;
        }
        
        System.out.println("\n=== Account Statement ===");
        System.out.printf("%-12s %-50s %-15s %-15s %-15s%n", 
            "Date", "Description", "Debit", "Credit", "Balance");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        
        // Use the stored runningBalance from each statement (already calculated correctly)
        // Statements are now ordered chronologically (oldest first)
        for (AccountStatement stmt : statements) {
            BigDecimal balance = stmt.getRunningBalance() != null ? stmt.getRunningBalance() : BigDecimal.ZERO;
            System.out.printf("%-12s %-50s $%-14.2f $%-14.2f $%-14.2f%n",
                stmt.getTransactionDate().toLocalDate(),
                stmt.getDescription(),
                stmt.getDebitAmount() != null ? stmt.getDebitAmount() : BigDecimal.ZERO,
                stmt.getCreditAmount() != null ? stmt.getCreditAmount() : BigDecimal.ZERO,
                balance);
        }
        
        // Show summary
        if (!statements.isEmpty()) {
            AccountStatement lastStmt = statements.get(statements.size() - 1);
            BigDecimal finalBalance = lastStmt.getRunningBalance() != null ? lastStmt.getRunningBalance() : BigDecimal.ZERO;
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.printf("%-12s %-50s %-15s %-15s %-15s%n", 
                "", "FINAL BALANCE", "", "", "$" + finalBalance);
            
            // Explain balance meaning
            // Balance: +balance = has money (can refund), -balance = owes university
            if (finalBalance.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("\nNote: Positive balance ($" + finalBalance + ") = Student has CREDIT of $" + finalBalance + " and can request a refund.");
            } else if (finalBalance.compareTo(BigDecimal.ZERO) < 0) {
                System.out.println("\nNote: Negative balance ($" + finalBalance + ") = Student OWES $" + finalBalance.abs() + " to the university.");
            } else {
                System.out.println("\nNote: Balance is zero - account is settled.");
            }
        }
    }
    
    // Helper methods
    private BigDecimal readBigDecimal() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.print("Invalid Currency Format. Please enter a positive number: ");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Invalid Currency Format. Please enter a valid number: ");
            }
        }
    }
    
    private Long readLong() {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please try again: ");
            }
        }
    }
    
    private Integer readInteger() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please try again: ");
            }
        }
    }
    
    private LocalDate readDate() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException e) {
                System.out.print("Invalid date format. Please use YYYY-MM-DD: ");
            }
        }
    }
}
