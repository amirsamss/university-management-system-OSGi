package com.example.university.lecturer.model;

public class PerformanceReview {
    private Long lecturerId;
    private int rating; // 1-5
    private String feedback;

    public PerformanceReview(Long lecturerId, int rating, String feedback) {
        this.lecturerId = lecturerId;
        this.rating = rating;
        this.feedback = feedback;
    }
    // Getters and Setters...
    public String toString() { return "Rating: " + rating + "/5 - " + feedback; }
}