package com.example.university.exam.service.impl;

import com.example.university.exam.model.Exam;
import com.example.university.exam.model.Grade;
import com.example.university.exam.service.GradingService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

@Component(service = GradingService.class)
public class GradingServiceImpl implements GradingService {

    private static final Logger logger = LoggerFactory.getLogger(GradingServiceImpl.class);
    private EntityManagerFactory emf;

    public GradingServiceImpl() {
        // In a real OSGi app, this is often injected, but we'll create it manually for simplicity
        // matching the persistence.xml unit name we will create later
        try {
            this.emf = Persistence.createEntityManagerFactory("exam-pu");
        } catch (Exception e) {
            logger.error("Failed to create EntityManagerFactory", e);
        }
    }

    @Override
    public Exam scheduleExam(Exam exam) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(exam);
            em.getTransaction().commit();
            return exam;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Exam> getAllExams() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Exam e", Exam.class).getResultList();
        } finally {
            em.close();
        }
    }

   // 1. Submit Grade
    @Override
    public Grade submitGrade(Grade grade) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // Ensure metrics are calculated if only marks are sent
            if (grade.getMarks() != null) {
                grade.setMarks(grade.getMarks());
            }
            em.persist(grade);
            em.getTransaction().commit();
            return grade;
        } finally {
            em.close();
        }
    }

    // 2. Get All Grades
    @Override
    public List<Grade> getAllGrades() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT g FROM Grade g", Grade.class).getResultList();
        } finally {
            em.close();
        }
    }

    // 3. Calculate CGPA (UC-15)
    @Override
    public Double calculateGPA(String studentId) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Double> points = em.createQuery(
                "SELECT g.gradePoint FROM Grade g WHERE g.studentId = :sid", Double.class)
                .setParameter("sid", studentId)
                .getResultList();

            if (points.isEmpty()) return 0.0;
            
            double sum = 0;
            for (Double p : points) sum += p;
            return sum / points.size();
        } finally {
            em.close();
        }
    }

    // 4. Generate Transcript (UC-16)
    @Override
    public String generateTranscript(String studentId) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Grade> grades = em.createQuery(
                "SELECT g FROM Grade g WHERE g.studentId = :sid", Grade.class)
                .setParameter("sid", studentId)
                .getResultList();

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
        } finally {
            em.close();
        }
    }
}
