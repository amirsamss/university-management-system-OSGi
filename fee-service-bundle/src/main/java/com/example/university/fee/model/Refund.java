package com.example.university.fee.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Refund Entity - Process refunds for overpayments or withdrawals
 * UC-28: Process Refund
 */
@Entity
@Table(name = "refunds")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_type", nullable = false)
    private RefundType refundType = RefundType.BANK_TRANSFER;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RefundStatus status = RefundStatus.PENDING;

    @Column(name = "bank_reference")
    private String bankReference;

    @Column(name = "bank_account_info")
    private String bankAccountInfo;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "processed_by")
    private String processedBy;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @Column(name = "holding_semester")
    private String holdingSemester;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public RefundType getRefundType() { return refundType; }
    public void setRefundType(RefundType refundType) { this.refundType = refundType; }
    public RefundStatus getStatus() { return status; }
    public void setStatus(RefundStatus status) { this.status = status; }
    public String getBankReference() { return bankReference; }
    public void setBankReference(String bankReference) { this.bankReference = bankReference; }
    public String getBankAccountInfo() { return bankAccountInfo; }
    public void setBankAccountInfo(String bankAccountInfo) { this.bankAccountInfo = bankAccountInfo; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }
    public LocalDateTime getProcessedDate() { return processedDate; }
    public void setProcessedDate(LocalDateTime processedDate) { this.processedDate = processedDate; }
    public String getHoldingSemester() { return holdingSemester; }
    public void setHoldingSemester(String holdingSemester) { this.holdingSemester = holdingSemester; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum RefundType {
        BANK_TRANSFER, CHECK, TRANSFER_NEXT_SEMESTER, CREDIT_TO_ACCOUNT
    }

    public enum RefundStatus {
        PENDING, APPROVED, PROCESSING, PROCESSED, HELD, REJECTED, CANCELLED
    }
}
