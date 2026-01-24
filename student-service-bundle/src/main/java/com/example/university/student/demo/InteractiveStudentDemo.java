package com.example.university.student.demo;

import com.example.university.student.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Student Service Demo
 * Demonstrates student management, academic profiles, disciplinary records, and enrollments
 * 
 * NOTE: This is a standalone demo that uses in-memory storage (ArrayList) for demonstration purposes.
 * The actual OSGi service implementation (StudentServiceImpl) uses JPA/EntityManager to persist
 * data to the PostgreSQL database. See StudentServiceImpl.java for the database implementation.
 */
public class InteractiveStudentDemo {

    private List<Student> students;
    private List<AcademicProfile> academicProfiles;
    private List<DisciplinaryRecord> disciplinaryRecords;
    private List<Enrollment> enrollments;
    private Scanner scanner;
    private Long nextStudentId = 1L;
    private Long nextProfileId = 1L;
    private Long nextRecordId = 1L;
    private Long nextEnrollmentId = 1L;

    public InteractiveStudentDemo() {
        students = new ArrayList<>();
        academicProfiles = new ArrayList<>();
        disciplinaryRecords = new ArrayList<>();
        enrollments = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  INTERACTIVE STUDENT SERVICE DEMO");
        System.out.println("=".repeat(60));
        
        boolean running = true;
        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    studentManagementMenu();
                    break;
                case "2":
                    academicProfileMenu();
                    break;
                case "3":
                    disciplinaryRecordMenu();
                    break;
                case "4":
                    enrollmentMenu();
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
        System.out.println("MAIN MENU - Select Function:");
        System.out.println("-".repeat(60));
        System.out.println("  [1] Student Management");
        System.out.println("  [2] Academic Profile Management");
        System.out.println("  [3] Disciplinary Record Management");
        System.out.println("  [4] Enrollment Management");
        System.out.println("  [5] View All Current Data");
        System.out.println("  [0] Exit");
        System.out.println("-".repeat(60));
        System.out.print("Enter your choice: ");
    }

    // ===== Student Management =====
    private void studentManagementMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  STUDENT MANAGEMENT");
        System.out.println("=".repeat(60));
        System.out.println("  [1] Create new student");
        System.out.println("  [2] View student by ID");
        System.out.println("  [3] View student by Student ID");
        System.out.println("  [4] List all students");
        System.out.println("  [5] Search students");
        System.out.println("  [6] Update student information");
        System.out.println("  [7] Delete student");
        System.out.println("  [8] List students by status");
        System.out.println("  [0] Back to main menu");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                createStudent();
                break;
            case "2":
                viewStudentById();
                break;
            case "3":
                viewStudentByStudentId();
                break;
            case "4":
                listAllStudents();
                break;
            case "5":
                searchStudents();
                break;
            case "6":
                updateStudent();
                break;
            case "7":
                deleteStudent();
                break;
            case "8":
                listStudentsByStatus();
                break;
            case "0":
                return;
            default:
                System.out.println("\n[ERROR] Invalid choice.");
        }
    }

    private void createStudent() {
        System.out.println("\n--- Create New Student ---");
        
        // Validate Student ID (required, must be unique)
        String studentId = promptUntilValid(
            "Student ID (e.g., STU001): ",
            "Student ID is required and must be unique. Please enter a different ID.",
            this::isValidStudentId,
            true
        );
        
        // Validate First Name (required)
        String firstName = promptUntilValid(
            "First Name: ",
            "First name is required. Please enter a value.",
            name -> name != null && !name.trim().isEmpty(),
            true
        );
        
        // Validate Last Name (required)
        String lastName = promptUntilValid(
            "Last Name: ",
            "Last name is required. Please enter a value.",
            name -> name != null && !name.trim().isEmpty(),
            true
        );
        
        // Validate Email (required, must be valid format)
        String email = promptUntilValid(
            "Email: ",
            "Invalid email format. Please enter a valid email (e.g., student@university.edu).",
            this::isValidEmail,
            true
        );
        
        // Phone (optional)
        System.out.print("Phone (optional): ");
        String phone = scanner.nextLine().trim();
        
        // Validate Date of Birth (optional, must be valid format)
        String dobStr = promptUntilValid(
            "Date of Birth (YYYY-MM-DD, optional): ",
            "Invalid date format. Please use YYYY-MM-DD format (e.g., 2000-01-15).",
            this::isValidDate,
            false
        );
        LocalDate dateOfBirth = dobStr.isEmpty() ? null : LocalDate.parse(dobStr);
        
        // Program (optional)
        System.out.print("Program (optional): ");
        String program = scanner.nextLine().trim();
        
        // Validate Status (required, must be one of the 3 allowed values)
        String statusStr = promptUntilValid(
            "Status (ACTIVE/INACTIVE/GRADUATED): ",
            "Invalid status. Please enter ACTIVE, INACTIVE, or GRADUATED.",
            this::isValidStatus,
            false
        );
        Student.StudentStatus status = statusStr.isEmpty() ? Student.StudentStatus.ACTIVE : 
                                       Student.StudentStatus.valueOf(statusStr.toUpperCase());
        
        Student student = new Student();
        student.setId(nextStudentId++);
        student.setStudentId(studentId);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setPhone(phone);
        student.setDateOfBirth(dateOfBirth);
        student.setProgram(program);
        student.setStatus(status);
        student.setAdmissionDate(LocalDate.now());
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
        
        students.add(student);
        
        System.out.println("\n[SUCCESS] Student created!");
        System.out.println("  ID: " + student.getId());
        System.out.println("  Student ID: " + student.getStudentId());
        System.out.println("  Name: " + student.getFirstName() + " " + student.getLastName());
        System.out.println("  Email: " + student.getEmail());
        System.out.println("  Status: " + student.getStatus());
    }

    private void viewStudentById() {
        System.out.println("\n--- View Student by ID ---");
        System.out.print("Enter student ID: ");
        Long id = Long.parseLong(scanner.nextLine().trim());
        
        Student found = students.stream()
            .filter(s -> s.getId().equals(id))
            .findFirst()
            .orElse(null);
        
        if (found != null) {
            System.out.println("\n[SUCCESS] Student found:");
            displayStudentDetails(found);
        } else {
            System.out.println("\n[NOT FOUND] No student found with ID: " + id);
        }
    }

    private void viewStudentByStudentId() {
        System.out.println("\n--- View Student by Student ID ---");
        System.out.print("Enter student ID (e.g., STU001): ");
        String studentId = scanner.nextLine().trim();
        
        Student found = students.stream()
            .filter(s -> s.getStudentId().equalsIgnoreCase(studentId))
            .findFirst()
            .orElse(null);
        
        if (found != null) {
            System.out.println("\n[SUCCESS] Student found:");
            displayStudentDetails(found);
        } else {
            System.out.println("\n[NOT FOUND] No student found with ID: " + studentId);
        }
    }

    private void listAllStudents() {
        System.out.println("\n--- List All Students ---");
        
        if (!students.isEmpty()) {
            System.out.println("\n[SUCCESS] Found " + students.size() + " student(s):");
            System.out.println("\n" + "-".repeat(100));
            System.out.printf("%-10s %-15s %-25s %-30s %-15s\n", "ID", "Student ID", "Name", "Email", "Status");
            System.out.println("-".repeat(100));
            for (Student s : students) {
                System.out.printf("%-10d %-15s %-25s %-30s %-15s\n",
                    s.getId(),
                    s.getStudentId(),
                    s.getFirstName() + " " + s.getLastName(),
                    s.getEmail().length() > 30 ? s.getEmail().substring(0, 27) + "..." : s.getEmail(),
                    s.getStatus());
            }
            System.out.println("-".repeat(100));
        } else {
            System.out.println("\n[INFO] No students registered yet.");
        }
    }

    private void searchStudents() {
        System.out.println("\n--- Search Students ---");
        System.out.print("Enter search keyword (name, email, or student ID): ");
        String keyword = scanner.nextLine().trim().toLowerCase();
        
        List<Student> results = students.stream()
            .filter(s -> s.getFirstName().toLowerCase().contains(keyword) ||
                        s.getLastName().toLowerCase().contains(keyword) ||
                        s.getEmail().toLowerCase().contains(keyword) ||
                        s.getStudentId().toLowerCase().contains(keyword))
            .toList();
        
        if (!results.isEmpty()) {
            System.out.println("\n[SUCCESS] Found " + results.size() + " student(s) matching '" + keyword + "':");
            for (Student s : results) {
                System.out.println("  - " + s.getStudentId() + ": " + s.getFirstName() + " " + s.getLastName() + 
                                 " (" + s.getEmail() + ")");
            }
        } else {
            System.out.println("\n[NOT FOUND] No students found matching: " + keyword);
        }
    }

    private void updateStudent() {
        System.out.println("\n--- Update Student Information ---");
        System.out.print("Enter student ID (e.g., STU001): ");
        String studentId = scanner.nextLine().trim();
        
        Student student = students.stream()
            .filter(s -> s.getStudentId().equalsIgnoreCase(studentId))
            .findFirst()
            .orElse(null);
        
        if (student == null) {
            System.out.println("\n[NOT FOUND] Student not found: " + studentId);
            return;
        }
        
        System.out.println("\nCurrent student details:");
        displayStudentDetails(student);
        
        System.out.println("\nWhat would you like to update?");
        System.out.println("  [1] Email");
        System.out.println("  [2] Phone");
        System.out.println("  [3] Status");
        System.out.println("  [4] Major");
        System.out.print("Enter choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                String newEmail = promptUntilValid(
                    "New email: ",
                    "Invalid email format. Please enter a valid email (e.g., student@university.edu).",
                    this::isValidEmail,
                    true
                );
                student.setEmail(newEmail);
                student.setUpdatedAt(LocalDateTime.now());
                System.out.println("\n[SUCCESS] Email updated to " + newEmail);
                break;
            case "2":
                System.out.print("New phone: ");
                String newPhone = scanner.nextLine().trim();
                student.setPhone(newPhone);
                student.setUpdatedAt(LocalDateTime.now());
                System.out.println("\n[SUCCESS] Phone updated to " + newPhone);
                break;
            case "3":
                String newStatus = promptUntilValid(
                    "New status (ACTIVE/INACTIVE/GRADUATED): ",
                    "Invalid status. Please enter ACTIVE, INACTIVE, or GRADUATED.",
                    this::isValidStatus,
                    true
                );
                student.setStatus(Student.StudentStatus.valueOf(newStatus.toUpperCase()));
                student.setUpdatedAt(LocalDateTime.now());
                System.out.println("\n[SUCCESS] Status updated to " + newStatus);
                break;
            case "4":
                System.out.print("New major: ");
                String newMajor = scanner.nextLine().trim();
                student.setMajor(newMajor);
                student.setUpdatedAt(LocalDateTime.now());
                System.out.println("\n[SUCCESS] Major updated to " + newMajor);
                break;
            default:
                System.out.println("\n[ERROR] Invalid choice");
        }
    }

    private void deleteStudent() {
        System.out.println("\n--- Delete Student ---");
        System.out.print("Enter student ID (e.g., STU001): ");
        String studentId = scanner.nextLine().trim();
        
        Student student = students.stream()
            .filter(s -> s.getStudentId().equalsIgnoreCase(studentId))
            .findFirst()
            .orElse(null);
        
        if (student == null) {
            System.out.println("\n[NOT FOUND] Student not found: " + studentId);
            return;
        }
        
        System.out.print("Are you sure you want to delete " + studentId + "? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        
        if (confirm.equalsIgnoreCase("yes")) {
            students.remove(student);
            System.out.println("\n[SUCCESS] Student deleted: " + studentId);
        } else {
            System.out.println("\n[INFO] Deletion cancelled");
        }
    }

    private void listStudentsByStatus() {
        System.out.println("\n--- List Students by Status ---");
        System.out.print("Enter status (ACTIVE/INACTIVE/GRADUATED): ");
        String statusStr = scanner.nextLine().trim();
        Student.StudentStatus status = Student.StudentStatus.valueOf(statusStr.toUpperCase());
        
        List<Student> results = students.stream()
            .filter(s -> s.getStatus() == status)
            .toList();
        
        if (!results.isEmpty()) {
            System.out.println("\n[SUCCESS] Found " + results.size() + " student(s) with status " + status + ":");
            for (Student s : results) {
                System.out.println("  - " + s.getStudentId() + ": " + s.getFirstName() + " " + s.getLastName());
            }
        } else {
            System.out.println("\n[INFO] No students found with status: " + status);
        }
    }

    // ===== Academic Profile Management =====
    private void academicProfileMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ACADEMIC PROFILE MANAGEMENT");
        System.out.println("=".repeat(60));
        System.out.println("  [1] Create academic profile");
        System.out.println("  [2] View academic profile");
        System.out.println("  [3] Update academic profile");
        System.out.println("  [4] List all academic profiles");
        System.out.println("  [0] Back to main menu");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                createAcademicProfile();
                break;
            case "2":
                viewAcademicProfile();
                break;
            case "3":
                updateAcademicProfile();
                break;
            case "4":
                listAllAcademicProfiles();
                break;
            case "0":
                return;
            default:
                System.out.println("\n[ERROR] Invalid choice.");
        }
    }

    private void createAcademicProfile() {
        System.out.println("\n--- Create Academic Profile ---");
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        
        Student student = students.stream()
            .filter(s -> s.getStudentId().equalsIgnoreCase(studentId))
            .findFirst()
            .orElse(null);
        
        if (student == null) {
            System.out.println("\n[ERROR] Student not found: " + studentId);
            return;
        }
        
        // CGPA (required, must be valid number between 0.0 and 4.0)
        String cgpaStr = promptUntilValid(
            "CGPA: ",
            "CGPA must be a valid number between 0.0 and 4.0.",
            this::isValidCGPA,
            true
        );
        BigDecimal cumulativeGpa = new BigDecimal(cgpaStr);
        
        // Credit Hours (required, must be a positive number)
        String creditsStr = promptUntilValid(
            "Credit Hours: ",
            "Credit hours must be a valid number. Please enter a positive integer.",
            this::isValidCreditHours,
            true
        );
        Integer creditHours = Integer.parseInt(creditsStr);
        
        AcademicProfile profile = new AcademicProfile();
        profile.setId(nextProfileId++);
        profile.setStudentId(studentId);
        profile.setCumulativeGpa(cumulativeGpa);
        profile.setTotalCreditsEarned(creditHours);
        profile.setLastUpdated(LocalDateTime.now());
        
        academicProfiles.add(profile);
        
        System.out.println("\n[SUCCESS] Academic profile created!");
        System.out.println("  Student ID: " + studentId);
        System.out.println("  CGPA: " + cumulativeGpa);
        System.out.println("  Credit Hours: " + creditHours);
    }

    private void viewAcademicProfile() {
        System.out.println("\n--- View Academic Profile ---");
        System.out.print("Enter student ID: ");
        String studentId = scanner.nextLine().trim();
        
        AcademicProfile profile = academicProfiles.stream()
            .filter(p -> p.getStudentId().equalsIgnoreCase(studentId))
            .findFirst()
            .orElse(null);
        
        if (profile != null) {
            System.out.println("\n[SUCCESS] Academic profile found:");
            displayAcademicProfileDetails(profile);
        } else {
            System.out.println("\n[NOT FOUND] No academic profile found for student: " + studentId);
        }
    }

    private void updateAcademicProfile() {
        System.out.println("\n--- Update Academic Profile ---");
        System.out.print("Enter student ID: ");
        String studentId = scanner.nextLine().trim();
        
        AcademicProfile profile = academicProfiles.stream()
            .filter(p -> p.getStudentId().equalsIgnoreCase(studentId))
            .findFirst()
            .orElse(null);
        
        if (profile == null) {
            System.out.println("\n[NOT FOUND] Academic profile not found for student: " + studentId);
            return;
        }
        
        System.out.println("\nCurrent academic profile:");
        displayAcademicProfileDetails(profile);
        
        System.out.println("\nWhat would you like to update?");
        System.out.println("  [1] CGPA");
        System.out.println("  [2] Credit Hours");
        System.out.print("Enter choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                String newCgpaStr = promptUntilValid(
                    "New CGPA: ",
                    "CGPA must be a valid number between 0.0 and 4.0.",
                    this::isValidCGPA,
                    true
                );
                BigDecimal newCgpa = new BigDecimal(newCgpaStr);
                profile.setCumulativeGpa(newCgpa);
                profile.setLastUpdated(LocalDateTime.now());
                System.out.println("\n[SUCCESS] CGPA updated to " + newCgpa);
                break;
            case "2":
                String newCreditsStr = promptUntilValid(
                    "New Credit Hours: ",
                    "Credit hours must be a valid number. Please enter a positive integer.",
                    this::isValidCreditHours,
                    true
                );
                Integer newCredits = Integer.parseInt(newCreditsStr);
                profile.setTotalCreditsEarned(newCredits);
                profile.setLastUpdated(LocalDateTime.now());
                System.out.println("\n[SUCCESS] Credit hours updated to " + newCredits);
                break;
            default:
                System.out.println("\n[ERROR] Invalid choice");
        }
    }

    private void listAllAcademicProfiles() {
        System.out.println("\n--- List All Academic Profiles ---");
        
        if (!academicProfiles.isEmpty()) {
            System.out.println("\n[SUCCESS] Found " + academicProfiles.size() + " academic profile(s):");
            System.out.println("\n" + "-".repeat(100));
            System.out.printf("%-15s %-15s %-15s\n", 
                "Student ID", "CGPA", "Credit Hours");
            System.out.println("-".repeat(50));
            for (AcademicProfile p : academicProfiles) {
                System.out.printf("%-15s %-15s %-15d\n",
                    p.getStudentId(),
                    p.getCumulativeGpa() != null ? p.getCumulativeGpa().toString() : "N/A",
                    p.getTotalCreditsEarned());
            }
            System.out.println("-".repeat(100));
        } else {
            System.out.println("\n[INFO] No academic profiles available.");
        }
    }

    // ===== Disciplinary Record Management =====
    private void disciplinaryRecordMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  DISCIPLINARY RECORD MANAGEMENT");
        System.out.println("=".repeat(60));
        System.out.println("  [1] Create disciplinary record");
        System.out.println("  [2] View disciplinary records for student");
        System.out.println("  [3] Update disciplinary record");
        System.out.println("  [4] Delete disciplinary record");
        System.out.println("  [5] List all disciplinary records");
        System.out.println("  [0] Back to main menu");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                createDisciplinaryRecord();
                break;
            case "2":
                viewDisciplinaryRecords();
                break;
            case "3":
                updateDisciplinaryRecord();
                break;
            case "4":
                deleteDisciplinaryRecord();
                break;
            case "5":
                listAllDisciplinaryRecords();
                break;
            case "0":
                return;
            default:
                System.out.println("\n[ERROR] Invalid choice.");
        }
    }

    private void createDisciplinaryRecord() {
        System.out.println("\n--- Create Disciplinary Record ---");
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        
        Student student = students.stream()
            .filter(s -> s.getStudentId().equalsIgnoreCase(studentId))
            .findFirst()
            .orElse(null);
        
        if (student == null) {
            System.out.println("\n[ERROR] Student not found: " + studentId);
            return;
        }
        
        System.out.print("Violation Type: ");
        String violationType = scanner.nextLine().trim();
        
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        
        // Validate Severity (only LOW, MODERATE, HIGH)
        String severityStr = promptUntilValid(
            "Severity (LOW/MODERATE/HIGH): ",
            "Invalid severity. Please enter LOW, MODERATE, or HIGH.",
            this::isValidSeverity,
            true
        );
        DisciplinaryRecord.Severity severity = DisciplinaryRecord.Severity.valueOf(severityStr.toUpperCase());
        
        System.out.print("Record Status (OPEN/UNDER_REVIEW/RESOLVED/APPEALED/CLOSED): ");
        String statusStr = scanner.nextLine().trim();
        DisciplinaryRecord.RecordStatus status = DisciplinaryRecord.RecordStatus.valueOf(statusStr.toUpperCase());
        
        DisciplinaryRecord record = new DisciplinaryRecord();
        record.setId(nextRecordId++);
        record.setStudentId(studentId);
        record.setViolationType(violationType);
        record.setDescription(description);
        record.setSeverity(severity);
        record.setStatus(status);
        record.setIncidentDate(LocalDate.now());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        
        disciplinaryRecords.add(record);
        
        System.out.println("\n[SUCCESS] Disciplinary record created!");
        System.out.println("  Record ID: " + record.getId());
        System.out.println("  Student ID: " + studentId);
        System.out.println("  Violation: " + violationType);
        System.out.println("  Severity: " + severity);
        System.out.println("  Status: " + status);
    }

    private void viewDisciplinaryRecords() {
        System.out.println("\n--- View Disciplinary Records ---");
        System.out.print("Enter student ID: ");
        String studentId = scanner.nextLine().trim();
        
        List<DisciplinaryRecord> records = disciplinaryRecords.stream()
            .filter(r -> r.getStudentId().equalsIgnoreCase(studentId))
            .toList();
        
        if (!records.isEmpty()) {
            System.out.println("\n[SUCCESS] Found " + records.size() + " disciplinary record(s) for " + studentId + ":");
            for (DisciplinaryRecord r : records) {
                System.out.println("\n  Record ID: " + r.getId());
                System.out.println("  Violation: " + r.getViolationType());
                System.out.println("  Description: " + r.getDescription());
                System.out.println("  Severity: " + r.getSeverity());
                System.out.println("  Status: " + r.getStatus());
                System.out.println("  Incident Date: " + r.getIncidentDate());
            }
        } else {
            System.out.println("\n[INFO] No disciplinary records found for student: " + studentId);
        }
    }

    private void updateDisciplinaryRecord() {
        System.out.println("\n--- Update Disciplinary Record ---");
        System.out.print("Enter record ID: ");
        Long recordId = Long.parseLong(scanner.nextLine().trim());
        
        DisciplinaryRecord record = disciplinaryRecords.stream()
            .filter(r -> r.getId().equals(recordId))
            .findFirst()
            .orElse(null);
        
        if (record == null) {
            System.out.println("\n[NOT FOUND] Disciplinary record not found: " + recordId);
            return;
        }
        
        System.out.println("\nCurrent record:");
        System.out.println("  Violation: " + record.getViolationType());
        System.out.println("  Status: " + record.getStatus());
        
        System.out.println("\nWhat would you like to update?");
        System.out.println("  [1] Record Status");
        System.out.print("Enter choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                System.out.print("New status (OPEN/UNDER_REVIEW/RESOLVED/APPEALED/CLOSED): ");
                String newStatus = scanner.nextLine().trim();
                record.setStatus(DisciplinaryRecord.RecordStatus.valueOf(newStatus.toUpperCase()));
                record.setUpdatedAt(LocalDateTime.now());
                System.out.println("\n[SUCCESS] Record status updated to " + newStatus);
                break;
            default:
                System.out.println("\n[ERROR] Invalid choice");
        }
    }

    private void deleteDisciplinaryRecord() {
        System.out.println("\n--- Delete Disciplinary Record ---");
        System.out.print("Enter record ID: ");
        Long recordId = Long.parseLong(scanner.nextLine().trim());
        
        DisciplinaryRecord record = disciplinaryRecords.stream()
            .filter(r -> r.getId().equals(recordId))
            .findFirst()
            .orElse(null);
        
        if (record == null) {
            System.out.println("\n[NOT FOUND] Disciplinary record not found: " + recordId);
            return;
        }
        
        System.out.print("Are you sure you want to delete record " + recordId + "? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        
        if (confirm.equalsIgnoreCase("yes")) {
            disciplinaryRecords.remove(record);
            System.out.println("\n[SUCCESS] Disciplinary record deleted: " + recordId);
        } else {
            System.out.println("\n[INFO] Deletion cancelled");
        }
    }

    private void listAllDisciplinaryRecords() {
        System.out.println("\n--- List All Disciplinary Records ---");
        
        if (!disciplinaryRecords.isEmpty()) {
            System.out.println("\n[SUCCESS] Found " + disciplinaryRecords.size() + " disciplinary record(s):");
            System.out.println("\n" + "-".repeat(100));
            System.out.printf("%-10s %-15s %-25s %-15s %-15s %-15s\n", 
                "ID", "Student ID", "Violation Type", "Severity", "Status", "Incident Date");
            System.out.println("-".repeat(100));
            for (DisciplinaryRecord r : disciplinaryRecords) {
                System.out.printf("%-10d %-15s %-25s %-15s %-15s %-15s\n",
                    r.getId(),
                    r.getStudentId(),
                    r.getViolationType().length() > 25 ? r.getViolationType().substring(0, 22) + "..." : r.getViolationType(),
                    r.getSeverity(),
                    r.getStatus(),
                    r.getIncidentDate());
            }
            System.out.println("-".repeat(100));
        } else {
            System.out.println("\n[INFO] No disciplinary records available.");
        }
    }

    // ===== Enrollment Management =====
    private void enrollmentMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ENROLLMENT MANAGEMENT");
        System.out.println("=".repeat(60));
        System.out.println("  [1] Create enrollment");
        System.out.println("  [2] View enrollments for student");
        System.out.println("  [3] Update enrollment");
        System.out.println("  [4] Drop enrollment");
        System.out.println("  [5] Delete enrollment");
        System.out.println("  [0] Back to main menu");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                createEnrollment();
                break;
            case "2":
                viewEnrollmentsByStudent();
                break;
            case "3":
                updateEnrollment();
                break;
            case "4":
                dropEnrollment();
                break;
            case "5":
                deleteEnrollment();
                break;
            case "0":
                return;
            default:
                System.out.println("\n[ERROR] Invalid choice.");
        }
    }

    private void createEnrollment() {
        System.out.println("\n--- Create Enrollment ---");
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();
        
        Student student = students.stream()
            .filter(s -> s.getStudentId().equalsIgnoreCase(studentId))
            .findFirst()
            .orElse(null);
        
        if (student == null) {
            System.out.println("\n[ERROR] Student not found: " + studentId);
            return;
        }
        
        // Semester ID (required)
        String semester = promptUntilValid(
            "Semester ID: ",
            "Semester ID is required. Please enter a value.",
            sem -> sem != null && !sem.trim().isEmpty(),
            true
        );
        
        // Credit Hours (required, must be a number)
        String creditsStr = promptUntilValid(
            "Credit Hours: ",
            "Credit hours must be a valid number. Please enter a positive integer.",
            this::isValidCreditHours,
            true
        );
        Integer credits = Integer.parseInt(creditsStr);
        
        // Validate Enrollment Status (only ENROLLED, DROPPED, COMPLETED)
        String statusStr = promptUntilValid(
            "Enrollment Status (ENROLLED/DROPPED/COMPLETED): ",
            "Invalid status. Please enter ENROLLED, DROPPED, or COMPLETED.",
            this::isValidEnrollmentStatus,
            false
        );
        Enrollment.EnrollmentStatus status = statusStr.isEmpty() ? Enrollment.EnrollmentStatus.ENROLLED : 
                                            Enrollment.EnrollmentStatus.valueOf(statusStr.toUpperCase());
        
        Enrollment enrollment = new Enrollment();
        enrollment.setId(nextEnrollmentId++);
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(""); // Not required
        enrollment.setCourseCode(""); // Not required
        enrollment.setCourseName(""); // Not required
        enrollment.setSemester(semester);
        enrollment.setAcademicYear(""); // Not required
        enrollment.setCredits(credits);
        enrollment.setEnrollmentStatus(status);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setCreatedAt(LocalDateTime.now());
        enrollment.setUpdatedAt(LocalDateTime.now());
        
        enrollments.add(enrollment);
        
        System.out.println("\n[SUCCESS] Enrollment created!");
        System.out.println("  Enrollment ID: " + enrollment.getId());
        System.out.println("  Student ID: " + studentId);
        System.out.println("  Semester ID: " + semester);
        System.out.println("  Credit Hours: " + credits);
        System.out.println("  Status: " + status);
    }

    private void viewEnrollmentsByStudent() {
        System.out.println("\n--- View Enrollments by Student ---");
        System.out.print("Enter student ID: ");
        String studentId = scanner.nextLine().trim();
        
        List<Enrollment> studentEnrollments = enrollments.stream()
            .filter(e -> e.getStudentId().equalsIgnoreCase(studentId))
            .toList();
        
        if (!studentEnrollments.isEmpty()) {
            System.out.println("\n[SUCCESS] Found " + studentEnrollments.size() + " enrollment(s) for " + studentId + ":");
            System.out.println("\n" + "-".repeat(100));
            System.out.printf("%-10s %-20s %-15s %-15s\n", 
                "ID", "Semester ID", "Credit Hours", "Status");
            System.out.println("-".repeat(70));
            for (Enrollment e : studentEnrollments) {
                System.out.printf("%-10d %-20s %-15d %-15s\n",
                    e.getId(),
                    e.getSemester() != null && !e.getSemester().isEmpty() ? e.getSemester() : "N/A",
                    e.getCredits() != null ? e.getCredits() : 0,
                    e.getEnrollmentStatus());
            }
            System.out.println("-".repeat(100));
        } else {
            System.out.println("\n[INFO] No enrollments found for student: " + studentId);
        }
    }


    private void updateEnrollment() {
        System.out.println("\n--- Update Enrollment ---");
        System.out.print("Enter enrollment ID: ");
        Long enrollmentId = Long.parseLong(scanner.nextLine().trim());
        
        Enrollment enrollment = enrollments.stream()
            .filter(e -> e.getId().equals(enrollmentId))
            .findFirst()
            .orElse(null);
        
        if (enrollment == null) {
            System.out.println("\n[NOT FOUND] Enrollment not found: " + enrollmentId);
            return;
        }
        
        System.out.println("\nCurrent enrollment:");
        System.out.println("  Student: " + enrollment.getStudentId());
        System.out.println("  Semester ID: " + (enrollment.getSemester() != null && !enrollment.getSemester().isEmpty() ? enrollment.getSemester() : "N/A"));
        System.out.println("  Credit Hours: " + (enrollment.getCredits() != null ? enrollment.getCredits() : "N/A"));
        System.out.println("  Status: " + enrollment.getEnrollmentStatus());
        
        System.out.println("\nWhat would you like to update?");
        System.out.println("  [1] Semester ID");
        System.out.println("  [2] Credit Hours");
        System.out.println("  [3] Enrollment Status");
        System.out.print("Enter choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                String newSemester = promptUntilValid(
                    "New Semester ID: ",
                    "Semester ID is required. Please enter a value.",
                    sem -> sem != null && !sem.trim().isEmpty(),
                    true
                );
                enrollment.setSemester(newSemester);
                enrollment.setUpdatedAt(LocalDateTime.now());
                System.out.println("\n[SUCCESS] Semester ID updated to " + newSemester);
                break;
            case "2":
                String newCreditsStr = promptUntilValid(
                    "New Credit Hours: ",
                    "Credit hours must be a valid number. Please enter a positive integer.",
                    this::isValidCreditHours,
                    true
                );
                Integer newCredits = Integer.parseInt(newCreditsStr);
                enrollment.setCredits(newCredits);
                enrollment.setUpdatedAt(LocalDateTime.now());
                System.out.println("\n[SUCCESS] Credit hours updated to " + newCredits);
                break;
            case "3":
                String newStatus = promptUntilValid(
                    "New status (ENROLLED/DROPPED/COMPLETED): ",
                    "Invalid status. Please enter ENROLLED, DROPPED, or COMPLETED.",
                    this::isValidEnrollmentStatus,
                    true
                );
                enrollment.setEnrollmentStatus(Enrollment.EnrollmentStatus.valueOf(newStatus.toUpperCase()));
                enrollment.setUpdatedAt(LocalDateTime.now());
                System.out.println("\n[SUCCESS] Enrollment status updated to " + newStatus);
                break;
            default:
                System.out.println("\n[ERROR] Invalid choice");
        }
    }

    private void dropEnrollment() {
        System.out.println("\n--- Drop Enrollment ---");
        System.out.print("Enter enrollment ID: ");
        Long enrollmentId = Long.parseLong(scanner.nextLine().trim());
        
        Enrollment enrollment = enrollments.stream()
            .filter(e -> e.getId().equals(enrollmentId))
            .findFirst()
            .orElse(null);
        
        if (enrollment == null) {
            System.out.println("\n[NOT FOUND] Enrollment not found: " + enrollmentId);
            return;
        }
        
        System.out.print("Reason for dropping: ");
        String reason = scanner.nextLine().trim();
        
        enrollment.setEnrollmentStatus(Enrollment.EnrollmentStatus.DROPPED);
        enrollment.setUpdatedAt(LocalDateTime.now());
        
        System.out.println("\n[SUCCESS] Enrollment dropped!");
        System.out.println("  Enrollment ID: " + enrollmentId);
        System.out.println("  Student: " + enrollment.getStudentId());
        System.out.println("  Reason: " + reason);
    }

    private void deleteEnrollment() {
        System.out.println("\n--- Delete Enrollment ---");
        System.out.print("Enter enrollment ID: ");
        Long enrollmentId = Long.parseLong(scanner.nextLine().trim());
        
        Enrollment enrollment = enrollments.stream()
            .filter(e -> e.getId().equals(enrollmentId))
            .findFirst()
            .orElse(null);
        
        if (enrollment == null) {
            System.out.println("\n[NOT FOUND] Enrollment not found: " + enrollmentId);
            return;
        }
        
        System.out.print("Are you sure you want to delete enrollment " + enrollmentId + "? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        
        if (confirm.equalsIgnoreCase("yes")) {
            enrollments.remove(enrollment);
            System.out.println("\n[SUCCESS] Enrollment deleted: " + enrollmentId);
        } else {
            System.out.println("\n[INFO] Deletion cancelled");
        }
    }

    // ===== Helper Methods =====
    private void viewAllData() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ALL CURRENT DATA");
        System.out.println("=".repeat(60));
        
        System.out.println("\n--- STUDENTS (" + students.size() + ") ---");
        if (students.isEmpty()) {
            System.out.println("  (No students yet)");
        } else {
            for (Student s : students) {
                System.out.println("  " + s.getStudentId() + ": " + s.getFirstName() + " " + s.getLastName() + 
                                 " | " + s.getEmail() + " | Status: " + s.getStatus());
            }
        }
        
        System.out.println("\n--- ACADEMIC PROFILES (" + academicProfiles.size() + ") ---");
        if (academicProfiles.isEmpty()) {
            System.out.println("  (No academic profiles yet)");
        } else {
            for (AcademicProfile p : academicProfiles) {
                System.out.println("  Student: " + p.getStudentId() + 
                                 " | CGPA: " + (p.getCumulativeGpa() != null ? p.getCumulativeGpa() : "N/A") + 
                                 " | Credit Hours: " + p.getTotalCreditsEarned());
            }
        }
        
        System.out.println("\n--- DISCIPLINARY RECORDS (" + disciplinaryRecords.size() + ") ---");
        if (disciplinaryRecords.isEmpty()) {
            System.out.println("  (No disciplinary records yet)");
        } else {
            for (DisciplinaryRecord r : disciplinaryRecords) {
                System.out.println("  Student: " + r.getStudentId() + " | Violation: " + r.getViolationType() + 
                                 " | Severity: " + r.getSeverity() + " | Status: " + r.getStatus());
            }
        }
        
        System.out.println("\n--- ENROLLMENTS (" + enrollments.size() + ") ---");
        if (enrollments.isEmpty()) {
            System.out.println("  (No enrollments yet)");
        } else {
            for (Enrollment e : enrollments) {
                System.out.println("  Student: " + e.getStudentId() + 
                                 " | Semester: " + (e.getSemester() != null && !e.getSemester().isEmpty() ? e.getSemester() : "N/A") +
                                 " | Credits: " + (e.getCredits() != null ? e.getCredits() : "N/A") +
                                 " | Status: " + e.getEnrollmentStatus());
            }
        }
    }

    private void displayStudentDetails(Student student) {
        System.out.println("  ID: " + student.getId());
        System.out.println("  Student ID: " + student.getStudentId());
        System.out.println("  Name: " + student.getFirstName() + " " + student.getLastName());
        System.out.println("  Email: " + student.getEmail());
        System.out.println("  Phone: " + (student.getPhone() != null && !student.getPhone().isEmpty() ? student.getPhone() : "N/A"));
        System.out.println("  Gender: " + (student.getGender() != null ? student.getGender() : "N/A"));
        System.out.println("  Program: " + (student.getProgram() != null && !student.getProgram().isEmpty() ? student.getProgram() : "N/A"));
        System.out.println("  Major: " + (student.getMajor() != null && !student.getMajor().isEmpty() ? student.getMajor() : "N/A"));
        System.out.println("  Status: " + student.getStatus());
        System.out.println("  Admission Date: " + student.getAdmissionDate());
    }

    private void displayAcademicProfileDetails(AcademicProfile profile) {
        System.out.println("  Student ID: " + profile.getStudentId());
        System.out.println("  CGPA: " + (profile.getCumulativeGpa() != null ? profile.getCumulativeGpa() : "N/A"));
        System.out.println("  Credit Hours: " + profile.getTotalCreditsEarned());
    }

    // ===== Validation Methods =====
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Basic email validation regex
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private boolean isValidStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return false;
        }
        // Check if student ID already exists
        return students.stream().noneMatch(s -> s.getStudentId().equalsIgnoreCase(studentId));
    }

    private boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return true; // Optional field
        }
        try {
            LocalDate.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidStatus(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return true; // Will default to ACTIVE
        }
        try {
            Student.StudentStatus status = Student.StudentStatus.valueOf(statusStr.toUpperCase());
            return status == Student.StudentStatus.ACTIVE || 
                   status == Student.StudentStatus.INACTIVE || 
                   status == Student.StudentStatus.GRADUATED;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidEnrollmentStatus(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return true; // Will default to ENROLLED
        }
        try {
            Enrollment.EnrollmentStatus status = Enrollment.EnrollmentStatus.valueOf(statusStr.toUpperCase());
            return status == Enrollment.EnrollmentStatus.ENROLLED || 
                   status == Enrollment.EnrollmentStatus.DROPPED || 
                   status == Enrollment.EnrollmentStatus.COMPLETED;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidCreditHours(String creditsStr) {
        if (creditsStr == null || creditsStr.trim().isEmpty()) {
            return false; // Credit hours is required
        }
        try {
            int credits = Integer.parseInt(creditsStr);
            return credits > 0; // Must be positive
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidCGPA(String cgpaStr) {
        if (cgpaStr == null || cgpaStr.trim().isEmpty()) {
            return false; // CGPA is required
        }
        try {
            double cgpa = Double.parseDouble(cgpaStr);
            return cgpa >= 0.0 && cgpa <= 4.0; // Must be between 0.0 and 4.0
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidSeverity(String severityStr) {
        if (severityStr == null || severityStr.trim().isEmpty()) {
            return false; // Severity is required
        }
        try {
            DisciplinaryRecord.Severity severity = DisciplinaryRecord.Severity.valueOf(severityStr.toUpperCase());
            return severity == DisciplinaryRecord.Severity.LOW || 
                   severity == DisciplinaryRecord.Severity.MODERATE || 
                   severity == DisciplinaryRecord.Severity.HIGH;
        } catch (Exception e) {
            return false;
        }
    }

    private String promptUntilValid(String prompt, String errorMessage, java.util.function.Predicate<String> validator, boolean required) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            
            if (input.isEmpty() && !required) {
                return input; // Optional field, empty is OK
            }
            
            if (input.isEmpty() && required) {
                System.out.println("[ERROR] This field is required. Please enter a value.");
                continue;
            }
            
            if (validator.test(input)) {
                return input;
            } else {
                System.out.println("[ERROR] " + errorMessage);
            }
        }
    }

    public static void main(String[] args) {
        InteractiveStudentDemo demo = new InteractiveStudentDemo();
        demo.start();
    }
}
