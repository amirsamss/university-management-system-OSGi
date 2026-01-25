package com.example.university.fee.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * FeeItem Entity - Individual fee items within a fee structure
 * UC-23: Configure Tuition Fee
 */
@Entity
@Table(name = "fee_items")
public class FeeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "fee_structure_id", nullable = false, insertable = true, updatable = true)
    private FeeStructure feeStructure;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false)
    private FeeItemType feeType = FeeItemType.FIXED;

    @Column(name = "mandatory")
    private Boolean mandatory = true;

    @Column(name = "refundable")
    private Boolean refundable = false;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FeeStructure getFeeStructure() { return feeStructure; }
    public void setFeeStructure(FeeStructure feeStructure) { this.feeStructure = feeStructure; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public FeeItemType getFeeType() { return feeType; }
    public void setFeeType(FeeItemType feeType) { this.feeType = feeType; }
    public Boolean getMandatory() { return mandatory; }
    public void setMandatory(Boolean mandatory) { this.mandatory = mandatory; }
    public Boolean getRefundable() { return refundable; }
    public void setRefundable(Boolean refundable) { this.refundable = refundable; }

    public enum FeeItemType {
        FIXED, VARIABLE, ONE_TIME
    }
}
