package com.example.university.lecturer.model;

import java.util.ArrayList;
import java.util.List;

public class Lecturer {
    private String id;
    private String name;
    private String department;
    private String status;
    private int kpiScore;
    // Simple lists to hold related mock data inside the object for the demo
    private List<String> reviews = new ArrayList<>();
    private List<String> schedule = new ArrayList<>();

    public Lecturer(String id, String name, String department, String status, int kpiScore) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.status = status;
        this.kpiScore = kpiScore;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getKpiScore() { return kpiScore; }
    public void setKpiScore(int kpiScore) { this.kpiScore = kpiScore; }
    
    public List<String> getReviews() { return reviews; }
    public void addReview(String review) { this.reviews.add(review); }
    
    public List<String> getSchedule() { return schedule; }
    public void addSchedule(String slot) { this.schedule.add(slot); }

    @Override
    public String toString() {
        return String.format("| %-5s | %-20s | %-18s | %-10s |", id, name, department, status);
    }
}