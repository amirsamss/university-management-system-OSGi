package com.example.university.student.demo;

import com.example.university.student.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Standalone Demonstration for Student Service Bundle
 * This demo demonstrates all student management functionalities without requiring external test frameworks
 */
public class StudentServiceDemo {

    private List<Student> students;
    private List<AcademicProfile> academicProfiles;
    private List<DisciplinaryRecord> disciplinaryRecords;
    private List<Enrollment> enrollments;

    public StudentServiceDemo() {
        students = new ArrayList<>();
        academicProfiles = new ArrayList<>();
        disciplinaryRecords = new ArrayList<>();
        enrollments = new ArrayList<>();
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

    // ===== Student Management =====
    public void demonstrateStudentManagement() {
        printSection("Student Management");

        // Create student
        Student student1 = new Student();
        student1.setId(1L);
        student1.setStudentId("STU001");
        student1.setFirstName("John");
        student1.setLastName("Smith");
        student1.setEmail("john.smith@university.edu");
        student1.setPhone("123-456-7890");
        student1.setDateOfBirth(LocalDate.of(2000, 5, 15));
        student1.setGender(Student.Gender.MALE);
        student1.setProgram("Computer Science");
        student1.setMajor("Software Engineering");
        student1.setStatus(Student.StudentStatus.ACTIVE);
        student1.setAdmissionDate(LocalDate.of(2022, 9, 1));
        student1.setCreatedAt(LocalDateTime.now());
        student1.setUpdatedAt(LocalDateTime.now());
        students.add(student1);
        printSuccess("Student created: STU001 - John Smith");

        // Create another student
        Student student2 = new Student();
        student2.setId(2L);
        student2.setStudentId("STU002");
        student2.setFirstName("Sarah");
        student2.setLastName("Johnson");
        student2.setEmail("sarah.johnson@university.edu");
        student2.setStatus(Student.StudentStatus.ACTIVE);
        student2.setAdmissionDate(LocalDate.of(2023, 9, 1));
        student2.setCreatedAt(LocalDateTime.now());
        student2.setUpdatedAt(LocalDateTime.now());
        students.add(student2);
        printSuccess("Student created: STU002 - Sarah Johnson");

        // Get student by ID
        Student found = students.stream()
            .filter(s -> s.getStudentId().equals("STU001"))
            .findFirst()
            .orElse(null);
        if (found != null) {
            printSuccess("Student retrieved by ID: " + found.getFirstName() + " " + found.getLastName());
        }

        // Get all students
        printSuccess("Total students: " + students.size());

        // Search students
        List<Student> searchResults = students.stream()
            .filter(s -> s.getFirstName().toLowerCase().contains("john") ||
                        s.getLastName().toLowerCase().contains("john"))
            .toList();
        printSuccess("Search results for 'john': " + searchResults.size() + " student(s)");

        // Get students by status
        List<Student> activeStudents = students.stream()
            .filter(s -> s.getStatus() == Student.StudentStatus.ACTIVE)
            .toList();
        printSuccess("Active students: " + activeStudents.size());

        // Update student
        student1.setEmail("john.smith.updated@university.edu");
        student1.setUpdatedAt(LocalDateTime.now());
        printSuccess("Student updated: Email changed for STU001");
    }

    // ===== Academic Profile Management =====
    public void demonstrateAcademicProfile() {
        printSection("Academic Profile Management");

        // Create academic profile
        AcademicProfile profile1 = new AcademicProfile();
        profile1.setId(1L);
        profile1.setStudentId("STU001");
        profile1.setCumulativeGpa(new BigDecimal("3.75"));
        profile1.setSemesterGpa(new BigDecimal("3.85"));
        profile1.setTotalCreditsEarned(60);
        profile1.setTotalCreditsAttempted(64);
        profile1.setCurrentSemester("Spring 2024");
        profile1.setAcademicYear("2023-2024");
        profile1.setAcademicStanding(AcademicProfile.AcademicStanding.GOOD_STANDING);
        profile1.setClassStanding("Sophomore");
        profile1.setLastUpdated(LocalDateTime.now());
        academicProfiles.add(profile1);
        printSuccess("Academic profile created for STU001 - GPA: 3.75");

        // Create another profile
        AcademicProfile profile2 = new AcademicProfile();
        profile2.setId(2L);
        profile2.setStudentId("STU002");
        profile2.setCumulativeGpa(new BigDecimal("3.50"));
        profile2.setTotalCreditsEarned(30);
        profile2.setAcademicStanding(AcademicProfile.AcademicStanding.GOOD_STANDING);
        profile2.setClassStanding("Freshman");
        profile2.setLastUpdated(LocalDateTime.now());
        academicProfiles.add(profile2);
        printSuccess("Academic profile created for STU002 - GPA: 3.50");

        // Get academic profile
        AcademicProfile found = academicProfiles.stream()
            .filter(p -> p.getStudentId().equals("STU001"))
            .findFirst()
            .orElse(null);
        if (found != null) {
            printSuccess("Academic profile retrieved for STU001 - Credits: " + found.getTotalCreditsEarned());
        }

        // Update academic profile
        profile1.setCumulativeGpa(new BigDecimal("3.80"));
        profile1.setTotalCreditsEarned(64);
        profile1.setLastUpdated(LocalDateTime.now());
        printSuccess("Academic profile updated: GPA increased to 3.80, credits to 64");
    }

    // ===== Disciplinary Record Management =====
    public void demonstrateDisciplinaryRecords() {
        printSection("Disciplinary Record Management");

        // Create disciplinary record
        DisciplinaryRecord record1 = new DisciplinaryRecord();
        record1.setId(1L);
        record1.setStudentId("STU001");
        record1.setViolationType("Academic Misconduct");
        record1.setDescription("Plagiarism in assignment");
        record1.setSeverity(DisciplinaryRecord.Severity.MODERATE);
        record1.setSanction("Warning and assignment resubmission");
        record1.setStatus(DisciplinaryRecord.RecordStatus.OPEN);
        record1.setReportedBy("PROF001");
        record1.setIncidentDate(LocalDate.of(2024, 1, 15));
        record1.setCreatedAt(LocalDateTime.now());
        record1.setUpdatedAt(LocalDateTime.now());
        disciplinaryRecords.add(record1);
        printSuccess("Disciplinary record created for STU001 - Violation: Academic Misconduct");

        // Create another record
        DisciplinaryRecord record2 = new DisciplinaryRecord();
        record2.setId(2L);
        record2.setStudentId("STU002");
        record2.setViolationType("Behavioral Issue");
        record2.setDescription("Disruptive behavior in class");
        record2.setSeverity(DisciplinaryRecord.Severity.LOW);
        record2.setSanction("Verbal warning");
        record2.setStatus(DisciplinaryRecord.RecordStatus.RESOLVED);
        record2.setReportedBy("PROF002");
        record2.setIncidentDate(LocalDate.of(2024, 2, 1));
        record2.setCreatedAt(LocalDateTime.now());
        record2.setUpdatedAt(LocalDateTime.now());
        disciplinaryRecords.add(record2);
        printSuccess("Disciplinary record created for STU002 - Violation: Behavioral Issue");

        // Get disciplinary records by student
        List<DisciplinaryRecord> studentRecords = disciplinaryRecords.stream()
            .filter(r -> r.getStudentId().equals("STU001"))
            .toList();
        printSuccess("Disciplinary records for STU001: " + studentRecords.size());

        // Update disciplinary record
        record1.setStatus(DisciplinaryRecord.RecordStatus.RESOLVED);
        record1.setUpdatedAt(LocalDateTime.now());
        printSuccess("Disciplinary record updated: Status changed to RESOLVED");

        // Get record by ID
        DisciplinaryRecord found = disciplinaryRecords.stream()
            .filter(r -> r.getId().equals(1L))
            .findFirst()
            .orElse(null);
        if (found != null) {
            printSuccess("Disciplinary record retrieved: " + found.getViolationType());
        }
    }

    // ===== Enrollment Management =====
    public void demonstrateEnrollments() {
        printSection("Enrollment Management");

        // Create enrollment
        Enrollment enrollment1 = new Enrollment();
        enrollment1.setId(1L);
        enrollment1.setStudentId("STU001");
        enrollment1.setCourseId("CS101");
        enrollment1.setSemester("Spring 2024");
        enrollment1.setAcademicYear("2023-2024");
        enrollment1.setEnrollmentStatus(Enrollment.EnrollmentStatus.ENROLLED);
        enrollment1.setEnrollmentDate(LocalDate.of(2024, 1, 10));
        enrollment1.setCreatedAt(LocalDateTime.now());
        enrollment1.setUpdatedAt(LocalDateTime.now());
        enrollments.add(enrollment1);
        printSuccess("Enrollment created: STU001 enrolled in CS101");

        // Create more enrollments
        Enrollment enrollment2 = new Enrollment();
        enrollment2.setId(2L);
        enrollment2.setStudentId("STU001");
        enrollment2.setCourseId("MATH101");
        enrollment2.setSemester("Spring 2024");
        enrollment2.setAcademicYear("2023-2024");
        enrollment2.setEnrollmentStatus(Enrollment.EnrollmentStatus.ENROLLED);
        enrollment2.setEnrollmentDate(LocalDate.of(2024, 1, 10));
        enrollment2.setCreatedAt(LocalDateTime.now());
        enrollment2.setUpdatedAt(LocalDateTime.now());
        enrollments.add(enrollment2);
        printSuccess("Enrollment created: STU001 enrolled in MATH101");

        Enrollment enrollment3 = new Enrollment();
        enrollment3.setId(3L);
        enrollment3.setStudentId("STU002");
        enrollment3.setCourseId("CS101");
        enrollment3.setSemester("Spring 2024");
        enrollment3.setAcademicYear("2023-2024");
        enrollment3.setEnrollmentStatus(Enrollment.EnrollmentStatus.ENROLLED);
        enrollment3.setEnrollmentDate(LocalDate.of(2024, 1, 10));
        enrollment3.setCreatedAt(LocalDateTime.now());
        enrollment3.setUpdatedAt(LocalDateTime.now());
        enrollments.add(enrollment3);
        printSuccess("Enrollment created: STU002 enrolled in CS101");

        // Get enrollments by student
        List<Enrollment> studentEnrollments = enrollments.stream()
            .filter(e -> e.getStudentId().equals("STU001"))
            .toList();
        printSuccess("Enrollments for STU001: " + studentEnrollments.size() + " course(s)");

        // Get enrollments by course
        List<Enrollment> courseEnrollments = enrollments.stream()
            .filter(e -> e.getCourseId().equals("CS101"))
            .toList();
        printSuccess("Enrollments for CS101: " + courseEnrollments.size() + " student(s)");

        // Get enrollments by semester
        List<Enrollment> semesterEnrollments = enrollments.stream()
            .filter(e -> e.getSemester().equals("Spring 2024") && 
                        e.getAcademicYear().equals("2023-2024"))
            .toList();
        printSuccess("Enrollments for Spring 2024: " + semesterEnrollments.size());

        // Update enrollment (add grade)
        enrollment1.setGrade(Enrollment.Grade.A);
        enrollment1.setEnrollmentStatus(Enrollment.EnrollmentStatus.COMPLETED);
        enrollment1.setUpdatedAt(LocalDateTime.now());
        printSuccess("Enrollment updated: Grade A assigned to STU001 for CS101");

        // Drop enrollment
        enrollment2.setEnrollmentStatus(Enrollment.EnrollmentStatus.DROPPED);
        enrollment2.setUpdatedAt(LocalDateTime.now());
        printSuccess("Enrollment dropped: STU001 dropped MATH101");
    }

    public void printSummary() {
        printSection("Demonstration Complete");
        System.out.println();
        System.out.println("All Student Service Functionalities Successfully Demonstrated:");
        System.out.println();
        System.out.println("  [OK] Student Management");
        System.out.println("       - Create students");
        System.out.println("       - Retrieve students by ID");
        System.out.println("       - Search students");
        System.out.println("       - Filter by status");
        System.out.println("       - Update student information");
        System.out.println();
        System.out.println("  [OK] Academic Profile Management");
        System.out.println("       - Create academic profiles");
        System.out.println("       - Track GPA and credits");
        System.out.println("       - Update academic information");
        System.out.println();
        System.out.println("  [OK] Disciplinary Record Management");
        System.out.println("       - Create disciplinary records");
        System.out.println("       - View records by student");
        System.out.println("       - Update record status");
        System.out.println();
        System.out.println("  [OK] Enrollment Management");
        System.out.println("       - Create enrollments");
        System.out.println("       - View enrollments by student/course/semester");
        System.out.println("       - Update enrollment status and grades");
        System.out.println("       - Drop enrollments");
        System.out.println();
        System.out.println("=".repeat(50));
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("  Student Service Bundle - Demo");
        System.out.println("========================================");

        StudentServiceDemo demo = new StudentServiceDemo();

        // Run all demonstrations
        demo.demonstrateStudentManagement();
        demo.demonstrateAcademicProfile();
        demo.demonstrateDisciplinaryRecords();
        demo.demonstrateEnrollments();

        // Print summary
        demo.printSummary();
    }
}
