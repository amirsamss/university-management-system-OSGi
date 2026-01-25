package com.example.university.lecturer.demo;

import com.example.university.lecturer.model.Lecturer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InteractiveLecturerDemo implements Runnable {

    private List<Lecturer> lecturers;
    private Scanner scanner;
    private boolean running = true;

    public InteractiveLecturerDemo() {
        lecturers = new ArrayList<>();
        scanner = new Scanner(System.in);
        initializeMockData();
    }

    private void initializeMockData() {
        // 1. Create Lecturers
        Lecturer l1 = new Lecturer("L001", "Dr. Aishah", "Software Eng.", "Active", 92);
        l1.addReview("2024: Excellent research output. Student feedback 4.8/5.");
        l1.addReview("2025: Organized a successful hackathon.");
        l1.addSchedule("MON 10:00 - Software Arch (DK1)");
        l1.addSchedule("WED 14:00 - Databases (Lab 2)");

        Lecturer l2 = new Lecturer("L002", "Dr. John Doe", "Comp. System", "On Leave", 85);
        l2.addReview("2024: Good teaching, needs more publications.");
        l2.addSchedule("TUE 09:00 - Comp. Math (BK5)");

        Lecturer l3 = new Lecturer("L003", "Prof. Sarah", "Data Science", "Active", 98);
        l3.addReview("2025: Awarded Best Lecturer of the Year.");
        l3.addSchedule("FRI 10:00 - AI Fundamentals (Auditorium)");

        lecturers.add(l1);
        lecturers.add(l2);
        lecturers.add(l3);
    }

    @Override
    public void run() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("   LECTURER MANAGEMENT MODULE (OSGi Demo)");
        System.out.println("=".repeat(60));

        while (running) {
            try {
                displayMainMenu();
                if (scanner.hasNext()) {
                    String choice = scanner.next();
                    handleMainMenu(choice);
                }
                Thread.sleep(200); // Prevent CPU spiking
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine(); // Clear buffer
            }
        }
    }

    // --- MENUS ---

    private void displayMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("[1] Lecturer Directory (View All)");
        System.out.println("[2] Manage Lecturer Info (Edit)");
        System.out.println("[3] Performance Tracking (KPIs)");
        System.out.println("[4] Generate Timetable");
        System.out.println("[0] Exit");
        System.out.print("Enter choice: ");
    }

    private void handleMainMenu(String choice) {
        switch (choice) {
            case "1": listAllLecturers(); break;
            case "2": manageInfoMenu(); break;
            case "3": performanceMenu(); break;
            case "4": timetableMenu(); break;
            case "0": 
                System.out.println("Exiting Demo..."); 
                running = false; 
                break;
            default: System.out.println("Invalid option.");
        }
    }

    // --- 1. DIRECTORY ---
    private void listAllLecturers() {
        System.out.println("\n>>> LECTURER DIRECTORY");
        System.out.println("| ID    | Name                 | Department         | Status     |");
        System.out.println("|-------|----------------------|--------------------|------------|");
        for (Lecturer l : lecturers) {
            System.out.println(l);
        }
    }

    // --- 2. MANAGE INFO ---
    private void manageInfoMenu() {
        System.out.println("\n>>> MANAGE INFO");
        System.out.print("Enter Lecturer ID to edit (e.g., L001): ");
        String id = scanner.next();
        Lecturer l = findLecturer(id);
        
        if (l != null) {
            System.out.println("Editing: " + l.getName());
            System.out.println("[1] Update Name");
            System.out.println("[2] Update Status");
            System.out.print("Choice: ");
            String subChoice = scanner.next();
            
            if (subChoice.equals("1")) {
                System.out.print("Enter new name (use_underscores_for_space): ");
                String newName = scanner.next();
                l.setName(newName.replace("_", " "));
                System.out.println("[SUCCESS] Name updated.");
            } else if (subChoice.equals("2")) {
                System.out.print("Enter status (Active/OnLeave): ");
                l.setStatus(scanner.next());
                System.out.println("[SUCCESS] Status updated.");
            }
        } else {
            System.out.println("[ERROR] Lecturer not found.");
        }
    }

    // --- 3. PERFORMANCE ---
    private void performanceMenu() {
        System.out.println("\n>>> PERFORMANCE TRACKING");
        System.out.println("| ID    | Name                 | KPI Score | Rating     |");
        System.out.println("|-------|----------------------|-----------|------------|");
        for (Lecturer l : lecturers) {
            String rating = l.getKpiScore() >= 90 ? "Excellent" : "Good";
            System.out.printf("| %-5s | %-20s | %-9d | %-10s |\n", l.getId(), l.getName(), l.getKpiScore(), rating);
        }
        
        System.out.println("\n[1] View Reviews");
        System.out.println("[0] Back");
        System.out.print("Choice: ");
        if (scanner.next().equals("1")) {
            System.out.print("Enter ID: ");
            Lecturer l = findLecturer(scanner.next());
            if (l != null) {
                System.out.println("Reviews for " + l.getName() + ":");
                l.getReviews().forEach(r -> System.out.println(" - " + r));
            }
        }
    }

    // --- 4. TIMETABLE ---
    private void timetableMenu() {
        System.out.println("\n>>> GENERATE TIMETABLE");
        System.out.println("Select functionality:");
        System.out.println("[1] View Weekly Schedule");
        System.out.println("[2] Add Class Slot (Mock)");
        System.out.print("Choice: ");
        String choice = scanner.next();

        if (choice.equals("1")) {
            System.out.print("Enter Lecturer ID: ");
            Lecturer l = findLecturer(scanner.next());
            if (l != null) {
                System.out.println("\nSchedule for " + l.getName() + ":");
                if (l.getSchedule().isEmpty()) System.out.println(" (No classes assigned)");
                else l.getSchedule().forEach(s -> System.out.println(" [CLASS] " + s));
            }
        } else if (choice.equals("2")) {
            System.out.print("Enter ID: ");
            Lecturer l = findLecturer(scanner.next());
            if (l != null) {
                System.out.println("Adding mock slot: THU 11:00 - Consultation");
                l.addSchedule("THU 11:00 - Consultation");
                System.out.println("[SUCCESS] Timetable updated.");
            }
        }
    }

    private Lecturer findLecturer(String id) {
        return lecturers.stream().filter(l -> l.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
    
    public void stop() {
        this.running = false;
    }
}