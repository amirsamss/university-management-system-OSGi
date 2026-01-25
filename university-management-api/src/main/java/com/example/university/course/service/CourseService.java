package com.example.university.course.service;

import com.example.university.course.model.Course;
import com.example.university.course.model.CourseEnrollment;
import com.example.university.course.model.CourseSchedule;
import com.example.university.course.model.CoursePrerequisite;
import java.util.List;

/**
 * Course Service Interface - UC5 to UC8
 * Manages course catalog, enrollment, timetable, and prerequisites
 */
public interface CourseService {
    
    // UC5: View Course Information / Configure Course Catalog
    Course addCourse(Course course);
    Course updateCourse(Course course);
    Course getCourseById(Long id);
    Course getCourseByCourseCode(String courseCode);
    List<Course> getAllCourses();
    List<Course> getCoursesByDepartment(String department);
    List<Course> getCoursesByAcademicYear(String academicYear);
    List<Course> getCoursesBySemester(String semester);
    void deleteCourse(Long id);
    
    // UC6: Manage Course Enrollment
    CourseEnrollment enrollStudent(CourseEnrollment enrollment);
    CourseEnrollment updateEnrollment(CourseEnrollment enrollment);
    CourseEnrollment getEnrollmentById(Long id);
    CourseEnrollment getEnrollmentByStudentAndCourse(String studentId, Long courseId);
    List<CourseEnrollment> getEnrollmentsByStudent(String studentId);
    List<CourseEnrollment> getEnrollmentsByCourse(Long courseId);
    List<CourseEnrollment> getEnrollmentsByAcademicYear(String academicYear);
    void dropCourse(Long enrollmentId);
    void approveEnrollment(Long enrollmentId, String approvedBy);
    boolean validateEnrollmentCapacity(Long courseId);
    
    // UC7: Set Course Timetable
    CourseSchedule addSchedule(CourseSchedule schedule);
    CourseSchedule updateSchedule(CourseSchedule schedule);
    CourseSchedule getScheduleById(Long id);
    List<CourseSchedule> getSchedulesByCourse(Long courseId);
    List<CourseSchedule> getSchedulesByInstructor(String instructorId);
    List<CourseSchedule> getSchedulesByVenue(String venue);
    List<CourseSchedule> getSchedulesByDayOfWeek(String dayOfWeek);
    void deleteSchedule(Long id);
    
    // UC8: Check Course Prerequisites
    CoursePrerequisite addPrerequisite(CoursePrerequisite prerequisite);
    CoursePrerequisite updatePrerequisite(CoursePrerequisite prerequisite);
    CoursePrerequisite getPrerequisiteById(Long id);
    List<CoursePrerequisite> getPrerequisitesByCourse(Long courseId);
    List<CoursePrerequisite> getPrerequisitesForStudent(String studentId, Long courseId);
    void deletePrerequisite(Long id);
    boolean validateStudentPrerequisites(String studentId, Long courseId);
    List<String> getFailedPrerequisites(String studentId, Long courseId);
}
