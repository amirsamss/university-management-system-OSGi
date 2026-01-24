package com.example.university.student.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Enrollment Entity - Student course enrollments
 * Tracks student enrollments in courses
 */
@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, length = 50)
    private String studentId;

    @Column(name = "course_id", nullable = false, length = 50)
    private String courseId;

    @Column(name = "course_code", nullable = false, length = 50)
    private String courseCode;

    @Column(name = "course_name", nullable = false, length = 200)
    private String courseName;

    @Column(name = "semester", nullable = false, length = 50)
    private String semester;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "credits")
    private Integer credits;

    @Enumerated(EnumType.STRING)
    @Column(name = "enrollment_status", nullable = false)
    private EnrollmentStatus enrollmentStatus = EnrollmentStatus.ENROLLED;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade", length = 10)
    private Grade grade;

    @Column(name = "numeric_grade", precision = 5, scale = 2)
    private BigDecimal numericGrade;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Column(name = "drop_date")
    private LocalDate dropDate;

    @Column(name = "withdrawal_date")
    private LocalDate withdrawalDate;

    @Column(name = "instructor_id", length = 50)
    private String instructorId;

    @Column(name = "section", length = 50)
    private String section;

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
        if (enrollmentDate == null) {
            enrollmentDate = LocalDate.now();
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
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }
    public EnrollmentStatus getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }
    public Grade getGrade() { return grade; }
    public void setGrade(Grade grade) { this.grade = grade; }
    public BigDecimal getNumericGrade() { return numericGrade; }
    public void setNumericGrade(BigDecimal numericGrade) { this.numericGrade = numericGrade; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public LocalDate getDropDate() { return dropDate; }
    public void setDropDate(LocalDate dropDate) { this.dropDate = dropDate; }
    public LocalDate getWithdrawalDate() { return withdrawalDate; }
    public void setWithdrawalDate(LocalDate withdrawalDate) { this.withdrawalDate = withdrawalDate; }
    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum EnrollmentStatus {
        ENROLLED, DROPPED, WITHDRAWN, COMPLETED, INCOMPLETE, FAILED
    }

    public enum Grade {
        A_PLUS, A, A_MINUS, B_PLUS, B, B_MINUS, C_PLUS, C, C_MINUS, D_PLUS, D, D_MINUS, F, P, NP, W, I, AU
    }
}
