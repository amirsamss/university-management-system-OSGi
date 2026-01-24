package com.example.university.course.demo;

import com.example.university.course.model.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Course Service Demo - UC5-8
 * UC-05: Manage Course Catalog
 * UC-06: Validate Prerequisite Course
 * UC-07: Assign Course to Lecturer
 * UC-08: Retrieve Course Schedule/Rooms
 */
public class InteractiveCourseDemo {

    private List<Course> courses;
    private List<CourseEnrollment> enrollments;
    private List<CourseSchedule> schedules;
    private List<CoursePrerequisite> prerequisites;
    private Scanner scanner;
    private Long nextCourseId = 1L;
    private Long nextEnrollmentId = 1L;
    private Long nextScheduleId = 1L;
    private Long nextPrereqId = 1L;

    public InteractiveCourseDemo() {
        courses = new ArrayList<>();
        enrollments = new ArrayList<>();
        schedules = new ArrayList<>();
        prerequisites = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  INTERACTIVE COURSE SERVICE DEMO - UC5 to UC8");
        System.out.println("=".repeat(60));
        
        boolean running = true;
        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    uc5Menu();
                    break;
                case "2":
                    uc6Menu();
                    break;
                case "3":
                    uc7Menu();
                    break;
                case "4":
                    uc8Menu();
                    break;
                case "5":
                    viewAllData();
                    break;
                case "0":
                    running = false;
                    System.out.println("\n[INFO] Exiting demo. Thank you!");
                    break;
                default:
                    System.out.println("\n[ERROR] Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private void displayMainMenu() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("MAIN MENU - Select Use Case:");
        System.out.println("-".repeat(60));
        System.out.println("  [1] UC-05: Manage Course Catalog");
        System.out.println("  [2] UC-06: Validate Prerequisite Course");
        System.out.println("  [3] UC-07: Assign Course to Lecturer");
        System.out.println("  [4] UC-08: Retrieve Course Schedule/Rooms");
        System.out.println("  [5] View All Current Data");
        System.out.println("  [0] Exit");
        System.out.println("-".repeat(60));
        System.out.print("Enter your choice: ");
    }

    // ===== UC-05: Manage Course Catalog =====
    private void uc5Menu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  UC-05: MANAGE COURSE CATALOG");
        System.out.println("=".repeat(60));
        System.out.println("  [1] Create a new course (Add to catalog)");
        System.out.println("  [2] Retrieve/View course by code");
        System.out.println("  [3] List all courses in catalog");
        System.out.println("  [4] Update course information");
        System.out.println("  [5] Delete course from catalog");
        System.out.println("  [0] Back to main menu");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                createCourse();
                break;
            case "2":
                retrieveCourseByCode();
                break;
            case "3":
                listAllCourses();
                break;
            case "4":
                updateCourse();
                break;
            case "5":
                deleteCourse();
                break;
            case "0":
                return;
            default:
                System.out.println("\n[ERROR] Invalid choice.");
        }
    }

    private void createCourse() {
        System.out.println("\n--- UC-05: Create New Course (Add to Catalog) ---");
        
        System.out.print("Course Code (e.g., CS101): ");
        String code = scanner.nextLine().trim();
        
        System.out.print("Course Name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        
        System.out.print("Department: ");
        String department = scanner.nextLine().trim();
        
        System.out.print("Credits (e.g., 3): ");
        int credits = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Max Capacity: ");
        int maxCapacity = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Semester (e.g., Spring 2024): ");
        String semester = scanner.nextLine().trim();
        
        System.out.print("Academic Year (e.g., 2023-2024): ");
        String academicYear = scanner.nextLine().trim();
        
        Course course = new Course();
        course.setId(nextCourseId++);
        course.setCourseCode(code);
        course.setCourseName(name);
        course.setDescription(description);
        course.setDepartment(department);
        course.setCredits(credits);
        course.setMaxCapacity(maxCapacity);
        course.setSemester(semester);
        course.setAcademicYear(academicYear);
        
        courses.add(course);
        
        System.out.println("\n[SUCCESS] Course added to catalog!");
        System.out.println("  ID: " + course.getId());
        System.out.println("  Code: " + course.getCourseCode());
        System.out.println("  Name: " + course.getCourseName());
        System.out.println("  Department: " + course.getDepartment());
        System.out.println("  Credits: " + course.getCredits());
    }

    private void retrieveCourseByCode() {
        System.out.println("\n--- UC-05: Retrieve Course by Code ---");
        System.out.print("Enter course code: ");
        String code = scanner.nextLine().trim();
        
        Course found = courses.stream()
            .filter(c -> c.getCourseCode().equalsIgnoreCase(code))
            .findFirst()
            .orElse(null);
        
        if (found != null) {
            System.out.println("\n[SUCCESS] Course found:");
            displayCourseDetails(found);
        } else {
            System.out.println("\n[NOT FOUND] No course found with code: " + code);
        }
    }

    private void listAllCourses() {
        System.out.println("\n--- UC-05: List All Courses in Catalog ---");
        
        if (!courses.isEmpty()) {
            System.out.println("\n[SUCCESS] Found " + courses.size() + " course(s) in catalog:");
            System.out.println("\n" + "-".repeat(80));
            System.out.printf("%-10s %-30s %-15s %-10s\n", "Code", "Course Name", "Department", "Credits");
            System.out.println("-".repeat(80));
            for (Course c : courses) {
                System.out.printf("%-10s %-30s %-15s %-10d\n", 
                    c.getCourseCode(), 
                    c.getCourseName().length() > 30 ? c.getCourseName().substring(0, 27) + "..." : c.getCourseName(),
                    c.getDepartment(),
                    c.getCredits());
            }
            System.out.println("-".repeat(80));
        } else {
            System.out.println("\n[INFO] No courses in catalog yet.");
        }
    }

    private void updateCourse() {
        System.out.println("\n--- UC-05: Update Course Information ---");
        System.out.print("Enter course code to update: ");
        String code = scanner.nextLine().trim();
        
        Course course = courses.stream()
            .filter(c -> c.getCourseCode().equalsIgnoreCase(code))
            .findFirst()
            .orElse(null);
        
        if (course == null) {
            System.out.println("\n[NOT FOUND] Course not found: " + code);
            return;
        }
        
        System.out.println("\nCurrent course details:");
        displayCourseDetails(course);
        
        System.out.println("\nWhat would you like to update?");
        System.out.println("  [1] Max Capacity");
        System.out.println("  [2] Course Name");
        System.out.println("  [3] Description");
        System.out.print("Enter choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                System.out.print("New max capacity: ");
                int newCapacity = Integer.parseInt(scanner.nextLine().trim());
                course.setMaxCapacity(newCapacity);
                System.out.println("\n[SUCCESS] Max capacity updated to " + newCapacity);
                break;
            case "2":
                System.out.print("New course name: ");
                String newName = scanner.nextLine().trim();
                course.setCourseName(newName);
                System.out.println("\n[SUCCESS] Course name updated to " + newName);
                break;
            case "3":
                System.out.print("New description: ");
                String newDesc = scanner.nextLine().trim();
                course.setDescription(newDesc);
                System.out.println("\n[SUCCESS] Description updated");
                break;
            default:
                System.out.println("\n[ERROR] Invalid choice");
        }
    }

    private void deleteCourse() {
        System.out.println("\n--- UC-05: Delete Course from Catalog ---");
        System.out.print("Enter course code to delete: ");
        String code = scanner.nextLine().trim();
        
        Course course = courses.stream()
            .filter(c -> c.getCourseCode().equalsIgnoreCase(code))
            .findFirst()
            .orElse(null);
        
        if (course == null) {
            System.out.println("\n[NOT FOUND] Course not found: " + code);
            return;
        }
        
        System.out.print("Are you sure you want to delete " + code + "? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        
        if (confirm.equalsIgnoreCase("yes")) {
            courses.remove(course);
            System.out.println("\n[SUCCESS] Course deleted from catalog: " + code);
        } else {
            System.out.println("\n[INFO] Deletion cancelled");
        }
    }

    // ===== UC-06: Validate Prerequisite Course =====
    private void uc6Menu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  UC-06: VALIDATE PREREQUISITE COURSE");
        System.out.println("=".repeat(60));
        System.out.println("  [1] Add prerequisite to course");
        System.out.println("  [2] View prerequisites for a course");
        System.out.println("  [3] Validate student meets prerequisites");
        System.out.println("  [4] Mark student as completed course");
        System.out.println("  [0] Back to main menu");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                addPrerequisite();
                break;
            case "2":
                viewPrerequisites();
                break;
            case "3":
                validatePrerequisites();
                break;
            case "4":
                markCourseCompleted();
                break;
            case "0":
                return;
            default:
                System.out.println("\n[ERROR] Invalid choice.");
        }
    }

    private void addPrerequisite() {
        System.out.println("\n--- UC-06: Add Prerequisite ---");
        
        System.out.print("Course Code (to add prerequisite to): ");
        String courseCode = scanner.nextLine().trim();
        
        Course course = courses.stream()
            .filter(c -> c.getCourseCode().equalsIgnoreCase(courseCode))
            .findFirst()
            .orElse(null);
        
        if (course == null) {
            System.out.println("\n[ERROR] Course not found: " + courseCode);
            return;
        }
        
        System.out.print("Prerequisite Course Code: ");
        String prereqCode = scanner.nextLine().trim();
        
        System.out.print("Prerequisite Course Name: ");
        String prereqName = scanner.nextLine().trim();
        
        System.out.print("Minimum Grade Required (e.g., C): ");
        String minGrade = scanner.nextLine().trim();
        
        System.out.print("Is Mandatory? (true/false): ");
        boolean isMandatory = Boolean.parseBoolean(scanner.nextLine().trim());
        
        CoursePrerequisite prereq = new CoursePrerequisite();
        prereq.setId(nextPrereqId++);
        prereq.setCourseId(course.getId());
        prereq.setCourseCode(courseCode);
        prereq.setPrerequisiteCourseCode(prereqCode);
        prereq.setPrerequisiteCourseName(prereqName);
        prereq.setMinimumGradeRequired(minGrade);
        prereq.setIsMandatory(isMandatory);
        
        prerequisites.add(prereq);
        
        System.out.println("\n[SUCCESS] Prerequisite added!");
        System.out.println("  For Course: " + courseCode);
        System.out.println("  Prerequisite: " + prereqCode + " (" + prereqName + ")");
        System.out.println("  Min Grade: " + minGrade);
        System.out.println("  Mandatory: " + isMandatory);
    }

    private void viewPrerequisites() {
        System.out.println("\n--- UC-06: View Prerequisites ---");
        
        System.out.print("Course Code: ");
        String courseCode = scanner.nextLine().trim();
        
        Course course = courses.stream()
            .filter(c -> c.getCourseCode().equalsIgnoreCase(courseCode))
            .findFirst()
            .orElse(null);
        
        if (course == null) {
            System.out.println("\n[ERROR] Course not found: " + courseCode);
            return;
        }
        
        List<CoursePrerequisite> coursePrereqs = prerequisites.stream()
            .filter(p -> p.getCourseId().equals(course.getId()))
            .toList();
        
        if (!coursePrereqs.isEmpty()) {
            System.out.println("\n[SUCCESS] Found " + coursePrereqs.size() + " prerequisite(s) for " + courseCode + ":");
            for (CoursePrerequisite p : coursePrereqs) {
                System.out.println("  - " + p.getPrerequisiteCourseCode() + ": " + p.getPrerequisiteCourseName() + 
                                   " (Min Grade: " + p.getMinimumGradeRequired() + ", Mandatory: " + p.getIsMandatory() + ")");
            }
        } else {
            System.out.println("\n[INFO] No prerequisites for course: " + courseCode);
        }
    }

    private void validatePrerequisites() {
        System.out.println("\n--- UC-06: Validate Student Prerequisites ---");
        
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        
        System.out.print("Course Code: ");
        String courseCode = scanner.nextLine().trim();
        
        Course course = courses.stream()
            .filter(c -> c.getCourseCode().equalsIgnoreCase(courseCode))
            .findFirst()
            .orElse(null);
        
        if (course == null) {
            System.out.println("\n[ERROR] Course not found: " + courseCode);
            return;
        }
        
        List<CoursePrerequisite> coursePrereqs = prerequisites.stream()
            .filter(p -> p.getCourseId().equals(course.getId()) && p.getIsMandatory())
            .toList();
        
        if (coursePrereqs.isEmpty()) {
            System.out.println("\n[INFO] Course " + courseCode + " has no mandatory prerequisites");
            System.out.println("[SUCCESS] Student can enroll");
            return;
        }
        
        List<String> failedPrereqs = new ArrayList<>();
        
        for (CoursePrerequisite prereq : coursePrereqs) {
            boolean completed = enrollments.stream()
                .anyMatch(e -> e.getStudentId().equalsIgnoreCase(studentId) && 
                           e.getEnrollmentStatus() == CourseEnrollment.EnrollmentStatus.COMPLETED &&
                           e.getGradePoint() != null && e.getGradePoint() >= 1.0);
            
            if (!completed) {
                failedPrereqs.add(prereq.getPrerequisiteCourseCode());
            }
        }
        
        if (failedPrereqs.isEmpty()) {
            System.out.println("\n[SUCCESS] Student " + studentId + " meets all prerequisites for " + courseCode);
            System.out.println("[VALIDATION PASSED] Student can enroll");
        } else {
            System.out.println("\n[VALIDATION FAILED] Student " + studentId + " has NOT met " + failedPrereqs.size() + " prerequisite(s):");
            for (String failed : failedPrereqs) {
                System.out.println("  - " + failed);
            }
        }
    }

    private void markCourseCompleted() {
        System.out.println("\n--- UC-06: Mark Student as Completed Course ---");
        
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        
        System.out.print("Course Code (completed): ");
        String courseCode = scanner.nextLine().trim();
        
        Course course = courses.stream()
            .filter(c -> c.getCourseCode().equalsIgnoreCase(courseCode))
            .findFirst()
            .orElse(null);
        
        if (course == null) {
            System.out.println("\n[ERROR] Course not found: " + courseCode);
            return;
        }
        
        System.out.print("Grade Point (e.g., 3.5 for B+): ");
        double gradePoint = Double.parseDouble(scanner.nextLine().trim());
        
        System.out.print("Final Grade (e.g., B+): ");
        String finalGrade = scanner.nextLine().trim();
        
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setId(nextEnrollmentId++);
        enrollment.setCourseId(course.getId());
        enrollment.setStudentId(studentId);
        enrollment.setEnrollmentStatus(CourseEnrollment.EnrollmentStatus.COMPLETED);
        enrollment.setGradePoint(gradePoint);
        enrollment.setGrade(finalGrade);
        enrollment.setSemester(course.getSemester());
        enrollment.setAcademicYear(course.getAcademicYear());
        
        enrollments.add(enrollment);
        
        System.out.println("\n[SUCCESS] Course marked as completed!");
        System.out.println("  Student ID: " + studentId);
        System.out.println("  Course: " + courseCode);
        System.out.println("  Grade: " + finalGrade + " (" + gradePoint + ")");
        System.out.println("  Status: COMPLETED");
    }

    // ===== UC-07: Assign Course to Lecturer =====
    private void uc7Menu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  UC-07: ASSIGN COURSE TO LECTURER");
        System.out.println("=".repeat(60));
        System.out.println("  [1] Assign lecturer to course");
        System.out.println("  [2] View courses assigned to lecturer");
        System.out.println("  [3] Update lecturer assignment");
        System.out.println("  [4] Remove lecturer from course");
        System.out.println("  [0] Back to main menu");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                assignLecturerToCourse();
                break;
            case "2":
                viewCoursesForLecturer();
                break;
            case "3":
                updateLecturerAssignment();
                break;
            case "4":
                removeLecturerFromCourse();
                break;
            case "0":
                return;
            default:
                System.out.println("\n[ERROR] Invalid choice.");
        }
    }

    private void assignLecturerToCourse() {
        System.out.println("\n--- UC-07: Assign Lecturer to Course ---");
        
        System.out.print("Course Code: ");
        String courseCode = scanner.nextLine().trim();
        
        Course course = courses.stream()
            .filter(c -> c.getCourseCode().equalsIgnoreCase(courseCode))
            .findFirst()
            .orElse(null);
        
        if (course == null) {
            System.out.println("\n[ERROR] Course not found: " + courseCode);
            return;
        }
        
        System.out.print("Lecturer Name: ");
        String lecturerName = scanner.nextLine().trim();
        
        System.out.print("Lecturer ID: ");
        String lecturerId = scanner.nextLine().trim();
        
        course.setInstructorName(lecturerName);
        course.setInstructorId(lecturerId);
        
        System.out.println("\n[SUCCESS] Lecturer assigned to course!");
        System.out.println("  Course: " + courseCode + " - " + course.getCourseName());
        System.out.println("  Lecturer: " + lecturerName + " (" + lecturerId + ")");
    }

    private void viewCoursesForLecturer() {
        System.out.println("\n--- UC-07: View Courses for Lecturer ---");
        
        System.out.print("Lecturer ID: ");
        String lecturerId = scanner.nextLine().trim();
        
        List<Course> lecturerCourses = courses.stream()
            .filter(c -> c.getInstructorId() != null && c.getInstructorId().equalsIgnoreCase(lecturerId))
            .toList();
        
        if (!lecturerCourses.isEmpty()) {
            System.out.println("\n[SUCCESS] Found " + lecturerCourses.size() + " course(s) for lecturer " + lecturerId + ":");
            for (Course c : lecturerCourses) {
                System.out.println("  - " + c.getCourseCode() + ": " + c.getCourseName() + 
                                   " (" + c.getDepartment() + ", " + c.getCredits() + " credits)");
            }
        } else {
            System.out.println("\n[NOT FOUND] No courses assigned to lecturer: " + lecturerId);
        }
    }

    private void updateLecturerAssignment() {
        System.out.println("\n--- UC-07: Update Lecturer Assignment ---");
        
        System.out.print("Course Code: ");
        String courseCode = scanner.nextLine().trim();
        
        Course course = courses.stream()
            .filter(c -> c.getCourseCode().equalsIgnoreCase(courseCode))
            .findFirst()
            .orElse(null);
        
        if (course == null) {
            System.out.println("\n[ERROR] Course not found: " + courseCode);
            return;
        }
        
        System.out.println("\nCurrent Lecturer: " + course.getInstructorName() + " (" + course.getInstructorId() + ")");
        
        System.out.print("New Lecturer Name: ");
        String newLecturer = scanner.nextLine().trim();
        
        System.out.print("New Lecturer ID: ");
        String newLecturerId = scanner.nextLine().trim();
        
        course.setInstructorName(newLecturer);
        course.setInstructorId(newLecturerId);
        
        System.out.println("\n[SUCCESS] Lecturer assignment updated!");
        System.out.println("  Course: " + courseCode);
        System.out.println("  New Lecturer: " + newLecturer + " (" + newLecturerId + ")");
    }

    private void removeLecturerFromCourse() {
        System.out.println("\n--- UC-07: Remove Lecturer from Course ---");
        
        System.out.print("Course Code: ");
        String courseCode = scanner.nextLine().trim();
        
        Course course = courses.stream()
            .filter(c -> c.getCourseCode().equalsIgnoreCase(courseCode))
            .findFirst()
            .orElse(null);
        
        if (course == null) {
            System.out.println("\n[ERROR] Course not found: " + courseCode);
            return;
        }
        
        String removedLecturer = course.getInstructorName();
        course.setInstructorName(null);
        course.setInstructorId(null);
        
        System.out.println("\n[SUCCESS] Lecturer removed from course!");
        System.out.println("  Course: " + courseCode);
        System.out.println("  Removed: " + removedLecturer);
    }

    // ===== UC-08: Retrieve Course Schedule/Rooms =====
    private void uc8Menu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  UC-08: RETRIEVE COURSE SCHEDULE/ROOMS");
        System.out.println("=".repeat(60));
        System.out.println("  [1] Add course schedule/room");
        System.out.println("  [2] View schedules for specific course");
        System.out.println("  [3] View all schedules");
        System.out.println("  [4] Search schedules by room/venue");
        System.out.println("  [0] Back to main menu");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                addSchedule();
                break;
            case "2":
                viewSchedulesByCourse();
                break;
            case "3":
                viewAllSchedules();
                break;
            case "4":
                searchSchedulesByRoom();
                break;
            case "0":
                return;
            default:
                System.out.println("\n[ERROR] Invalid choice.");
        }
    }

    private void addSchedule() {
        System.out.println("\n--- UC-08: Add Course Schedule/Room ---");
        
        System.out.print("Course Code: ");
        String courseCode = scanner.nextLine().trim();
        
        Course course = courses.stream()
            .filter(c -> c.getCourseCode().equalsIgnoreCase(courseCode))
            .findFirst()
            .orElse(null);
        
        if (course == null) {
            System.out.println("\n[ERROR] Course not found: " + courseCode);
            return;
        }
        
        System.out.print("Day of Week (e.g., MONDAY): ");
        String dayOfWeek = scanner.nextLine().trim().toUpperCase();
        
        System.out.print("Start Time (HH:MM, e.g., 09:00): ");
        String startTimeStr = scanner.nextLine().trim();
        LocalTime startTime = LocalTime.parse(startTimeStr);
        
        System.out.print("End Time (HH:MM, e.g., 10:30): ");
        String endTimeStr = scanner.nextLine().trim();
        LocalTime endTime = LocalTime.parse(endTimeStr);
        
        System.out.print("Venue/Building: ");
        String venue = scanner.nextLine().trim();
        
        System.out.print("Room Number: ");
        String roomNumber = scanner.nextLine().trim();
        
        System.out.print("Schedule Type (LECTURE/LAB/TUTORIAL): ");
        String scheduleType = scanner.nextLine().trim().toUpperCase();
        
        CourseSchedule schedule = new CourseSchedule();
        schedule.setId(nextScheduleId++);
        schedule.setCourseId(course.getId());
        schedule.setCourseCode(courseCode);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setVenue(venue);
        schedule.setRoomNumber(roomNumber);
        schedule.setCapacity(course.getMaxCapacity());
        schedule.setScheduleType(scheduleType);
        schedule.setInstructorId(course.getInstructorId());
        
        schedules.add(schedule);
        
        System.out.println("\n[SUCCESS] Schedule added!");
        System.out.println("  Schedule ID: " + schedule.getId());
        System.out.println("  Course: " + courseCode);
        System.out.println("  Time: " + dayOfWeek + " " + startTime + "-" + endTime);
        System.out.println("  Location: " + venue + " Room " + roomNumber);
        System.out.println("  Type: " + scheduleType);
    }

    private void viewSchedulesByCourse() {
        System.out.println("\n--- UC-08: View Schedules for Course ---");
        
        System.out.print("Course Code: ");
        String courseCode = scanner.nextLine().trim();
        
        Course course = courses.stream()
            .filter(c -> c.getCourseCode().equalsIgnoreCase(courseCode))
            .findFirst()
            .orElse(null);
        
        if (course == null) {
            System.out.println("\n[ERROR] Course not found: " + courseCode);
            return;
        }
        
        List<CourseSchedule> courseSchedules = schedules.stream()
            .filter(s -> s.getCourseId().equals(course.getId()))
            .toList();
        
        if (!courseSchedules.isEmpty()) {
            System.out.println("\n[SUCCESS] Found " + courseSchedules.size() + " schedule(s) for " + courseCode + ":");
            System.out.println("\n" + "-".repeat(80));
            System.out.printf("%-10s %-15s %-15s %-20s %-10s\n", "Day", "Time", "Type", "Venue", "Room");
            System.out.println("-".repeat(80));
            for (CourseSchedule s : courseSchedules) {
                System.out.printf("%-10s %-15s %-15s %-20s %-10s\n",
                    s.getDayOfWeek(),
                    s.getStartTime() + "-" + s.getEndTime(),
                    s.getScheduleType(),
                    s.getVenue(),
                    s.getRoomNumber());
            }
            System.out.println("-".repeat(80));
        } else {
            System.out.println("\n[NOT FOUND] No schedules found for course: " + courseCode);
        }
    }

    private void viewAllSchedules() {
        System.out.println("\n--- UC-08: View All Schedules ---");
        
        if (!schedules.isEmpty()) {
            System.out.println("\n[SUCCESS] Found " + schedules.size() + " schedule(s):");
            System.out.println("\n" + "-".repeat(90));
            System.out.printf("%-10s %-10s %-15s %-15s %-20s %-10s\n", "ID", "Course", "Day", "Time", "Venue", "Room");
            System.out.println("-".repeat(90));
            for (CourseSchedule s : schedules) {
                System.out.printf("%-10d %-10s %-15s %-15s %-20s %-10s\n",
                    s.getId(),
                    s.getCourseCode(),
                    s.getDayOfWeek(),
                    s.getStartTime() + "-" + s.getEndTime(),
                    s.getVenue(),
                    s.getRoomNumber());
            }
            System.out.println("-".repeat(90));
        } else {
            System.out.println("\n[INFO] No schedules available.");
        }
    }

    private void searchSchedulesByRoom() {
        System.out.println("\n--- UC-08: Search Schedules by Room/Venue ---");
        
        System.out.print("Enter venue or room number to search: ");
        String searchTerm = scanner.nextLine().trim();
        
        List<CourseSchedule> matchingSchedules = schedules.stream()
            .filter(s -> (s.getVenue() != null && s.getVenue().toLowerCase().contains(searchTerm.toLowerCase())) ||
                        (s.getRoomNumber() != null && s.getRoomNumber().equalsIgnoreCase(searchTerm)))
            .toList();
        
        if (!matchingSchedules.isEmpty()) {
            System.out.println("\n[SUCCESS] Found " + matchingSchedules.size() + " schedule(s) matching '" + searchTerm + "':");
            System.out.println("\n" + "-".repeat(90));
            System.out.printf("%-10s %-10s %-15s %-15s %-20s %-10s\n", "ID", "Course", "Day", "Time", "Venue", "Room");
            System.out.println("-".repeat(90));
            for (CourseSchedule s : matchingSchedules) {
                System.out.printf("%-10d %-10s %-15s %-15s %-20s %-10s\n",
                    s.getId(),
                    s.getCourseCode(),
                    s.getDayOfWeek(),
                    s.getStartTime() + "-" + s.getEndTime(),
                    s.getVenue(),
                    s.getRoomNumber());
            }
            System.out.println("-".repeat(90));
        } else {
            System.out.println("\n[NOT FOUND] No schedules found matching: " + searchTerm);
        }
    }

    // ===== Helper Methods =====
    private void viewAllData() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ALL CURRENT DATA");
        System.out.println("=".repeat(60));
        
        System.out.println("\n--- COURSES (" + courses.size() + ") ---");
        if (courses.isEmpty()) {
            System.out.println("  (No courses yet)");
        } else {
            for (Course c : courses) {
                System.out.println("  ID: " + c.getId() + " | " + c.getCourseCode() + ": " + 
                                   c.getCourseName() + " | Dept: " + c.getDepartment() +
                                   " | Lecturer: " + (c.getInstructorName() != null ? c.getInstructorName() : "Not assigned"));
            }
        }
        
        System.out.println("\n--- COMPLETED COURSES (" + enrollments.size() + ") ---");
        if (enrollments.isEmpty()) {
            System.out.println("  (No completed courses yet)");
        } else {
            for (CourseEnrollment e : enrollments) {
                System.out.println("  Student: " + e.getStudentId() + " | Grade: " + e.getGrade() + 
                                   " | Status: " + e.getEnrollmentStatus());
            }
        }
        
        System.out.println("\n--- SCHEDULES (" + schedules.size() + ") ---");
        if (schedules.isEmpty()) {
            System.out.println("  (No schedules yet)");
        } else {
            for (CourseSchedule s : schedules) {
                System.out.println("  ID: " + s.getId() + " | " + s.getCourseCode() + " | " + 
                                   s.getDayOfWeek() + " " + s.getStartTime() + "-" + s.getEndTime() +
                                   " | " + s.getVenue() + " Room " + s.getRoomNumber());
            }
        }
        
        System.out.println("\n--- PREREQUISITES (" + prerequisites.size() + ") ---");
        if (prerequisites.isEmpty()) {
            System.out.println("  (No prerequisites yet)");
        } else {
            for (CoursePrerequisite p : prerequisites) {
                System.out.println("  " + p.getCourseCode() + " requires " + p.getPrerequisiteCourseCode() +
                                   " (Min Grade: " + p.getMinimumGradeRequired() + ", Mandatory: " + p.getIsMandatory() + ")");
            }
        }
    }

    private void displayCourseDetails(Course course) {
        System.out.println("  ID: " + course.getId());
        System.out.println("  Code: " + course.getCourseCode());
        System.out.println("  Name: " + course.getCourseName());
        System.out.println("  Description: " + course.getDescription());
        System.out.println("  Department: " + course.getDepartment());
        System.out.println("  Credits: " + course.getCredits());
        System.out.println("  Lecturer: " + (course.getInstructorName() != null ? course.getInstructorName() + " (" + course.getInstructorId() + ")" : "Not assigned"));
        System.out.println("  Max Capacity: " + course.getMaxCapacity());
        System.out.println("  Semester: " + course.getSemester());
        System.out.println("  Academic Year: " + course.getAcademicYear());
    }

    public static void main(String[] args) {
        InteractiveCourseDemo demo = new InteractiveCourseDemo();
        demo.start();
    }
}
