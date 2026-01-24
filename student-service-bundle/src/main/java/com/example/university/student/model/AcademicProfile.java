package com.example.university.student.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Academic Profile Entity - Student academic information
 * Tracks GPA, academic standing, credits, etc.
 */
@Entity
@Table(name = "academic_profiles")
public class AcademicProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, length = 50)
    private String studentId;

    @Column(name = "cumulative_gpa", precision = 3, scale = 2)
    private BigDecimal cumulativeGpa;

    @Column(name = "semester_gpa", precision = 3, scale = 2)
    private BigDecimal semesterGpa;

    @Column(name = "total_credits_earned")
    private Integer totalCreditsEarned = 0;

    @Column(name = "total_credits_attempted")
    private Integer totalCreditsAttempted = 0;

    @Column(name = "current_semester", length = 50)
    private String currentSemester;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "academic_standing", length = 50)
    private AcademicStanding academicStanding = AcademicStanding.GOOD_STANDING;

    @Column(name = "class_standing", length = 50)
    private String classStanding; // Freshman, Sophomore, Junior, Senior

    @Column(name = "expected_graduation_date")
    private LocalDate expectedGraduationDate;

    @Column(name = "honors", length = 200)
    private String honors; // Dean's List, Honors Program, etc.

    @Column(name = "scholarships", columnDefinition = "TEXT")
    private String scholarships;

    @Column(name = "academic_notes", columnDefinition = "TEXT")
    private String academicNotes;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public BigDecimal getCumulativeGpa() { return cumulativeGpa; }
    public void setCumulativeGpa(BigDecimal cumulativeGpa) { this.cumulativeGpa = cumulativeGpa; }
    public BigDecimal getSemesterGpa() { return semesterGpa; }
    public void setSemesterGpa(BigDecimal semesterGpa) { this.semesterGpa = semesterGpa; }
    public Integer getTotalCreditsEarned() { return totalCreditsEarned; }
    public void setTotalCreditsEarned(Integer totalCreditsEarned) { this.totalCreditsEarned = totalCreditsEarned; }
    public Integer getTotalCreditsAttempted() { return totalCreditsAttempted; }
    public void setTotalCreditsAttempted(Integer totalCreditsAttempted) { this.totalCreditsAttempted = totalCreditsAttempted; }
    public String getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(String currentSemester) { this.currentSemester = currentSemester; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public AcademicStanding getAcademicStanding() { return academicStanding; }
    public void setAcademicStanding(AcademicStanding academicStanding) { this.academicStanding = academicStanding; }
    public String getClassStanding() { return classStanding; }
    public void setClassStanding(String classStanding) { this.classStanding = classStanding; }
    public LocalDate getExpectedGraduationDate() { return expectedGraduationDate; }
    public void setExpectedGraduationDate(LocalDate expectedGraduationDate) { this.expectedGraduationDate = expectedGraduationDate; }
    public String getHonors() { return honors; }
    public void setHonors(String honors) { this.honors = honors; }
    public String getScholarships() { return scholarships; }
    public void setScholarships(String scholarships) { this.scholarships = scholarships; }
    public String getAcademicNotes() { return academicNotes; }
    public void setAcademicNotes(String academicNotes) { this.academicNotes = academicNotes; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public enum AcademicStanding {
        GOOD_STANDING, PROBATION, ACADEMIC_WARNING, SUSPENDED, DISMISSED
    }
}
