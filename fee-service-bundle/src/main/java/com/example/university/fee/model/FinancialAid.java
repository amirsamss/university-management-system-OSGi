package com.example.university.fee.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * FinancialAid Entity - Scholarships, grants, and waivers
 * UC-26: Manage Financial Aid
 */
@Entity
@Table(name = "financial_aids")
public class FinancialAid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "aid_type", nullable = false)
    private AidType aidType;

    @Column(name = "aid_code")
    private String aidCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "semester")
    private String semester;

    @Column(name = "eligibility_criteria", columnDefinition = "TEXT")
    private String eligibilityCriteria;

    @Column(name = "min_gpa_required", precision = 3, scale = 2)
    private BigDecimal minGpaRequired;

    @Column(name = "max_aid_cap", precision = 10, scale = 2)
    private BigDecimal maxAidCap;

    @Column(name = "is_refundable")
    private Boolean isRefundable = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AidStatus status = AidStatus.PENDING;

    @Column(name = "applied_to_invoice_id")
    private Long appliedToInvoiceId;

    @Column(name = "applied_date")
    private LocalDateTime appliedDate;

    @Column(name = "revoked_reason", columnDefinition = "TEXT")
    private String revokedReason;

    @Column(name = "approved_by")
    private String approvedBy;

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
    public AidType getAidType() { return aidType; }
    public void setAidType(AidType aidType) { this.aidType = aidType; }
    public String getAidCode() { return aidCode; }
    public void setAidCode(String aidCode) { this.aidCode = aidCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getEligibilityCriteria() { return eligibilityCriteria; }
    public void setEligibilityCriteria(String eligibilityCriteria) { this.eligibilityCriteria = eligibilityCriteria; }
    public BigDecimal getMinGpaRequired() { return minGpaRequired; }
    public void setMinGpaRequired(BigDecimal minGpaRequired) { this.minGpaRequired = minGpaRequired; }
    public BigDecimal getMaxAidCap() { return maxAidCap; }
    public void setMaxAidCap(BigDecimal maxAidCap) { this.maxAidCap = maxAidCap; }
    public Boolean getIsRefundable() { return isRefundable; }
    public void setIsRefundable(Boolean isRefundable) { this.isRefundable = isRefundable; }
    public AidStatus getStatus() { return status; }
    public void setStatus(AidStatus status) { this.status = status; }
    public Long getAppliedToInvoiceId() { return appliedToInvoiceId; }
    public void setAppliedToInvoiceId(Long appliedToInvoiceId) { this.appliedToInvoiceId = appliedToInvoiceId; }
    public LocalDateTime getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDateTime appliedDate) { this.appliedDate = appliedDate; }
    public String getRevokedReason() { return revokedReason; }
    public void setRevokedReason(String revokedReason) { this.revokedReason = revokedReason; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum AidType {
        SCHOLARSHIP, GRANT, WAIVER, LOAN, BURSARY, SPONSORSHIP
    }

    public enum AidStatus {
        PENDING, APPROVED, APPLIED, REVOKED, EXPIRED, REJECTED
    }
}
