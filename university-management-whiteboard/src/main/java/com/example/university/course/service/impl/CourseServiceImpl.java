package com.example.university.course.service.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

import com.example.university.course.service.CourseService;
import com.example.university.course.model.Course;
import com.example.university.course.model.CourseEnrollment;
import com.example.university.course.model.CourseSchedule;
import com.example.university.course.model.CoursePrerequisite;

/**
 * Course Service Implementation - UC5 to UC8
 */
@Component(service = CourseService.class, immediate = true)
public class CourseServiceImpl implements CourseService {

    private EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Course addCourse(Course course) {
        entityManager.persist(course);
        entityManager.flush();
        return course;
    }

    @Override
    public Course updateCourse(Course course) {
        return entityManager.merge(course);
    }

    @Override
    public Course getCourseById(Long id) {
        return entityManager.find(Course.class, id);
    }

    @Override
    public Course getCourseByCourseCode(String courseCode) {
        TypedQuery<Course> query = entityManager.createQuery(
            "SELECT c FROM Course c WHERE c.courseCode = :code", Course.class);
        query.setParameter("code", courseCode);
        List<Course> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<Course> getAllCourses() {
        TypedQuery<Course> query = entityManager.createQuery(
            "SELECT c FROM Course c ORDER BY c.courseCode", Course.class);
        return query.getResultList();
    }

    @Override
    public List<Course> getCoursesByDepartment(String department) {
        TypedQuery<Course> query = entityManager.createQuery(
            "SELECT c FROM Course c WHERE c.department = :dept ORDER BY c.courseCode", Course.class);
        query.setParameter("dept", department);
        return query.getResultList();
    }

    @Override
    public List<Course> getCoursesByAcademicYear(String academicYear) {
        TypedQuery<Course> query = entityManager.createQuery(
            "SELECT c FROM Course c WHERE c.academicYear = :year ORDER BY c.courseCode", Course.class);
        query.setParameter("year", academicYear);
        return query.getResultList();
    }

    @Override
    public List<Course> getCoursesBySemester(String semester) {
        TypedQuery<Course> query = entityManager.createQuery(
            "SELECT c FROM Course c WHERE c.semester = :sem ORDER BY c.courseCode", Course.class);
        query.setParameter("sem", semester);
        return query.getResultList();
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = entityManager.find(Course.class, id);
        if (course != null) {
            entityManager.remove(course);
        }
    }

    @Override
    public CourseEnrollment enrollStudent(CourseEnrollment enrollment) {
        entityManager.persist(enrollment);
        entityManager.flush();
        return enrollment;
    }

    @Override
    public CourseEnrollment updateEnrollment(CourseEnrollment enrollment) {
        return entityManager.merge(enrollment);
    }

    @Override
    public CourseEnrollment getEnrollmentById(Long id) {
        return entityManager.find(CourseEnrollment.class, id);
    }

    @Override
    public CourseEnrollment getEnrollmentByStudentAndCourse(String studentId, Long courseId) {
        TypedQuery<CourseEnrollment> query = entityManager.createQuery(
            "SELECT e FROM CourseEnrollment e WHERE e.studentId = :studentId AND e.courseId = :courseId", 
            CourseEnrollment.class);
        query.setParameter("studentId", studentId);
        query.setParameter("courseId", courseId);
        List<CourseEnrollment> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<CourseEnrollment> getEnrollmentsByStudent(String studentId) {
        TypedQuery<CourseEnrollment> query = entityManager.createQuery(
            "SELECT e FROM CourseEnrollment e WHERE e.studentId = :studentId ORDER BY e.enrollmentDate DESC", 
            CourseEnrollment.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }

    @Override
    public List<CourseEnrollment> getEnrollmentsByCourse(Long courseId) {
        TypedQuery<CourseEnrollment> query = entityManager.createQuery(
            "SELECT e FROM CourseEnrollment e WHERE e.courseId = :courseId ORDER BY e.studentId", 
            CourseEnrollment.class);
        query.setParameter("courseId", courseId);
        return query.getResultList();
    }

    @Override
    public List<CourseEnrollment> getEnrollmentsByAcademicYear(String academicYear) {
        TypedQuery<CourseEnrollment> query = entityManager.createQuery(
            "SELECT e FROM CourseEnrollment e WHERE e.academicYear = :year ORDER BY e.studentId", 
            CourseEnrollment.class);
        query.setParameter("year", academicYear);
        return query.getResultList();
    }

    @Override
    public void dropCourse(Long enrollmentId) {
        CourseEnrollment enrollment = entityManager.find(CourseEnrollment.class, enrollmentId);
        if (enrollment != null) {
            enrollment.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.DROPPED);
            entityManager.merge(enrollment);
        }
    }

    @Override
    public void approveEnrollment(Long enrollmentId, String approvedBy) {
        CourseEnrollment enrollment = entityManager.find(CourseEnrollment.class, enrollmentId);
        if (enrollment != null) {
            enrollment.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.ENROLLED);
            enrollment.setApprovedBy(approvedBy);
            enrollment.setApprovalDate(java.time.LocalDateTime.now());
            entityManager.merge(enrollment);
        }
    }

    @Override
    public boolean validateEnrollmentCapacity(Long courseId) {
        Course course = entityManager.find(Course.class, courseId);
        if (course == null) {
            return false;
        }
        return course.getCurrentEnrollment() < course.getMaxCapacity();
    }

    @Override
    public CourseSchedule addSchedule(CourseSchedule schedule) {
        entityManager.persist(schedule);
        entityManager.flush();
        return schedule;
    }

    @Override
    public CourseSchedule updateSchedule(CourseSchedule schedule) {
        return entityManager.merge(schedule);
    }

    @Override
    public CourseSchedule getScheduleById(Long id) {
        return entityManager.find(CourseSchedule.class, id);
    }

    @Override
    public List<CourseSchedule> getSchedulesByCourse(Long courseId) {
        TypedQuery<CourseSchedule> query = entityManager.createQuery(
            "SELECT s FROM CourseSchedule s WHERE s.courseId = :courseId ORDER BY s.dayOfWeek, s.startTime", 
            CourseSchedule.class);
        query.setParameter("courseId", courseId);
        return query.getResultList();
    }

    @Override
    public List<CourseSchedule> getSchedulesByInstructor(String instructorId) {
        TypedQuery<CourseSchedule> query = entityManager.createQuery(
            "SELECT s FROM CourseSchedule s WHERE s.instructorId = :instructorId ORDER BY s.dayOfWeek, s.startTime", 
            CourseSchedule.class);
        query.setParameter("instructorId", instructorId);
        return query.getResultList();
    }

    @Override
    public List<CourseSchedule> getSchedulesByVenue(String venue) {
        TypedQuery<CourseSchedule> query = entityManager.createQuery(
            "SELECT s FROM CourseSchedule s WHERE s.venue = :venue ORDER BY s.dayOfWeek, s.startTime", 
            CourseSchedule.class);
        query.setParameter("venue", venue);
        return query.getResultList();
    }

    @Override
    public List<CourseSchedule> getSchedulesByDayOfWeek(String dayOfWeek) {
        TypedQuery<CourseSchedule> query = entityManager.createQuery(
            "SELECT s FROM CourseSchedule s WHERE s.dayOfWeek = :day ORDER BY s.startTime", 
            CourseSchedule.class);
        query.setParameter("day", dayOfWeek);
        return query.getResultList();
    }

    @Override
    public void deleteSchedule(Long id) {
        CourseSchedule schedule = entityManager.find(CourseSchedule.class, id);
        if (schedule != null) {
            entityManager.remove(schedule);
        }
    }

    @Override
    public CoursePrerequisite addPrerequisite(CoursePrerequisite prerequisite) {
        entityManager.persist(prerequisite);
        entityManager.flush();
        return prerequisite;
    }

    @Override
    public CoursePrerequisite updatePrerequisite(CoursePrerequisite prerequisite) {
        return entityManager.merge(prerequisite);
    }

    @Override
    public CoursePrerequisite getPrerequisiteById(Long id) {
        return entityManager.find(CoursePrerequisite.class, id);
    }

    @Override
    public List<CoursePrerequisite> getPrerequisitesByCourse(Long courseId) {
        TypedQuery<CoursePrerequisite> query = entityManager.createQuery(
            "SELECT p FROM CoursePrerequisite p WHERE p.courseId = :courseId", 
            CoursePrerequisite.class);
        query.setParameter("courseId", courseId);
        return query.getResultList();
    }

    @Override
    public List<CoursePrerequisite> getPrerequisitesForStudent(String studentId, Long courseId) {
        TypedQuery<CoursePrerequisite> query = entityManager.createQuery(
            "SELECT p FROM CoursePrerequisite p WHERE p.courseId = :courseId", 
            CoursePrerequisite.class);
        query.setParameter("courseId", courseId);
        return query.getResultList();
    }

    @Override
    public void deletePrerequisite(Long id) {
        CoursePrerequisite prerequisite = entityManager.find(CoursePrerequisite.class, id);
        if (prerequisite != null) {
            entityManager.remove(prerequisite);
        }
    }

    @Override
    public boolean validateStudentPrerequisites(String studentId, Long courseId) {
        List<CoursePrerequisite> prerequisites = getPrerequisitesByCourse(courseId);
        for (CoursePrerequisite prereq : prerequisites) {
            if (Boolean.TRUE.equals(prereq.getIsMandatory())) {
                TypedQuery<CourseEnrollment> query = entityManager.createQuery(
                    "SELECT e FROM CourseEnrollment e WHERE e.studentId = :studentId " +
                    "AND EXISTS (SELECT c FROM Course c WHERE c.id = e.courseId AND c.courseCode = :prereqCode) " +
                    "AND e.enrollmentStatus = 'COMPLETED' AND e.gradePoint >= 1.0",
                    CourseEnrollment.class);
                query.setParameter("studentId", studentId);
                query.setParameter("prereqCode", prereq.getPrerequisiteCourseCode());
                List<CourseEnrollment> results = query.getResultList();
                if (results.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<String> getFailedPrerequisites(String studentId, Long courseId) {
        List<String> failedPrereqs = new java.util.ArrayList<>();
        List<CoursePrerequisite> prerequisites = getPrerequisitesByCourse(courseId);
        
        for (CoursePrerequisite prereq : prerequisites) {
            if (Boolean.TRUE.equals(prereq.getIsMandatory())) {
                TypedQuery<CourseEnrollment> query = entityManager.createQuery(
                    "SELECT e FROM CourseEnrollment e WHERE e.studentId = :studentId " +
                    "AND EXISTS (SELECT c FROM Course c WHERE c.id = e.courseId AND c.courseCode = :prereqCode) " +
                    "AND e.enrollmentStatus = 'COMPLETED' AND e.gradePoint >= 1.0",
                    CourseEnrollment.class);
                query.setParameter("studentId", studentId);
                query.setParameter("prereqCode", prereq.getPrerequisiteCourseCode());
                List<CourseEnrollment> results = query.getResultList();
                if (results.isEmpty()) {
                    failedPrereqs.add(prereq.getPrerequisiteCourseCode() + " (" + 
                                     prereq.getPrerequisiteCourseName() + ")");
                }
            }
        }
        return failedPrereqs;
    }
}
