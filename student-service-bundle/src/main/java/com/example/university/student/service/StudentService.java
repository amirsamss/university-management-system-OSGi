package com.example.university.student.service;

import com.example.university.student.model.Student;
import com.example.university.student.model.AcademicProfile;
import com.example.university.student.model.DisciplinaryRecord;
import com.example.university.student.model.Enrollment;
import java.util.List;

/**
 * Service interface for Student management
 * Handles student records, academic profiles, disciplinary records, and enrollments
 */
public interface StudentService {

    // Student Management
    Student createStudent(Student student);
    Student getStudentById(Long id);
    Student getStudentByStudentId(String studentId);
    Student updateStudent(Student student);
    void deleteStudent(Long id);
    List<Student> getAllStudents();
    List<Student> getStudentsByStatus(Student.StudentStatus status);
    List<Student> searchStudents(String keyword);

    // Academic Profile Management
    AcademicProfile getAcademicProfileByStudentId(String studentId);
    AcademicProfile updateAcademicProfile(AcademicProfile profile);
    AcademicProfile createAcademicProfile(AcademicProfile profile);

    // Disciplinary Record Management
    DisciplinaryRecord createDisciplinaryRecord(DisciplinaryRecord record);
    DisciplinaryRecord getDisciplinaryRecordById(Long id);
    List<DisciplinaryRecord> getDisciplinaryRecordsByStudentId(String studentId);
    DisciplinaryRecord updateDisciplinaryRecord(DisciplinaryRecord record);
    void deleteDisciplinaryRecord(Long id);

    // Enrollment Management
    Enrollment createEnrollment(Enrollment enrollment);
    Enrollment getEnrollmentById(Long id);
    List<Enrollment> getEnrollmentsByStudentId(String studentId);
    List<Enrollment> getEnrollmentsByCourseId(String courseId);
    List<Enrollment> getEnrollmentsBySemester(String semester, String academicYear);
    Enrollment updateEnrollment(Enrollment enrollment);
    void deleteEnrollment(Long id);
    Enrollment dropEnrollment(Long id, String reason);
}
