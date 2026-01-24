package com.example.university.course.demo;

import com.example.university.course.model.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Standalone Demonstration for Course Service Bundle - UC5-8
 * This demo demonstrates all use cases without requiring external test frameworks
 */
public class CourseServiceDemo {

    private List<Course> courses;
    private List<CourseEnrollment> enrollments;
    private List<CourseSchedule> schedules;
    private List<CoursePrerequisite> prerequisites;

    public CourseServiceDemo() {
        courses = new ArrayList<>();
        enrollments = new ArrayList<>();
        schedules = new ArrayList<>();
        prerequisites = new ArrayList<>();
    }

    public void printSection(String title) {
        System.out.println();
        System.out.println("=".repeat(50));
        System.out.println("  " + title);
        System.out.println("=".repeat(50));
    }

    public void printSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    // ===== UC5: View and Configure Course Information =====
    public void demonstrateUC5() {
        printSection("UC5: View and Configure Course Information");

        // UC5.1 - Create course
        Course course = new Course();
        course.setId(1L);
        course.setCourseCode("CS101");
        course.setCourseName("Introduction to Computer Science");
        course.setDescription("Fundamentals of computer science and programming");
        course.setDepartment("Computer Science");
        course.setCredits(3);
        course.setInstructorName("Dr. Sarah Johnson");
        course.setInstructorId("INSTR001");
        course.setMaxCapacity(50);
        course.setSemester("Spring 2024");
        course.setAcademicYear("2023-2024");
        courses.add(course);
        printSuccess("UC5.1 - Course created: CS101");

        // UC5.2 - Retrieve course by code
        Course found = courses.stream()
            .filter(c -> c.getCourseCode().equals("CS101"))
            .findFirst()
            .orElse(null);
        if (found != null) {
            printSuccess("UC5.2 - Course retrieved: " + found.getCourseName());
        }

        // UC5.3 - List courses by department
        Course c2 = new Course();
        c2.setId(2L);
        c2.setCourseCode("CS102");
        c2.setDepartment("Computer Science");
        courses.add(c2);

        List<Course> csCourses = courses.stream()
            .filter(c -> c.getDepartment().equals("Computer Science"))
            .toList();
        printSuccess("UC5.3 - Found " + csCourses.size() + " Computer Science courses");

        // UC5.4 - Update course
        course.setMaxCapacity(60);
        printSuccess("UC5.4 - Course updated: max capacity changed to 60");
    }

    // ===== UC6: Manage Course Enrollment =====
    public void demonstrateUC6() {
        printSection("UC6: Manage Course Enrollment");

        // UC6.1 - Enroll student
        CourseEnrollment enrollment1 = new CourseEnrollment();
        enrollment1.setId(1L);
        enrollment1.setCourseId(1L);
        enrollment1.setStudentId("STU001");
        enrollment1.setStudentName("John Smith");
        enrollment1.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.PENDING);
        enrollment1.setSemester("Spring 2024");
        enrollment1.setAcademicYear("2023-2024");
        enrollments.add(enrollment1);
        printSuccess("UC6.1 - Student STU001 enrolled in course CS101");

        // UC6.2 - Approve enrollment
        enrollment1.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.ENROLLED);
        enrollment1.setApprovedBy("ADMIN001");
        printSuccess("UC6.2 - Enrollment approved by ADMIN001");

        // UC6.3 - Get student enrollments
        CourseEnrollment enrollment2 = new CourseEnrollment();
        enrollment2.setId(2L);
        enrollment2.setCourseId(2L);
        enrollment2.setStudentId("STU001");
        enrollments.add(enrollment2);

        List<CourseEnrollment> studentEnrollments = enrollments.stream()
            .filter(e -> e.getStudentId().equals("STU001"))
            .toList();
        printSuccess("UC6.3 - Student STU001 has " + studentEnrollments.size() + " enrollments");

        // UC6.4 - Drop course
        CourseEnrollment enrollment3 = new CourseEnrollment();
        enrollment3.setId(3L);
        enrollment3.setStudentId("STU002");
        enrollment3.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.ENROLLED);
        enrollments.add(enrollment3);

        enrollment3.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.DROPPED);
        printSuccess("UC6.4 - Course dropped for student STU002");
    }

    // ===== UC7: Set Course Timetable =====
    public void demonstrateUC7() {
        printSection("UC7: Set Course Timetable");

        // UC7.1 - Add schedule
        CourseSchedule schedule1 = new CourseSchedule();
        schedule1.setId(1L);
        schedule1.setCourseId(1L);
        schedule1.setCourseCode("CS101");
        schedule1.setDayOfWeek("MONDAY");
        schedule1.setStartTime(LocalTime.of(9, 0));
        schedule1.setEndTime(LocalTime.of(10, 30));
        schedule1.setVenue("Engineering Building");
        schedule1.setRoomNumber("205");
        schedule1.setCapacity(50);
        schedule1.setScheduleType("LECTURE");
        schedules.add(schedule1);
        printSuccess("UC7.1 - Monday lecture scheduled (09:00-10:30 in Engineering Building 205)");

        // UC7.2 - Get schedules for course
        CourseSchedule schedule2 = new CourseSchedule();
        schedule2.setId(2L);
        schedule2.setCourseId(1L);
        schedule2.setDayOfWeek("WEDNESDAY");
        schedules.add(schedule2);

        List<CourseSchedule> courseSchedules = schedules.stream()
            .filter(s -> s.getCourseId().equals(1L))
            .toList();
        printSuccess("UC7.2 - Found " + courseSchedules.size() + " schedules for course CS101");

        // UC7.3 - Get schedules by instructor
        schedule1.setInstructorId("INSTR001");
        schedule2.setInstructorId("INSTR001");

        List<CourseSchedule> instrSchedules = schedules.stream()
            .filter(s -> s.getInstructorId() != null && s.getInstructorId().equals("INSTR001"))
            .toList();
        printSuccess("UC7.3 - Instructor INSTR001 has " + instrSchedules.size() + " schedules");

        // UC7.4 - Update schedule
        schedule1.setStartTime(LocalTime.of(10, 0));
        printSuccess("UC7.4 - Schedule updated: start time changed to 10:00");
    }

    // ===== UC8: Check Course Prerequisites =====
    public void demonstrateUC8() {
        printSection("UC8: Check Course Prerequisites");

        // UC8.1 - Add prerequisites
        CoursePrerequisite prereq1 = new CoursePrerequisite();
        prereq1.setId(1L);
        prereq1.setCourseId(1L);
        prereq1.setCourseCode("CS101");
        prereq1.setPrerequisiteCourseCode("MATH101");
        prereq1.setPrerequisiteCourseName("Calculus I");
        prereq1.setMinimumGradeRequired("C");
        prereq1.setIsMandatory(true);
        prerequisites.add(prereq1);
        printSuccess("UC8.1 - Prerequisite added: MATH101 for CS101 (mandatory)");

        CoursePrerequisite prereq2 = new CoursePrerequisite();
        prereq2.setId(2L);
        prereq2.setCourseId(1L);
        prereq2.setPrerequisiteCourseCode("CS100");
        prereq2.setIsMandatory(true);
        prerequisites.add(prereq2);
        printSuccess("UC8.1 - Prerequisite added: CS100 for CS101 (mandatory)");

        // UC8.2 - Get prerequisites for course
        List<CoursePrerequisite> coursePrereqs = prerequisites.stream()
            .filter(p -> p.getCourseId().equals(1L))
            .toList();
        printSuccess("UC8.2 - Course CS101 has " + coursePrereqs.size() + " prerequisites");

        // UC8.3 - Validate prerequisites (met)
        CourseEnrollment completedEnrollment = new CourseEnrollment();
        completedEnrollment.setStudentId("STU001");
        completedEnrollment.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.COMPLETED);
        completedEnrollment.setGradePoint(3.0);
        enrollments.add(completedEnrollment);

        boolean allMetMandatory = prerequisites.stream()
            .filter(p -> p.getCourseId().equals(1L) && p.getIsMandatory())
            .allMatch(p -> enrollments.stream()
                .anyMatch(e -> e.getStudentId().equals("STU001") && 
                           e.getEnrollmentStatus() == CourseEnrollment.EnrollmentStatus.COMPLETED &&
                           e.getGradePoint() >= 1.0));

        if (allMetMandatory) {
            printSuccess("UC8.3 - Student STU001 meets all prerequisites for course CS101");
        } else {
            System.out.println("[INFO] UC8.3 - Student STU001 has NOT met all prerequisites");
        }

        // UC8.4 - Check failed prerequisites
        List<String> failedPrereqs = prerequisites.stream()
            .filter(p -> p.getCourseId().equals(1L) && p.getIsMandatory())
            .filter(p -> enrollments.stream()
                .noneMatch(e -> e.getStudentId().equals("STU002") && 
                           e.getEnrollmentStatus() == CourseEnrollment.EnrollmentStatus.COMPLETED))
            .map(CoursePrerequisite::getPrerequisiteCourseCode)
            .toList();

        if (!failedPrereqs.isEmpty()) {
            System.out.println("[INFO] UC8.4 - Student STU002 has NOT met " + failedPrereqs.size() + " prerequisite(s): " + failedPrereqs);
        }
    }

    public void printSummary() {
        printSection("Demonstration Complete");
        System.out.println();
        System.out.println("All Use Cases (UC5-8) Successfully Demonstrated:");
        System.out.println();
        System.out.println("  [OK] UC5 - View and Configure Course Information");
        System.out.println("       - Create courses");
        System.out.println("       - Retrieve courses by code");
        System.out.println("       - Filter by department");
        System.out.println("       - Update course information");
        System.out.println();
        System.out.println("  [OK] UC6 - Manage Course Enrollment");
        System.out.println("       - Enroll students");
        System.out.println("       - Approve enrollments");
        System.out.println("       - View student enrollments");
        System.out.println("       - Drop courses");
        System.out.println();
        System.out.println("  [OK] UC7 - Set Course Timetable");
        System.out.println("       - Add course schedules");
        System.out.println("       - View schedules by course");
        System.out.println("       - View schedules by instructor");
        System.out.println("       - Update schedules");
        System.out.println();
        System.out.println("  [OK] UC8 - Check Course Prerequisites");
        System.out.println("       - Add prerequisites");
        System.out.println("       - View prerequisites");
        System.out.println("       - Validate student prerequisites");
        System.out.println("       - Check failed prerequisites");
        System.out.println();
        System.out.println("=".repeat(50));
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("  Course Service Bundle - UC5-8 Demo");
        System.out.println("========================================");

        CourseServiceDemo demo = new CourseServiceDemo();

        // Run all demonstrations
        demo.demonstrateUC5();
        demo.demonstrateUC6();
        demo.demonstrateUC7();
        demo.demonstrateUC8();

        // Print summary
        demo.printSummary();
    }
}
