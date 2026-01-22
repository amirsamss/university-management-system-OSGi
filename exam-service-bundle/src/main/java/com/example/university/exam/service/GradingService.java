package com.example.university.exam.service;

import com.example.university.exam.model.Exam;
import com.example.university.exam.model.Grade;
import java.util.List;

public interface GradingService {
    Exam scheduleExam(Exam exam);
    List<Exam> getAllExams();
    
    Grade submitGrade(Grade grade);
    List<Grade> getAllGrades();
    Double calculateGPA(String studentId);
    String generateTranscript(String studentId);
}