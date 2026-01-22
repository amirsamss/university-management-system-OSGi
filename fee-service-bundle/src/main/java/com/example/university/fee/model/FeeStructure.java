package com.example.university.fee.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * FeeStructure Entity - Defines tuition fee configuration
 * UC-23: Configure Tuition Fee
 */
@Entity
@Table(name = "fee_structures", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"academic_year", "department", "student_type"})
})
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "department", nullable = false)
    private String department;

    @Column(name = "student_type", nullable = false)
    private String studentType;

    @Column(name = "cost_per_credit", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPerCredit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FeeStructureStatus status = FeeStructureStatus.ACTIVE;

    @OneToMany(mappedBy = "feeStructure", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FeeItem> feeItems = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addFeeItem(FeeItem feeItem) {
        feeItems.add(feeItem);
        feeItem.setFeeStructure(this);
    }

    public BigDecimal getTotalFixedFees() {
        return feeItems.stream()
                // 👇 FIXED: Added "FeeItem." before FeeItemType
                .filter(item -> item.getFeeType() == FeeItem.FeeItemType.FIXED)
                .map(FeeItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getStudentType() { return studentType; }
    public void setStudentType(String studentType) { this.studentType = studentType; }
    public BigDecimal getCostPerCredit() { return costPerCredit; }
    public void setCostPerCredit(BigDecimal costPerCredit) { this.costPerCredit = costPerCredit; }
    public FeeStructureStatus getStatus() { return status; }
    public void setStatus(FeeStructureStatus status) { this.status = status; }
    public List<FeeItem> getFeeItems() { return feeItems; }
    public void setFeeItems(List<FeeItem> feeItems) { this.feeItems = feeItems; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public enum FeeStructureStatus {
        ACTIVE, INACTIVE, ARCHIVED
    }
}