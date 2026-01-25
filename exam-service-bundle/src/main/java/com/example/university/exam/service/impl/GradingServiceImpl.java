package com.example.university.exam.service.impl;

import com.example.university.exam.model.Exam;
import com.example.university.exam.model.Grade;
import com.example.university.exam.service.GradingService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Component(service = GradingService.class, immediate = true)
public class GradingServiceImpl implements GradingService {

    private static final Logger logger = LoggerFactory.getLogger(GradingServiceImpl.class);
    private EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Exam scheduleExam(Exam exam) {
        logger.info("Scheduling exam: {}", exam.getCourseCode());
        entityManager.persist(exam);
        entityManager.flush();
        return exam;
    }

    @Override
    public List<Exam> getAllExams() {
        TypedQuery<Exam> query = entityManager.createQuery("SELECT e FROM Exam e", Exam.class);
        return query.getResultList();
    }

    @Override
    public Grade submitGrade(Grade grade) {
        logger.info("Submitting grade for student: {}", grade.getStudentId());
        entityManager.persist(grade);
        entityManager.flush();
        return grade;
    }

    @Override
    public List<Grade> getAllGrades() {
        TypedQuery<Grade> query = entityManager.createQuery("SELECT g FROM Grade g", Grade.class);
        return query.getResultList();
    }

    @Override
    public Double calculateGPA(String studentId) {
        TypedQuery<Double> query = entityManager.createQuery(
            "SELECT g.gradePoint FROM Grade g WHERE g.studentId = :sid", Double.class);
        query.setParameter("sid", studentId);
        List<Double> points = query.getResultList();

        if (points.isEmpty()) return 0.0;
        
        double sum = 0;
        for (Double p : points) sum += p;
        return sum / points.size();
    }

    @Override
    public String generateTranscript(String studentId) {
        TypedQuery<Grade> query = entityManager.createQuery(
            "SELECT g FROM Grade g WHERE g.studentId = :sid", Grade.class);
        query.setParameter("sid", studentId);
        List<Grade> grades = query.getResultList();

        Double gpa = calculateGPA(studentId);

        StringBuilder sb = new StringBuilder();
        sb.append("OFFICIAL ACADEMIC TRANSCRIPT\n");
        sb.append("Student ID: ").append(studentId).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("%-10s %-10s %-10s\n", "Course", "Grade", "Points"));
        sb.append("--------------------------------------------------\n");

        for (Grade g : grades) {
            sb.append(String.format("%-10s %-10s %-10.2f\n", 
                g.getCourseCode(), g.getGradeLetter(), g.getGradePoint()));
        }
        sb.append("--------------------------------------------------\n");
        sb.append("CGPA: ").append(String.format("%.2f", gpa)).append("\n");
        
        return sb.toString();
    }
}
