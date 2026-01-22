package com.example.university.exam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "course_code")
    private String courseCode;

    @Column(name = "marks")
    private Double marks;

    @Column(name = "grade_letter")
    private String gradeLetter;

    @Column(name = "grade_point") // 👈 NEW: Needed for CGPA
    private Double gradePoint;

    public Grade() {}

    public Grade(String studentId, String courseCode, Double marks) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        setMarks(marks); // Use setter to trigger calculations
    }

    // Auto-calculate Letter and Point when marks are set
    public void setMarks(Double marks) {
        this.marks = marks;
        calculateMetrics(marks);
    }

    private void calculateMetrics(Double marks) {
        if (marks >= 80) { this.gradeLetter = "A"; this.gradePoint = 4.0; }
        else if (marks >= 75) { this.gradeLetter = "A-"; this.gradePoint = 3.7; }
        else if (marks >= 70) { this.gradeLetter = "B+"; this.gradePoint = 3.3; }
        else if (marks >= 65) { this.gradeLetter = "B"; this.gradePoint = 3.0; }
        else if (marks >= 60) { this.gradeLetter = "C+"; this.gradePoint = 2.3; }
        else if (marks >= 50) { this.gradeLetter = "C"; this.gradePoint = 2.0; }
        else { this.gradeLetter = "F"; this.gradePoint = 0.0; }
    }

    // Standard Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public Double getMarks() { return marks; }
    public String getGradeLetter() { return gradeLetter; }
    public void setGradeLetter(String gradeLetter) { this.gradeLetter = gradeLetter; }
    public Double getGradePoint() { return gradePoint; }
    public void setGradePoint(Double gradePoint) { this.gradePoint = gradePoint; }
}