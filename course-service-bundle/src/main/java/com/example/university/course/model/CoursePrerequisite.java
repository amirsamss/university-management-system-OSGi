package com.example.university.course.model;

import jakarta.persistence.*;

/**
 * Course Prerequisite Entity - UC8: Check Course Prerequisites
 */
@Entity
@Table(name = "course_prerequisites", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"course_id", "prerequisite_course_code"})
})
public class CoursePrerequisite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "course_code", nullable = false)
    private String courseCode;

    @Column(name = "prerequisite_course_code", nullable = false)
    private String prerequisiteCourseCode;

    @Column(name = "prerequisite_course_name")
    private String prerequisiteCourseName;

    @Column(name = "minimum_grade_required")
    private String minimumGradeRequired; // A, B+, B, C+, C, D

    @Column(name = "minimum_gpa_required", precision = 3, scale = 2)
    private Double minimumGpaRequired;

    @Column(name = "is_mandatory")
    private Boolean isMandatory = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    
    public String getPrerequisiteCourseCode() { return prerequisiteCourseCode; }
    public void setPrerequisiteCourseCode(String prerequisiteCourseCode) { this.prerequisiteCourseCode = prerequisiteCourseCode; }
    
    public String getPrerequisiteCourseName() { return prerequisiteCourseName; }
    public void setPrerequisiteCourseName(String prerequisiteCourseName) { this.prerequisiteCourseName = prerequisiteCourseName; }
    
    public String getMinimumGradeRequired() { return minimumGradeRequired; }
    public void setMinimumGradeRequired(String minimumGradeRequired) { this.minimumGradeRequired = minimumGradeRequired; }
    
    public Double getMinimumGpaRequired() { return minimumGpaRequired; }
    public void setMinimumGpaRequired(Double minimumGpaRequired) { this.minimumGpaRequired = minimumGpaRequired; }
    
    public Boolean getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
