package com.example.university.student.service.impl;

import com.example.university.student.model.Student;
import com.example.university.student.model.AcademicProfile;
import com.example.university.student.model.DisciplinaryRecord;
import com.example.university.student.model.Enrollment;
import com.example.university.student.service.StudentService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Student Service Implementation using OSGi Declarative Services
 * Provides student management functionality with database persistence
 * 
 * This implementation uses JPA/EntityManager to persist data to PostgreSQL database.
 * All CRUD operations (create, read, update, delete) are performed using EntityManager
 * which handles database transactions and persistence automatically.
 * 
 * Database configuration:
 * - DataSource: universityDS (configured in Karaf)
 * - Persistence Unit: student-service-pu (defined in persistence.xml)
 * - Entities: Student, AcademicProfile, DisciplinaryRecord, Enrollment
 */
@Component(service = StudentService.class, immediate = true)
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    private EntityManager entityManager; // Injected by OSGi - provides database access

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Student Management
    @Override
    public Student createStudent(Student student) {
        logger.info("Creating student: {}", student.getStudentId());
        entityManager.persist(student);
        entityManager.flush();
        return student;
    }

    @Override
    public Student getStudentById(Long id) {
        return entityManager.find(Student.class, id);
    }

    @Override
    public Student getStudentByStudentId(String studentId) {
        TypedQuery<Student> query = entityManager.createQuery(
            "SELECT s FROM Student s WHERE s.studentId = :studentId", Student.class);
        query.setParameter("studentId", studentId);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Student updateStudent(Student student) {
        logger.info("Updating student: {}", student.getStudentId());
        return entityManager.merge(student);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        if (student != null) {
            logger.info("Deleting student: {}", student.getStudentId());
            entityManager.remove(student);
        }
    }

    @Override
    public List<Student> getAllStudents() {
        TypedQuery<Student> query = entityManager.createQuery(
            "SELECT s FROM Student s ORDER BY s.lastName, s.firstName", Student.class);
        return query.getResultList();
    }

    @Override
    public List<Student> getStudentsByStatus(Student.StudentStatus status) {
        TypedQuery<Student> query = entityManager.createQuery(
            "SELECT s FROM Student s WHERE s.status = :status ORDER BY s.lastName, s.firstName", Student.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<Student> searchStudents(String keyword) {
        String searchPattern = "%" + keyword.toLowerCase() + "%";
        TypedQuery<Student> query = entityManager.createQuery(
            "SELECT s FROM Student s WHERE " +
            "LOWER(s.studentId) LIKE :keyword OR " +
            "LOWER(s.firstName) LIKE :keyword OR " +
            "LOWER(s.lastName) LIKE :keyword OR " +
            "LOWER(s.email) LIKE :keyword " +
            "ORDER BY s.lastName, s.firstName", Student.class);
        query.setParameter("keyword", searchPattern);
        return query.getResultList();
    }

    // Academic Profile Management
    @Override
    public AcademicProfile getAcademicProfileByStudentId(String studentId) {
        TypedQuery<AcademicProfile> query = entityManager.createQuery(
            "SELECT ap FROM AcademicProfile ap WHERE ap.studentId = :studentId", AcademicProfile.class);
        query.setParameter("studentId", studentId);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public AcademicProfile updateAcademicProfile(AcademicProfile profile) {
        logger.info("Updating academic profile for student: {}", profile.getStudentId());
        return entityManager.merge(profile);
    }

    @Override
    public AcademicProfile createAcademicProfile(AcademicProfile profile) {
        logger.info("Creating academic profile for student: {}", profile.getStudentId());
        entityManager.persist(profile);
        entityManager.flush();
        return profile;
    }

    // Disciplinary Record Management
    @Override
    public DisciplinaryRecord createDisciplinaryRecord(DisciplinaryRecord record) {
        logger.info("Creating disciplinary record for student: {}", record.getStudentId());
        entityManager.persist(record);
        entityManager.flush();
        return record;
    }

    @Override
    public DisciplinaryRecord getDisciplinaryRecordById(Long id) {
        return entityManager.find(DisciplinaryRecord.class, id);
    }

    @Override
    public List<DisciplinaryRecord> getDisciplinaryRecordsByStudentId(String studentId) {
        TypedQuery<DisciplinaryRecord> query = entityManager.createQuery(
            "SELECT dr FROM DisciplinaryRecord dr WHERE dr.studentId = :studentId " +
            "ORDER BY dr.incidentDate DESC", DisciplinaryRecord.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }

    @Override
    public DisciplinaryRecord updateDisciplinaryRecord(DisciplinaryRecord record) {
        logger.info("Updating disciplinary record: {}", record.getId());
        return entityManager.merge(record);
    }

    @Override
    public void deleteDisciplinaryRecord(Long id) {
        DisciplinaryRecord record = getDisciplinaryRecordById(id);
        if (record != null) {
            logger.info("Deleting disciplinary record: {}", id);
            entityManager.remove(record);
        }
    }

    // Enrollment Management
    @Override
    public Enrollment createEnrollment(Enrollment enrollment) {
        logger.info("Creating enrollment for student: {} in course: {}", 
            enrollment.getStudentId(), enrollment.getCourseCode());
        entityManager.persist(enrollment);
        entityManager.flush();
        return enrollment;
    }

    @Override
    public Enrollment getEnrollmentById(Long id) {
        return entityManager.find(Enrollment.class, id);
    }

    @Override
    public List<Enrollment> getEnrollmentsByStudentId(String studentId) {
        TypedQuery<Enrollment> query = entityManager.createQuery(
            "SELECT e FROM Enrollment e WHERE e.studentId = :studentId " +
            "ORDER BY e.academicYear DESC, e.semester DESC", Enrollment.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourseId(String courseId) {
        TypedQuery<Enrollment> query = entityManager.createQuery(
            "SELECT e FROM Enrollment e WHERE e.courseId = :courseId " +
            "ORDER BY e.studentId", Enrollment.class);
        query.setParameter("courseId", courseId);
        return query.getResultList();
    }

    @Override
    public List<Enrollment> getEnrollmentsBySemester(String semester, String academicYear) {
        TypedQuery<Enrollment> query = entityManager.createQuery(
            "SELECT e FROM Enrollment e WHERE e.semester = :semester AND e.academicYear = :academicYear " +
            "ORDER BY e.studentId", Enrollment.class);
        query.setParameter("semester", semester);
        query.setParameter("academicYear", academicYear);
        return query.getResultList();
    }

    @Override
    public Enrollment updateEnrollment(Enrollment enrollment) {
        logger.info("Updating enrollment: {}", enrollment.getId());
        return entityManager.merge(enrollment);
    }

    @Override
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = getEnrollmentById(id);
        if (enrollment != null) {
            logger.info("Deleting enrollment: {}", id);
            entityManager.remove(enrollment);
        }
    }

    @Override
    public Enrollment dropEnrollment(Long id, String reason) {
        Enrollment enrollment = getEnrollmentById(id);
        if (enrollment != null) {
            enrollment.setEnrollmentStatus(Enrollment.EnrollmentStatus.DROPPED);
            enrollment.setNotes(reason);
            logger.info("Dropping enrollment: {} - Reason: {}", id, reason);
            return entityManager.merge(enrollment);
        }
        return null;
    }
}
