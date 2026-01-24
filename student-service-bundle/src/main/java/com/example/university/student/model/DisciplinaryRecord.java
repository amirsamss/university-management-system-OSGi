package com.example.university.student.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Disciplinary Record Entity - Student disciplinary actions
 * Tracks violations, sanctions, and disciplinary history
 */
@Entity
@Table(name = "disciplinary_records")
public class DisciplinaryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, length = 50)
    private String studentId;

    @Column(name = "incident_date", nullable = false)
    private LocalDate incidentDate;

    @Column(name = "violation_type", nullable = false, length = 100)
    private String violationType;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private Severity severity = Severity.LOW;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RecordStatus status = RecordStatus.OPEN;

    @Column(name = "sanction", columnDefinition = "TEXT")
    private String sanction;

    @Column(name = "sanction_start_date")
    private LocalDate sanctionStartDate;

    @Column(name = "sanction_end_date")
    private LocalDate sanctionEndDate;

    @Column(name = "reported_by", length = 100)
    private String reportedBy;

    @Column(name = "reviewed_by", length = 100)
    private String reviewedBy;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (incidentDate == null) {
            incidentDate = LocalDate.now();
        }
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
    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; }
    public String getViolationType() { return violationType; }
    public void setViolationType(String violationType) { this.violationType = violationType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public String getSanction() { return sanction; }
    public void setSanction(String sanction) { this.sanction = sanction; }
    public LocalDate getSanctionStartDate() { return sanctionStartDate; }
    public void setSanctionStartDate(LocalDate sanctionStartDate) { this.sanctionStartDate = sanctionStartDate; }
    public LocalDate getSanctionEndDate() { return sanctionEndDate; }
    public void setSanctionEndDate(LocalDate sanctionEndDate) { this.sanctionEndDate = sanctionEndDate; }
    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }
    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum Severity {
        LOW, MODERATE, HIGH
    }

    public enum RecordStatus {
        OPEN, UNDER_REVIEW, RESOLVED, APPEALED, CLOSED
    }
}
