package com.example.university.lecturer.model;

public class LecturerSchedule {
    private Long lecturerId;
    private String day;
    private String time;
    private String activity;

    public LecturerSchedule(Long lecturerId, String day, String time, String activity) {
        this.lecturerId = lecturerId;
        this.day = day;
        this.time = time;
        this.activity = activity;
    }
    // Getters and Setters...
    public String toString() { return day + " " + time + ": " + activity; }

    public Long getLecturerId() { return lecturerId; }
    public String getDay() { return day; }
    public String getTime() { return time; }
    public String getActivity() { return activity; }

    public void setDay(String day) { this.day = day; }
    public void setTime(String time) { this.time = time; }
    public void setActivity(String activity) { this.activity = activity; }
}