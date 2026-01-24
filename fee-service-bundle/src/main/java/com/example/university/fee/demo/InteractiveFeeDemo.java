package com.example.university.fee.demo;

import com.example.university.fee.model.Payment;
import com.example.university.fee.model.Invoice;
import com.example.university.fee.model.InvoiceLineItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.SessionFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Fee Service Demo
 * Uses Supabase PostgreSQL database via JPA
 */
public class InteractiveFeeDemo {

    private EntityManagerFactory emf;
    private SessionFactory sessionFactory; // Store SessionFactory as backup
    private Scanner scanner;

    public InteractiveFeeDemo() {
        scanner = new Scanner(System.in);
        try {
            // Configure Hibernate using StandardServiceRegistry (native Hibernate API)
            StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
            registryBuilder.applySetting("hibernate.connection.driver_class", "org.postgresql.Driver");
            registryBuilder.applySetting("hibernate.connection.url", "jdbc:postgresql://aws-1-ap-northeast-2.pooler.supabase.com:5432/postgres?sslmode=require");
            registryBuilder.applySetting("hibernate.connection.username", "postgres.yopvpvgbgajkwadxevda");
            registryBuilder.applySetting("hibernate.connection.password", "WIF_3006_G2");
            registryBuilder.applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            registryBuilder.applySetting("hibernate.hbm2ddl.auto", "update");
            registryBuilder.applySetting("hibernate.show_sql", "false");
            registryBuilder.applySetting("hibernate.format_sql", "true");
            
            StandardServiceRegistry registry = registryBuilder.build();
            
            // Create MetadataSources and add entity classes
            MetadataSources sources = new MetadataSources(registry);
            sources.addAnnotatedClass(Payment.class);
            sources.addAnnotatedClass(Invoice.class);
            sources.addAnnotatedClass(InvoiceLineItem.class);
            
            // Build metadata and create SessionFactory
            var metadata = sources.getMetadataBuilder().build();
            SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
            
            // Store SessionFactory - we'll create EntityManagers from it
            this.sessionFactory = sessionFactory;
            
            // Create a simple EntityManagerFactory wrapper around SessionFactory
            emf = new EntityManagerFactory() {
                @Override
                public EntityManager createEntityManager() {
                    return sessionFactory.createEntityManager();
                }
                
                @Override
                public EntityManager createEntityManager(java.util.Map map) {
                    return sessionFactory.createEntityManager();
                }
                
                @Override
                public EntityManager createEntityManager(jakarta.persistence.SynchronizationType synchronizationType) {
                    return sessionFactory.createEntityManager();
                }
                
                @Override
                public EntityManager createEntityManager(jakarta.persistence.SynchronizationType synchronizationType, java.util.Map map) {
                    return sessionFactory.createEntityManager();
                }
                
                @Override
                public jakarta.persistence.criteria.CriteriaBuilder getCriteriaBuilder() {
                    return sessionFactory.getCriteriaBuilder();
                }
                
                @Override
                public jakarta.persistence.metamodel.Metamodel getMetamodel() {
                    return sessionFactory.getMetamodel();
                }
                
                @Override
                public boolean isOpen() {
                    return !sessionFactory.isClosed();
                }
                
                @Override
                public void close() {
                    sessionFactory.close();
                }
                
                @Override
                public java.util.Map<String, Object> getProperties() {
                    return java.util.Collections.emptyMap();
                }
                
                @Override
                public jakarta.persistence.cache.Cache getCache() {
                    return sessionFactory.getCache();
                }
                
                @Override
                public jakarta.persistence.PersistenceUnitUtil getPersistenceUnitUtil() {
                    return sessionFactory.getPersistenceUnitUtil();
                }
                
                @Override
                public void addNamedQuery(String name, jakarta.persistence.Query query) {
                    // Not supported
                }
                
                @Override
                public <T> T unwrap(Class<T> cls) {
                    if (cls.isInstance(sessionFactory)) {
                        return cls.cast(sessionFactory);
                    }
                    throw new jakarta.persistence.PersistenceException("Cannot unwrap to " + cls);
                }
                
                @Override
                public <T> void addNamedEntityGraph(String graphName, jakarta.persistence.EntityGraph<T> entityGraph) {
                    // Not supported
                }
            };
            
            // Test the connection
            EntityManager testEm = emf.createEntityManager();
            testEm.close();
            
            // Seed database with dummy data if empty (silently)
            seedDummyData();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to connect to database: " + e.getMessage());
            System.err.println("[ERROR] Please check your Supabase connection settings.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  Fee & Billing Management");
        System.out.println("=".repeat(60));
        
        boolean running = true;
        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    recordPaymentMenu();
                    break;
                case "2":
                    viewPaymentMenu();
                    break;
                case "3":
                    viewPaymentsByStudent();
                    break;
                case "4":
                    viewPaymentsByDateRange();
                    break;
                case "5":
                    reversePaymentMenu();
                    break;
                case "6":
                    createInvoiceMenu();
                    break;
                case "7":
                    viewAllData();
                    break;
                case "0":
                    running = false;
                    System.out.println("\n[INFO] Exiting demo. Thank you!");
                    break;
                default:
                    System.out.println("\n[ERROR] Invalid choice. Please try again.");
            }
        }
        scanner.close();
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    private void displayMainMenu() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("Payment Management:");
        System.out.println("-".repeat(60));
        System.out.println("  [1] Record Payment");
        System.out.println("  [2] View Payment by ID or Reference Number");
        System.out.println("  [3] View Payments by Student");
        System.out.println("  [4] View Payments by Date Range");
        System.out.println("  [5] Reverse Payment");
        System.out.println("  [6] Create Invoice");
        System.out.println("  [7] View All Data");
        System.out.println("  [0] Exit");
        System.out.println("-".repeat(60));
        System.out.print("Enter your choice: ");
    }

    // ===== Record Payment =====
    private void recordPaymentMenu() {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  RECORD PAYMENT");
            System.out.println("=".repeat(60));
            
            System.out.print("Student ID: ");
            String studentId = scanner.nextLine().trim();
            if (studentId.isEmpty()) {
                System.out.println("\n[ERROR] Student ID cannot be empty.");
                return;
            }
            
            System.out.print("Amount: ");
            BigDecimal amount;
            try {
                amount = new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR] Invalid amount format. Please enter a valid number.");
                return;
            }
            
            System.out.print("Reference Number: ");
            String referenceNumber = scanner.nextLine().trim();
            if (referenceNumber.isEmpty()) {
                System.out.println("\n[ERROR] Reference number cannot be empty.");
                return;
            }
            
            // Check if reference number already exists
            TypedQuery<Payment> refQuery = em.createQuery(
                "SELECT p FROM Payment p WHERE p.referenceNumber = :ref", Payment.class);
            refQuery.setParameter("ref", referenceNumber);
            if (!refQuery.getResultList().isEmpty()) {
                System.out.println("\n[ERROR] Reference number already exists: " + referenceNumber);
                return;
            }
            
            System.out.println("\nPayment Method:");
            System.out.println("  [1] BANK_TRANSFER");
            System.out.println("  [2] CHECK");
            System.out.println("  [3] CASH");
            System.out.println("  [4] ONLINE");
            System.out.println("  [5] CREDIT_CARD");
            System.out.println("  [6] DEBIT_CARD");
            System.out.print("Select payment method (1-6): ");
            String methodChoice = scanner.nextLine().trim();
            
            Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.BANK_TRANSFER;
            switch (methodChoice) {
                case "1": paymentMethod = Payment.PaymentMethod.BANK_TRANSFER; break;
                case "2": paymentMethod = Payment.PaymentMethod.CHECK; break;
                case "3": paymentMethod = Payment.PaymentMethod.CASH; break;
                case "4": paymentMethod = Payment.PaymentMethod.ONLINE; break;
                case "5": paymentMethod = Payment.PaymentMethod.CREDIT_CARD; break;
                case "6": paymentMethod = Payment.PaymentMethod.DEBIT_CARD; break;
                default:
                    System.out.println("[INFO] Using default: BANK_TRANSFER");
            }
            
            System.out.print("Payment Date (YYYY-MM-DD) or press Enter for today: ");
            String dateStr = scanner.nextLine().trim();
            LocalDate paymentDate;
            try {
                paymentDate = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);
            } catch (Exception e) {
                System.out.println("\n[ERROR] Invalid date format. Using today's date.");
                paymentDate = LocalDate.now();
            }
            
            System.out.print("Recorded By (optional): ");
            String recordedBy = scanner.nextLine().trim();
            
            System.out.print("Notes (optional): ");
            String notes = scanner.nextLine().trim();
            
            System.out.print("Invoice ID (optional, press Enter to skip): ");
            String invoiceIdStr = scanner.nextLine().trim();
            Long invoiceIdValue = null;
            if (!invoiceIdStr.isEmpty()) {
                try {
                    invoiceIdValue = Long.parseLong(invoiceIdStr);
                } catch (NumberFormatException e) {
                    System.out.println("[WARN] Invalid invoice ID format. Continuing without invoice.");
                }
            }
            
            Payment payment = new Payment();
            payment.setStudentId(studentId);
            payment.setAmount(amount);
            payment.setReferenceNumber(referenceNumber);
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentDate(paymentDate);
            payment.setRecordedBy(recordedBy.isEmpty() ? null : recordedBy);
            payment.setNotes(notes.isEmpty() ? null : notes);
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setCreatedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            
            if (invoiceIdValue != null) {
                Invoice invoice = em.find(Invoice.class, invoiceIdValue);
                if (invoice != null) {
                    payment.setInvoice(invoice);
                } else {
                    System.out.println("[WARN] Invoice not found, payment recorded without invoice");
                }
            }
            
            em.getTransaction().begin();
            em.persist(payment);
            em.getTransaction().commit();
            
            System.out.println("\n[SUCCESS] Payment recorded successfully!");
            displayPaymentDetails(payment);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("\n[ERROR] An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ===== View Payment =====
    private void viewPaymentMenu() {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  VIEW PAYMENT");
            System.out.println("=".repeat(60));
            System.out.println("  [1] By Payment ID");
            System.out.println("  [2] By Reference Number");
            System.out.println("  [0] Back to main menu");
            System.out.print("Enter your choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    System.out.print("Payment ID: ");
                    Long id;
                    try {
                        id = Long.parseLong(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("\n[ERROR] Invalid payment ID format.");
                        return;
                    }
                    Payment payment = em.find(Payment.class, id);
                    if (payment == null) {
                        System.out.println("\n[ERROR] Payment not found with ID: " + id);
                    } else {
                        System.out.println("\n--- PAYMENT DETAILS ---");
                        displayPaymentDetails(payment);
                    }
                    break;
                case "2":
                    System.out.print("Reference Number: ");
                    String refNum = scanner.nextLine().trim();
                    TypedQuery<Payment> query = em.createQuery(
                        "SELECT p FROM Payment p WHERE p.referenceNumber = :ref", Payment.class);
                    query.setParameter("ref", refNum);
                    List<Payment> results = query.getResultList();
                    if (results.isEmpty()) {
                        System.out.println("\n[ERROR] Payment not found with reference: " + refNum);
                    } else {
                        System.out.println("\n--- PAYMENT DETAILS ---");
                        displayPaymentDetails(results.get(0));
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println("\n[ERROR] Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println("\n[ERROR] An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ===== View Payments by Student =====
    private void viewPaymentsByStudent() {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  VIEW PAYMENTS BY STUDENT");
            System.out.println("=".repeat(60));
            
            System.out.print("Student ID (or press Enter for all): ");
            String studentId = scanner.nextLine().trim();
            
            List<Payment> studentPayments;
            if (studentId.isEmpty()) {
                TypedQuery<Payment> query = em.createQuery("SELECT p FROM Payment p", Payment.class);
                studentPayments = query.getResultList();
            } else {
                TypedQuery<Payment> query = em.createQuery(
                    "SELECT p FROM Payment p WHERE p.studentId = :studentId", Payment.class);
                query.setParameter("studentId", studentId);
                studentPayments = query.getResultList();
            }
            
            if (studentPayments.isEmpty()) {
                System.out.println("\n[INFO] No payments found" + (studentId.isEmpty() ? "" : " for student: " + studentId));
            } else {
                System.out.println("\n--- PAYMENTS (" + studentPayments.size() + ") ---");
                BigDecimal total = BigDecimal.ZERO;
                for (Payment p : studentPayments) {
                    displayPaymentSummary(p);
                    total = total.add(p.getAmount());
                }
                System.out.println("\nTotal Amount: " + total);
            }
        } catch (Exception e) {
            System.out.println("\n[ERROR] An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ===== View Payments by Date Range =====
    private void viewPaymentsByDateRange() {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  VIEW PAYMENTS BY DATE RANGE");
            System.out.println("=".repeat(60));
            
            System.out.print("Start Date (YYYY-MM-DD): ");
            LocalDate startDate;
            try {
                startDate = LocalDate.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("\n[ERROR] Invalid date format. Please use YYYY-MM-DD.");
                return;
            }
            
            System.out.print("End Date (YYYY-MM-DD): ");
            LocalDate endDate;
            try {
                endDate = LocalDate.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("\n[ERROR] Invalid date format. Please use YYYY-MM-DD.");
                return;
            }
            
            TypedQuery<Payment> query = em.createQuery(
                "SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate", 
                Payment.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            List<Payment> rangePayments = query.getResultList();
            
            if (rangePayments.isEmpty()) {
                System.out.println("\n[INFO] No payments found between " + startDate + " and " + endDate);
            } else {
                System.out.println("\n--- PAYMENTS (" + rangePayments.size() + ") ---");
                BigDecimal total = BigDecimal.ZERO;
                for (Payment p : rangePayments) {
                    displayPaymentSummary(p);
                    total = total.add(p.getAmount());
                }
                System.out.println("\nTotal Amount: " + total);
            }
        } catch (Exception e) {
            System.out.println("\n[ERROR] An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ===== Reverse Payment =====
    private void reversePaymentMenu() {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  REVERSE PAYMENT");
            System.out.println("=".repeat(60));
            
            System.out.print("Payment ID to reverse: ");
            Long id;
            try {
                id = Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR] Invalid payment ID format.");
                return;
            }
            
            Payment payment = em.find(Payment.class, id);
            if (payment == null) {
                System.out.println("\n[ERROR] Payment not found with ID: " + id);
                return;
            }
            
            if (payment.getStatus() == Payment.PaymentStatus.REVERSED) {
                System.out.println("\n[WARN] Payment is already reversed");
                return;
            }
            
            System.out.print("Reason for reversal: ");
            String reason = scanner.nextLine().trim();
            
            payment.setStatus(Payment.PaymentStatus.REVERSED);
            payment.setNotes((payment.getNotes() != null ? payment.getNotes() + "\n" : "") + 
                            "REVERSED: " + reason);
            payment.setUpdatedAt(LocalDateTime.now());
            
            em.getTransaction().begin();
            em.merge(payment);
            em.getTransaction().commit();
            
            System.out.println("\n[SUCCESS] Payment reversed successfully!");
            displayPaymentDetails(payment);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("\n[ERROR] An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ===== Create Invoice =====
    private void createInvoiceMenu() {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  CREATE INVOICE");
            System.out.println("=".repeat(60));
            
            System.out.print("Invoice Number: ");
            String invoiceNumber = scanner.nextLine().trim();
            if (invoiceNumber.isEmpty()) {
                System.out.println("\n[ERROR] Invoice number cannot be empty.");
                return;
            }
            
            // Check if invoice number already exists
            TypedQuery<Invoice> query = em.createQuery(
                "SELECT i FROM Invoice i WHERE i.invoiceNumber = :invoiceNumber", Invoice.class);
            query.setParameter("invoiceNumber", invoiceNumber);
            if (!query.getResultList().isEmpty()) {
                System.out.println("\n[ERROR] Invoice number already exists: " + invoiceNumber);
                return;
            }
            
            System.out.print("Student ID: ");
            String studentId = scanner.nextLine().trim();
            if (studentId.isEmpty()) {
                System.out.println("\n[ERROR] Student ID cannot be empty.");
                return;
            }
            
            System.out.print("Semester: ");
            String semester = scanner.nextLine().trim();
            
            System.out.print("Academic Year: ");
            String academicYear = scanner.nextLine().trim();
            
            System.out.print("Total Amount: ");
            BigDecimal totalAmount;
            try {
                totalAmount = new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("\n[ERROR] Invalid amount format. Please enter a valid number.");
                return;
            }
            
            System.out.print("Due Date (YYYY-MM-DD): ");
            LocalDate dueDate;
            try {
                dueDate = LocalDate.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("\n[ERROR] Invalid date format. Please use YYYY-MM-DD.");
                return;
            }
            
            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setStudentId(studentId);
            invoice.setSemester(semester);
            invoice.setAcademicYear(academicYear);
            invoice.setTotalAmount(totalAmount);
            invoice.setOutstandingBalance(totalAmount);
            invoice.setDueDate(dueDate);
            invoice.setStatus(Invoice.InvoiceStatus.UNPAID);
            invoice.setGeneratedAt(LocalDateTime.now());
            invoice.setUpdatedAt(LocalDateTime.now());
            
            em.getTransaction().begin();
            em.persist(invoice);
            em.getTransaction().commit();
            
            System.out.println("\n[SUCCESS] Invoice created successfully!");
            displayInvoiceDetails(invoice);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("\n[ERROR] An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ===== View All Data =====
    private void viewAllData() {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  ALL CURRENT DATA");
            System.out.println("=".repeat(60));
            
            TypedQuery<Invoice> invoiceQuery = em.createQuery("SELECT i FROM Invoice i", Invoice.class);
            List<Invoice> invoices = invoiceQuery.getResultList();
            
            System.out.println("\n--- INVOICES (" + invoices.size() + ") ---");
            if (invoices.isEmpty()) {
                System.out.println("  (No invoices yet)");
            } else {
                for (Invoice inv : invoices) {
                    System.out.println("  ID: " + inv.getId() + " | Invoice #: " + inv.getInvoiceNumber() + 
                                     " | Student: " + inv.getStudentId() + 
                                     " | Amount: " + inv.getTotalAmount() + 
                                     " | Balance: " + inv.getOutstandingBalance() + 
                                     " | Status: " + inv.getStatus());
                }
            }
            
            TypedQuery<Payment> paymentQuery = em.createQuery("SELECT p FROM Payment p", Payment.class);
            List<Payment> payments = paymentQuery.getResultList();
            
            System.out.println("\n--- PAYMENTS (" + payments.size() + ") ---");
            if (payments.isEmpty()) {
                System.out.println("  (No payments yet)");
            } else {
                for (Payment p : payments) {
                    displayPaymentSummary(p);
                }
            }
        } catch (Exception e) {
            System.out.println("\n[ERROR] An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private void displayPaymentDetails(Payment payment) {
        System.out.println("  ID: " + payment.getId());
        System.out.println("  Student ID: " + payment.getStudentId());
        System.out.println("  Amount: " + payment.getAmount());
        System.out.println("  Reference Number: " + payment.getReferenceNumber());
        System.out.println("  Payment Method: " + payment.getPaymentMethod());
        System.out.println("  Payment Date: " + payment.getPaymentDate());
        System.out.println("  Status: " + payment.getStatus());
        System.out.println("  Recorded By: " + (payment.getRecordedBy() != null ? payment.getRecordedBy() : "N/A"));
        System.out.println("  Invoice ID: " + (payment.getInvoice() != null ? payment.getInvoice().getId() : "N/A"));
        System.out.println("  Notes: " + (payment.getNotes() != null ? payment.getNotes() : "N/A"));
        System.out.println("  Created At: " + payment.getCreatedAt());
    }

    private void displayPaymentSummary(Payment payment) {
        System.out.println("  ID: " + payment.getId() + 
                         " | Student: " + payment.getStudentId() 
                         + " | Amount: " + payment.getAmount() 
                         + " | Ref: " + payment.getReferenceNumber() 
                         + " | Method: " + payment.getPaymentMethod() 
                         + " | Date: " + payment.getPaymentDate() 
                         + " | Status: " + payment.getStatus());
    }

    private void displayInvoiceDetails(Invoice invoice) {
        System.out.println("  ID: " + invoice.getId());
        System.out.println("  Invoice Number: " + invoice.getInvoiceNumber());
        System.out.println("  Student ID: " + invoice.getStudentId());
        System.out.println("  Semester: " + invoice.getSemester());
        System.out.println("  Academic Year: " + invoice.getAcademicYear());
        System.out.println("  Total Amount: " + invoice.getTotalAmount());
        System.out.println("  Outstanding Balance: " + invoice.getOutstandingBalance());
        System.out.println("  Status: " + invoice.getStatus());
        System.out.println("  Due Date: " + invoice.getDueDate());
    }

    // ===== Seed Dummy Data =====
    private void seedDummyData() {
        EntityManager em = emf.createEntityManager();
        try {
            // Check if data already exists
            TypedQuery<Long> invoiceCount = em.createQuery("SELECT COUNT(i) FROM Invoice i", Long.class);
            TypedQuery<Long> paymentCount = em.createQuery("SELECT COUNT(p) FROM Payment p", Long.class);
            
            long invoiceCountResult = invoiceCount.getSingleResult();
            long paymentCountResult = paymentCount.getSingleResult();
            
            if (invoiceCountResult > 0 || paymentCountResult > 0) {
                return;
            }
            
            em.getTransaction().begin();
            
            // Create dummy invoices
            Invoice inv1 = new Invoice();
            inv1.setInvoiceNumber("INV-2024-001");
            inv1.setStudentId("U1234567");
            inv1.setSemester("Fall");
            inv1.setAcademicYear("2024-2025");
            inv1.setTotalAmount(new BigDecimal("5000.00"));
            inv1.setOutstandingBalance(new BigDecimal("5000.00"));
            inv1.setDueDate(LocalDate.of(2024, 12, 15));
            inv1.setStatus(Invoice.InvoiceStatus.UNPAID);
            inv1.setGeneratedAt(LocalDateTime.now().minusDays(30));
            inv1.setUpdatedAt(LocalDateTime.now().minusDays(30));
            em.persist(inv1);
            
            Invoice inv2 = new Invoice();
            inv2.setInvoiceNumber("INV-2024-002");
            inv2.setStudentId("U2345678");
            inv2.setSemester("Fall");
            inv2.setAcademicYear("2024-2025");
            inv2.setTotalAmount(new BigDecimal("4500.00"));
            inv2.setOutstandingBalance(new BigDecimal("2000.00"));
            inv2.setDueDate(LocalDate.of(2024, 12, 20));
            inv2.setStatus(Invoice.InvoiceStatus.PARTIALLY_PAID);
            inv2.setAmountPaid(new BigDecimal("2500.00"));
            inv2.setGeneratedAt(LocalDateTime.now().minusDays(25));
            inv2.setUpdatedAt(LocalDateTime.now().minusDays(10));
            em.persist(inv2);
            
            Invoice inv3 = new Invoice();
            inv3.setInvoiceNumber("INV-2024-003");
            inv3.setStudentId("U3456789");
            inv3.setSemester("Spring");
            inv3.setAcademicYear("2024-2025");
            inv3.setTotalAmount(new BigDecimal("4800.00"));
            inv3.setOutstandingBalance(new BigDecimal("0.00"));
            inv3.setDueDate(LocalDate.of(2025, 5, 15));
            inv3.setStatus(Invoice.InvoiceStatus.PAID);
            inv3.setAmountPaid(new BigDecimal("4800.00"));
            inv3.setGeneratedAt(LocalDateTime.now().minusDays(60));
            inv3.setUpdatedAt(LocalDateTime.now().minusDays(45));
            em.persist(inv3);
            
            Invoice inv4 = new Invoice();
            inv4.setInvoiceNumber("INV-2024-004");
            inv4.setStudentId("U1234567");
            inv4.setSemester("Spring");
            inv4.setAcademicYear("2024-2025");
            inv4.setTotalAmount(new BigDecimal("5200.00"));
            inv4.setOutstandingBalance(new BigDecimal("5200.00"));
            inv4.setDueDate(LocalDate.of(2025, 5, 20));
            inv4.setStatus(Invoice.InvoiceStatus.UNPAID);
            inv4.setGeneratedAt(LocalDateTime.now().minusDays(20));
            inv4.setUpdatedAt(LocalDateTime.now().minusDays(20));
            em.persist(inv4);
            
            Invoice inv5 = new Invoice();
            inv5.setInvoiceNumber("INV-2024-005");
            inv5.setStudentId("U4567890");
            inv5.setSemester("Fall");
            inv5.setAcademicYear("2024-2025");
            inv5.setTotalAmount(new BigDecimal("6000.00"));
            inv5.setOutstandingBalance(new BigDecimal("6000.00"));
            inv5.setDueDate(LocalDate.of(2024, 11, 30));
            inv5.setStatus(Invoice.InvoiceStatus.OVERDUE);
            inv5.setGeneratedAt(LocalDateTime.now().minusDays(45));
            inv5.setUpdatedAt(LocalDateTime.now().minusDays(45));
            em.persist(inv5);
            
            // Flush to get IDs
            em.flush();
            
            // Create dummy payments
            Payment pay1 = new Payment();
            pay1.setStudentId("U2345678");
            pay1.setAmount(new BigDecimal("2500.00"));
            pay1.setReferenceNumber("PAY-2024-001");
            pay1.setPaymentMethod(Payment.PaymentMethod.BANK_TRANSFER);
            pay1.setPaymentDate(LocalDate.now().minusDays(10));
            pay1.setStatus(Payment.PaymentStatus.COMPLETED);
            pay1.setRecordedBy("Admin001");
            pay1.setNotes("Tuition payment for Fall 2024");
            pay1.setInvoice(inv2);
            pay1.setCreatedAt(LocalDateTime.now().minusDays(10));
            pay1.setUpdatedAt(LocalDateTime.now().minusDays(10));
            em.persist(pay1);
            
            Payment pay2 = new Payment();
            pay2.setStudentId("U3456789");
            pay2.setAmount(new BigDecimal("4800.00"));
            pay2.setReferenceNumber("PAY-2024-002");
            pay2.setPaymentMethod(Payment.PaymentMethod.ONLINE);
            pay2.setPaymentDate(LocalDate.now().minusDays(45));
            pay2.setStatus(Payment.PaymentStatus.COMPLETED);
            pay2.setRecordedBy("Admin002");
            pay2.setNotes("Full payment for Spring 2025 semester");
            pay2.setInvoice(inv3);
            pay2.setCreatedAt(LocalDateTime.now().minusDays(45));
            pay2.setUpdatedAt(LocalDateTime.now().minusDays(45));
            em.persist(pay2);
            
            Payment pay3 = new Payment();
            pay3.setStudentId("U1234567");
            pay3.setAmount(new BigDecimal("1500.00"));
            pay3.setReferenceNumber("PAY-2024-003");
            pay3.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);
            pay3.setPaymentDate(LocalDate.now().minusDays(5));
            pay3.setStatus(Payment.PaymentStatus.COMPLETED);
            pay3.setRecordedBy("Admin001");
            pay3.setNotes("Partial payment - will pay remaining later");
            pay3.setCreatedAt(LocalDateTime.now().minusDays(5));
            pay3.setUpdatedAt(LocalDateTime.now().minusDays(5));
            em.persist(pay3);
            
            Payment pay4 = new Payment();
            pay4.setStudentId("U2345678");
            pay4.setAmount(new BigDecimal("500.00"));
            pay4.setReferenceNumber("PAY-2024-004");
            pay4.setPaymentMethod(Payment.PaymentMethod.CASH);
            pay4.setPaymentDate(LocalDate.now().minusDays(2));
            pay4.setStatus(Payment.PaymentStatus.COMPLETED);
            pay4.setRecordedBy("Admin003");
            pay4.setNotes("Cash payment at counter");
            pay4.setCreatedAt(LocalDateTime.now().minusDays(2));
            pay4.setUpdatedAt(LocalDateTime.now().minusDays(2));
            em.persist(pay4);
            
            Payment pay5 = new Payment();
            pay5.setStudentId("U5678901");
            pay5.setAmount(new BigDecimal("3000.00"));
            pay5.setReferenceNumber("PAY-2024-005");
            pay5.setPaymentMethod(Payment.PaymentMethod.DEBIT_CARD);
            pay5.setPaymentDate(LocalDate.now().minusDays(15));
            pay5.setStatus(Payment.PaymentStatus.COMPLETED);
            pay5.setRecordedBy("Admin002");
            pay5.setNotes("Tuition payment");
            pay5.setCreatedAt(LocalDateTime.now().minusDays(15));
            pay5.setUpdatedAt(LocalDateTime.now().minusDays(15));
            em.persist(pay5);
            
            Payment pay6 = new Payment();
            pay6.setStudentId("U1234567");
            pay6.setAmount(new BigDecimal("2000.00"));
            pay6.setReferenceNumber("PAY-2024-006");
            pay6.setPaymentMethod(Payment.PaymentMethod.BANK_TRANSFER);
            pay6.setPaymentDate(LocalDate.now().minusDays(1));
            pay6.setStatus(Payment.PaymentStatus.COMPLETED);
            pay6.setRecordedBy("Admin001");
            pay6.setNotes("Additional payment");
            pay6.setCreatedAt(LocalDateTime.now().minusDays(1));
            pay6.setUpdatedAt(LocalDateTime.now().minusDays(1));
            em.persist(pay6);
            
            Payment pay7 = new Payment();
            pay7.setStudentId("U4567890");
            pay7.setAmount(new BigDecimal("1000.00"));
            pay7.setReferenceNumber("PAY-2024-007");
            pay7.setPaymentMethod(Payment.PaymentMethod.CHECK);
            pay7.setPaymentDate(LocalDate.now().minusDays(20));
            pay7.setStatus(Payment.PaymentStatus.COMPLETED);
            pay7.setRecordedBy("Admin003");
            pay7.setNotes("Check payment - check number 12345");
            pay7.setCreatedAt(LocalDateTime.now().minusDays(20));
            pay7.setUpdatedAt(LocalDateTime.now().minusDays(20));
            em.persist(pay7);
            
            Payment pay8 = new Payment();
            pay8.setStudentId("U2345678");
            pay8.setAmount(new BigDecimal("800.00"));
            pay8.setReferenceNumber("PAY-2024-008");
            pay8.setPaymentMethod(Payment.PaymentMethod.ONLINE);
            pay8.setPaymentDate(LocalDate.now().minusDays(3));
            pay8.setStatus(Payment.PaymentStatus.COMPLETED);
            pay8.setRecordedBy("System");
            pay8.setNotes("Online payment via portal");
            pay8.setCreatedAt(LocalDateTime.now().minusDays(3));
            pay8.setUpdatedAt(LocalDateTime.now().minusDays(3));
            em.persist(pay8);
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Silently fail - don't show error messages for cleaner output
            // Don't exit - continue even if seeding fails
        } finally {
            em.close();
        }
    }

    public static void main(String[] args) {
        InteractiveFeeDemo demo = new InteractiveFeeDemo();
        try {
            demo.start();
        } catch (Exception e) {
            System.err.println("\n[ERROR] An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
