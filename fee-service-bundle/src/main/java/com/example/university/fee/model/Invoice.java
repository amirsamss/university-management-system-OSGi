package com.example.university.fee.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Invoice Entity
 */
@Entity
@Table(name = "invoices")
public class Invoice implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "semester", nullable = false)
    private String semester;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "total_credits")
    private Integer totalCredits;

    @Column(name = "tuition_amount", precision = 10, scale = 2)
    private BigDecimal tuitionAmount = BigDecimal.ZERO;

    @Column(name = "fixed_fees_amount", precision = 10, scale = 2)
    private BigDecimal fixedFeesAmount = BigDecimal.ZERO;

    @Column(name = "other_charges", precision = 10, scale = 2)
    private BigDecimal otherCharges = BigDecimal.ZERO;

    @Column(name = "late_fee_amount", precision = 10, scale = 2)
    private BigDecimal lateFeeAmount = BigDecimal.ZERO;

    @Column(name = "financial_aid_amount", precision = 10, scale = 2)
    private BigDecimal financialAidAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "outstanding_balance", precision = 10, scale = 2)
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status = InvoiceStatus.UNPAID;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceLineItem> lineItems = new ArrayList<>();

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateOutstandingBalance();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateOutstandingBalance();
    }

    public void calculateOutstandingBalance() {
        // Outstanding balance = Total + Late Fees - Amount Paid - Financial Aid
        // Handle null values safely
        BigDecimal total = this.totalAmount != null ? this.totalAmount : BigDecimal.ZERO;
        BigDecimal lateFees = this.lateFeeAmount != null ? this.lateFeeAmount : BigDecimal.ZERO;
        BigDecimal paid = this.amountPaid != null ? this.amountPaid : BigDecimal.ZERO;
        BigDecimal financialAid = this.financialAidAmount != null ? this.financialAidAmount : BigDecimal.ZERO;
        
        BigDecimal totalWithLateFees = total.add(lateFees);
        this.outstandingBalance = totalWithLateFees.subtract(paid).subtract(financialAid);
        if (this.outstandingBalance == null || this.outstandingBalance.compareTo(BigDecimal.ZERO) < 0) {
            this.outstandingBalance = BigDecimal.ZERO;
        }
        updateStatus();
    }
    
    /**
     * Calculate and apply late fees for overdue invoices
     * @param lateFeeRate Rate per day (e.g., 0.01 for 1% per day)
     * @return The late fee amount calculated
     */
    public BigDecimal calculateAndApplyLateFee(BigDecimal lateFeeRate) {
        if (this.dueDate == null || this.outstandingBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        LocalDate today = LocalDate.now();
        if (!today.isAfter(this.dueDate)) {
            return BigDecimal.ZERO; // Not overdue yet
        }
        
        // Calculate days overdue
        long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(this.dueDate, today);
        if (daysOverdue <= 0) {
            return BigDecimal.ZERO;
        }
        
        // Calculate late fee: (Outstanding Balance * Rate * Days Overdue)
        // Cap at 100% of original invoice amount
        BigDecimal baseAmount = this.totalAmount.subtract(this.amountPaid).subtract(this.financialAidAmount);
        if (baseAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal calculatedLateFee = baseAmount.multiply(lateFeeRate).multiply(BigDecimal.valueOf(daysOverdue));
        BigDecimal maxLateFee = this.totalAmount; // Cap at 100% of original amount
        
        BigDecimal newLateFee = calculatedLateFee.min(maxLateFee);
        
        // Only add the difference if late fee increased
        BigDecimal lateFeeIncrease = newLateFee.subtract(this.lateFeeAmount);
        if (lateFeeIncrease.compareTo(BigDecimal.ZERO) > 0) {
            this.lateFeeAmount = newLateFee;
            calculateOutstandingBalance();
        }
        
        return lateFeeIncrease;
    }

    private void updateStatus() {
        if (this.outstandingBalance.compareTo(BigDecimal.ZERO) == 0) {
            if (this.status == InvoiceStatus.SETTLED) {
                this.status = InvoiceStatus.SETTLED;
            } else {
                this.status = InvoiceStatus.PAID;
            }
        } else if (this.amountPaid.compareTo(BigDecimal.ZERO) > 0) {
            // Partially paid
            if (this.dueDate != null && LocalDate.now().isAfter(this.dueDate)) {
                this.status = InvoiceStatus.OVERDUE;
            } else {
                this.status = InvoiceStatus.PARTIALLY_PAID;
            }
        } else if (this.dueDate != null && LocalDate.now().isAfter(this.dueDate)) {
            this.status = InvoiceStatus.OVERDUE;
        } else {
            this.status = InvoiceStatus.UNPAID;
        }
    }

    public void addLineItem(InvoiceLineItem lineItem) {
        lineItems.add(lineItem);
        lineItem.setInvoice(this);
    }

    public void recordPayment(BigDecimal amount) {
        if (amount == null) {
            return;
        }
        if (this.amountPaid == null) {
            this.amountPaid = BigDecimal.ZERO;
        }
        this.amountPaid = this.amountPaid.add(amount);
        calculateOutstandingBalance();
    }

    public void applyFinancialAid(BigDecimal amount) {
        if (amount == null) {
            return;
        }
        if (this.financialAidAmount == null) {
            this.financialAidAmount = BigDecimal.ZERO;
        }
        this.financialAidAmount = this.financialAidAmount.add(amount);
        calculateOutstandingBalance();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public Integer getTotalCredits() { return totalCredits; }
    public void setTotalCredits(Integer totalCredits) { this.totalCredits = totalCredits; }
    public BigDecimal getTuitionAmount() { return tuitionAmount; }
    public void setTuitionAmount(BigDecimal tuitionAmount) { this.tuitionAmount = tuitionAmount; }
    public BigDecimal getFixedFeesAmount() { return fixedFeesAmount; }
    public void setFixedFeesAmount(BigDecimal fixedFeesAmount) { this.fixedFeesAmount = fixedFeesAmount; }
    public BigDecimal getOtherCharges() { return otherCharges; }
    public void setOtherCharges(BigDecimal otherCharges) { this.otherCharges = otherCharges; }
    public BigDecimal getLateFeeAmount() { return lateFeeAmount; }
    public void setLateFeeAmount(BigDecimal lateFeeAmount) { this.lateFeeAmount = lateFeeAmount; }
    public BigDecimal getFinancialAidAmount() { return financialAidAmount; }
    public void setFinancialAidAmount(BigDecimal financialAidAmount) { this.financialAidAmount = financialAidAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
    public BigDecimal getOutstandingBalance() { return outstandingBalance; }
    public void setOutstandingBalance(BigDecimal outstandingBalance) { this.outstandingBalance = outstandingBalance; }
    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public List<InvoiceLineItem> getLineItems() { return lineItems; }
    public void setLineItems(List<InvoiceLineItem> lineItems) { this.lineItems = lineItems; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum InvoiceStatus {
        UNPAID, PARTIALLY_PAID, PAID, OVERDUE, SETTLED, CANCELLED, REFUNDED
    }
}
