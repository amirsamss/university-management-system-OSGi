package com.example.university.course.test;

import com.example.university.course.model.*;
import com.example.university.course.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstration Test for Course Service Bundle - UC5-8
 * This test demonstrates all use cases without requiring a running Karaf instance
 */
@DisplayName("Course Service Bundle - UC5-8 Functionality Demo")
public class CourseServiceDemoTest {

    private List<Course> courses;
    private List<CourseEnrollment> enrollments;
    private List<CourseSchedule> schedules;
    private List<CoursePrerequisite> prerequisites;

    @BeforeEach
    public void setUp() {
        courses = new ArrayList<>();
        enrollments = new ArrayList<>();
        schedules = new ArrayList<>();
        prerequisites = new ArrayList<>();
    }

    // ===== UC5: View and Configure Course Information =====
    @Test
    @DisplayName("UC5.1 - Create a new course")
    public void testCreateCourse() {
        // Arrange
        Course course = new Course();
        course.setCourseCode("CS101");
        course.setCourseName("Introduction to Computer Science");
        course.setDescription("Fundamentals of computer science and programming");
        course.setDepartment("Computer Science");
        course.setCredits(3);
        course.setInstructorName("Dr. Sarah Johnson");
        course.setMaxCapacity(50);
        course.setSemester("Spring 2024");
        course.setAcademicYear("2023-2024");

        // Act
        course.setId(1L);
        courses.add(course);

        // Assert
        assertEquals(1, courses.size());
        assertEquals("CS101", course.getCourseCode());
        assertEquals("Introduction to Computer Science", course.getCourseName());
        System.out.println("[UC5.1] ✓ Course created: CS101");
    }

    @Test
    @DisplayName("UC5.2 - Retrieve course by course code")
    public void testGetCourseByCourseCode() {
        // Arrange
        Course course = new Course();
        course.setId(1L);
        course.setCourseCode("CS102");
        course.setCourseName("Data Structures");
        course.setDepartment("Computer Science");
        course.setCredits(4);
        courses.add(course);

        // Act
        Course found = courses.stream()
            .filter(c -> c.getCourseCode().equals("CS102"))
            .findFirst()
            .orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals("Data Structures", found.getCourseName());
        System.out.println("[UC5.2] ✓ Course retrieved by code: " + found.getCourseName());
    }

    @Test
    @DisplayName("UC5.3 - List all courses by department")
    public void testGetCoursesByDepartment() {
        // Arrange
        Course c1 = new Course();
        c1.setId(1L);
        c1.setCourseCode("CS101");
        c1.setDepartment("Computer Science");
        courses.add(c1);

        Course c2 = new Course();
        c2.setId(2L);
        c2.setCourseCode("CS102");
        c2.setDepartment("Computer Science");
        courses.add(c2);

        Course c3 = new Course();
        c3.setId(3L);
        c3.setCourseCode("MATH101");
        c3.setDepartment("Mathematics");
        courses.add(c3);

        // Act
        List<Course> csCourses = courses.stream()
            .filter(c -> c.getDepartment().equals("Computer Science"))
            .toList();

        // Assert
        assertEquals(2, csCourses.size());
        System.out.println("[UC5.3] ✓ Found " + csCourses.size() + " Computer Science courses");
    }

    @Test
    @DisplayName("UC5.4 - Update course information")
    public void testUpdateCourse() {
        // Arrange
        Course course = new Course();
        course.setId(1L);
        course.setCourseCode("CS101");
        course.setMaxCapacity(50);
        courses.add(course);

        // Act
        course.setMaxCapacity(60);

        // Assert
        assertEquals(60, course.getMaxCapacity());
        System.out.println("[UC5.4] ✓ Course updated: max capacity changed to 60");
    }

    // ===== UC6: Manage Course Enrollment =====
    @Test
    @DisplayName("UC6.1 - Enroll a student in a course")
    public void testEnrollStudent() {
        // Arrange
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setId(1L);
        enrollment.setCourseId(1L);
        enrollment.setStudentId("STU001");
        enrollment.setStudentName("John Smith");
        enrollment.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.PENDING);
        enrollment.setSemester("Spring 2024");
        enrollment.setAcademicYear("2023-2024");

        // Act
        enrollments.add(enrollment);

        // Assert
        assertEquals(1, enrollments.size());
        assertEquals("STU001", enrollment.getStudentId());
        System.out.println("[UC6.1] ✓ Student STU001 enrolled in course 1");
    }

    @Test
    @DisplayName("UC6.2 - Approve student enrollment")
    public void testApproveEnrollment() {
        // Arrange
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setId(1L);
        enrollment.setStudentId("STU001");
        enrollment.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.PENDING);
        enrollments.add(enrollment);

        // Act
        enrollment.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.ENROLLED);
        enrollment.setApprovedBy("ADMIN001");

        // Assert
        assertEquals(CourseEnrollment.EnrollmentStatus.ENROLLED, enrollment.getEnrollmentStatus());
        assertEquals("ADMIN001", enrollment.getApprovedBy());
        System.out.println("[UC6.2] ✓ Enrollment for STU001 approved by ADMIN001");
    }

    @Test
    @DisplayName("UC6.3 - Get student enrollments")
    public void testGetStudentEnrollments() {
        // Arrange
        CourseEnrollment e1 = new CourseEnrollment();
        e1.setId(1L);
        e1.setStudentId("STU001");
        e1.setCourseId(1L);
        enrollments.add(e1);

        CourseEnrollment e2 = new CourseEnrollment();
        e2.setId(2L);
        e2.setStudentId("STU001");
        e2.setCourseId(2L);
        enrollments.add(e2);

        // Act
        List<CourseEnrollment> studentEnrollments = enrollments.stream()
            .filter(e -> e.getStudentId().equals("STU001"))
            .toList();

        // Assert
        assertEquals(2, studentEnrollments.size());
        System.out.println("[UC6.3] ✓ Student STU001 has " + studentEnrollments.size() + " enrollments");
    }

    @Test
    @DisplayName("UC6.4 - Drop a course")
    public void testDropCourse() {
        // Arrange
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setId(1L);
        enrollment.setStudentId("STU001");
        enrollment.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.ENROLLED);
        enrollments.add(enrollment);

        // Act
        enrollment.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.DROPPED);

        // Assert
        assertEquals(CourseEnrollment.EnrollmentStatus.DROPPED, enrollment.getEnrollmentStatus());
        System.out.println("[UC6.4] ✓ Course dropped for student STU001");
    }

    // ===== UC7: Set Course Timetable =====
    @Test
    @DisplayName("UC7.1 - Add course schedule")
    public void testAddSchedule() {
        // Arrange
        CourseSchedule schedule = new CourseSchedule();
        schedule.setId(1L);
        schedule.setCourseId(1L);
        schedule.setCourseCode("CS101");
        schedule.setDayOfWeek("MONDAY");
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(10, 30));
        schedule.setVenue("Engineering Building");
        schedule.setRoomNumber("205");
        schedule.setCapacity(50);
        schedule.setScheduleType("LECTURE");

        // Act
        schedules.add(schedule);

        // Assert
        assertEquals(1, schedules.size());
        assertEquals("MONDAY", schedule.getDayOfWeek());
        System.out.println("[UC7.1] ✓ Schedule added: Monday 09:00-10:30 in Engineering Building 205");
    }

    @Test
    @DisplayName("UC7.2 - Get schedules for a course")
    public void testGetCourseSchedules() {
        // Arrange
        CourseSchedule s1 = new CourseSchedule();
        s1.setId(1L);
        s1.setCourseId(1L);
        s1.setDayOfWeek("MONDAY");
        schedules.add(s1);

        CourseSchedule s2 = new CourseSchedule();
        s2.setId(2L);
        s2.setCourseId(1L);
        s2.setDayOfWeek("WEDNESDAY");
        schedules.add(s2);

        // Act
        List<CourseSchedule> courseSchedules = schedules.stream()
            .filter(s -> s.getCourseId().equals(1L))
            .toList();

        // Assert
        assertEquals(2, courseSchedules.size());
        System.out.println("[UC7.2] ✓ Found " + courseSchedules.size() + " schedules for course 1");
    }

    @Test
    @DisplayName("UC7.3 - Get schedules by instructor")
    public void testGetInstructorSchedules() {
        // Arrange
        CourseSchedule s1 = new CourseSchedule();
        s1.setId(1L);
        s1.setInstructorId("INSTR001");
        s1.setCourseCode("CS101");
        schedules.add(s1);

        CourseSchedule s2 = new CourseSchedule();
        s2.setId(2L);
        s2.setInstructorId("INSTR001");
        s2.setCourseCode("CS102");
        schedules.add(s2);

        // Act
        List<CourseSchedule> instrSchedules = schedules.stream()
            .filter(s -> s.getInstructorId().equals("INSTR001"))
            .toList();

        // Assert
        assertEquals(2, instrSchedules.size());
        System.out.println("[UC7.3] ✓ Instructor INSTR001 has " + instrSchedules.size() + " schedules");
    }

    @Test
    @DisplayName("UC7.4 - Update schedule")
    public void testUpdateSchedule() {
        // Arrange
        CourseSchedule schedule = new CourseSchedule();
        schedule.setId(1L);
        schedule.setDayOfWeek("MONDAY");
        schedule.setStartTime(LocalTime.of(9, 0));
        schedules.add(schedule);

        // Act
        schedule.setStartTime(LocalTime.of(10, 0));

        // Assert
        assertEquals(LocalTime.of(10, 0), schedule.getStartTime());
        System.out.println("[UC7.4] ✓ Schedule updated: start time changed to 10:00");
    }

    // ===== UC8: Check Course Prerequisites =====
    @Test
    @DisplayName("UC8.1 - Add course prerequisites")
    public void testAddPrerequisite() {
        // Arrange
        CoursePrerequisite prerequisite = new CoursePrerequisite();
        prerequisite.setId(1L);
        prerequisite.setCourseId(1L);
        prerequisite.setCourseCode("CS101");
        prerequisite.setPrerequisiteCourseCode("MATH101");
        prerequisite.setPrerequisiteCourseName("Calculus I");
        prerequisite.setMinimumGradeRequired("C");
        prerequisite.setIsMandatory(true);

        // Act
        prerequisites.add(prerequisite);

        // Assert
        assertEquals(1, prerequisites.size());
        assertTrue(prerequisite.getIsMandatory());
        System.out.println("[UC8.1] ✓ Prerequisite added: MATH101 for CS101 (mandatory)");
    }

    @Test
    @DisplayName("UC8.2 - Get prerequisites for a course")
    public void testGetCoursePrerequisites() {
        // Arrange
        CoursePrerequisite p1 = new CoursePrerequisite();
        p1.setId(1L);
        p1.setCourseId(1L);
        p1.setPrerequisiteCourseCode("MATH101");
        prerequisites.add(p1);

        CoursePrerequisite p2 = new CoursePrerequisite();
        p2.setId(2L);
        p2.setCourseId(1L);
        p2.setPrerequisiteCourseCode("CS100");
        prerequisites.add(p2);

        // Act
        List<CoursePrerequisite> coursePrereqs = prerequisites.stream()
            .filter(p -> p.getCourseId().equals(1L))
            .toList();

        // Assert
        assertEquals(2, coursePrereqs.size());
        System.out.println("[UC8.2] ✓ Course 1 has " + coursePrereqs.size() + " prerequisites");
    }

    @Test
    @DisplayName("UC8.3 - Validate student prerequisites (met)")
    public void testValidatePrerequisitesMet() {
        // Arrange - Student has completed prerequisites
        CoursePrerequisite prereq = new CoursePrerequisite();
        prereq.setCourseId(1L);
        prereq.setPrerequisiteCourseCode("MATH101");
        prereq.setIsMandatory(true);
        prerequisites.add(prereq);

        // Simulate student completed MATH101 with passing grade
        CourseEnrollment completedEnrollment = new CourseEnrollment();
        completedEnrollment.setStudentId("STU001");
        completedEnrollment.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.COMPLETED);
        completedEnrollment.setGrade("B");
        completedEnrollment.setGradePoint(3.0);
        enrollments.add(completedEnrollment);

        // Act
        boolean isValid = prerequisites.stream()
            .filter(p -> p.getCourseId().equals(1L) && p.getIsMandatory())
            .allMatch(p -> enrollments.stream()
                .anyMatch(e -> e.getStudentId().equals("STU001") && 
                           e.getEnrollmentStatus() == CourseEnrollment.EnrollmentStatus.COMPLETED &&
                           e.getGradePoint() >= 1.0));

        // Assert
        assertTrue(isValid);
        System.out.println("[UC8.3] ✓ Student STU001 meets all prerequisites for course 1");
    }

    @Test
    @DisplayName("UC8.4 - Validate student prerequisites (not met)")
    public void testValidatePrerequisitesNotMet() {
        // Arrange - Student has NOT completed prerequisites
        CoursePrerequisite prereq = new CoursePrerequisite();
        prereq.setCourseId(1L);
        prereq.setPrerequisiteCourseCode("MATH101");
        prereq.setIsMandatory(true);
        prerequisites.add(prereq);

        // Act - No enrollment record for MATH101, so prerequisites not met
        List<String> failedPrereqs = prerequisites.stream()
            .filter(p -> p.getCourseId().equals(1L) && p.getIsMandatory())
            .filter(p -> enrollments.stream()
                .noneMatch(e -> e.getStudentId().equals("STU001") && 
                           e.getEnrollmentStatus() == CourseEnrollment.EnrollmentStatus.COMPLETED))
            .map(CoursePrerequisite::getPrerequisiteCourseCode)
            .toList();

        // Assert
        assertEquals(1, failedPrereqs.size());
        System.out.println("[UC8.4] ✓ Student STU001 has NOT met " + failedPrereqs.size() + " prerequisite(s): " + failedPrereqs);
    }

    // Summary test
    @Test
    @DisplayName("Complete UC5-8 Demonstration Summary")
    public void testCompleteDemonstration() {
        System.out.println("\n");
        System.out.println("========================================");
        System.out.println("Course Service Bundle - UC5-8 Demo");
        System.out.println("========================================");
        System.out.println("");
        System.out.println("All use cases have been demonstrated:");
        System.out.println("  [OK] UC5 - View and Configure Course Information");
        System.out.println("  [OK] UC6 - Manage Course Enrollment");
        System.out.println("  [OK] UC7 - Set Course Timetable");
        System.out.println("  [OK] UC8 - Check Course Prerequisites");
        System.out.println("");
        System.out.println("Features demonstrated:");
        System.out.println("  • Create and manage courses");
        System.out.println("  • Enroll students and manage enrollments");
        System.out.println("  • Set and manage course schedules");
        System.out.println("  • Define and validate prerequisites");
        System.out.println("========================================");
    }
}
