package com.example.university.course.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Course Entity - UC5: View Course Information
 */
@Entity
@Table(name = "courses", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"course_code"})
})
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_code", nullable = false)
    private String courseCode;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "department", nullable = false)
    private String department;

    @Column(name = "credits", nullable = false)
    private Integer credits;

    @Column(name = "instructor_name")
    private String instructorName;

    @Column(name = "instructor_id")
    private String instructorId;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "current_enrollment")
    private Integer currentEnrollment = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CourseStatus status = CourseStatus.ACTIVE;

    @Column(name = "semester", nullable = false)
    private String semester;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "pre_requisites", columnDefinition = "TEXT")
    private String preRequisites;

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
    
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }
    
    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }
    
    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }
    
    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }
    
    public Integer getCurrentEnrollment() { return currentEnrollment; }
    public void setCurrentEnrollment(Integer currentEnrollment) { this.currentEnrollment = currentEnrollment; }
    
    public CourseStatus getStatus() { return status; }
    public void setStatus(CourseStatus status) { this.status = status; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    
    public String getPreRequisites() { return preRequisites; }
    public void setPreRequisites(String preRequisites) { this.preRequisites = preRequisites; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum CourseStatus {
        ACTIVE, INACTIVE, ARCHIVED, PENDING_APPROVAL, CANCELLED
    }
}
