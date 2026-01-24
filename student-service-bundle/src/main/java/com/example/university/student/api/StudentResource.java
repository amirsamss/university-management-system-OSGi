package com.example.university.student.api;

import com.example.university.student.model.Student;
import com.example.university.student.model.AcademicProfile;
import com.example.university.student.model.DisciplinaryRecord;
import com.example.university.student.model.Enrollment;
import com.example.university.student.service.StudentService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Resource for Student management
 * Uses JAX-RS Whiteboard pattern for OSGi
 */
@Component(service = Object.class, property = {
    "service.exported.interfaces=*",
    "service.exported.configs=org.apache.cxf.rs",
    "cxf.jaxrs.address=/api/students"
})
@Path("/api/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentResource {

    private static final Logger logger = LoggerFactory.getLogger(StudentResource.class);

    private StudentService studentService;

    @Reference
    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }

    // ========== Student Management Endpoints ==========

    /**
     * Create a new student
     * POST /api/students
     */
    @POST
    public Response createStudent(Student student) {
        logger.info("POST /api/students - Creating student: {}", student.getStudentId());
        try {
            Student saved = studentService.createStudent(student);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Student created successfully");
            response.put("data", saved);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            logger.error("Error creating student", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("success", false, "error", e.getMessage())).build();
        }
    }

    /**
     * Get student by ID
     * GET /api/students/{id}
     */
    @GET
    @Path("/{id}")
    public Response getStudentById(@PathParam("id") Long id) {
        logger.info("GET /api/students/{} - Fetching student", id);
        Student student = studentService.getStudentById(id);
        if (student == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("success", false, "message", "Student not found")).build();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", student);
        return Response.ok(response).build();
    }

    /**
     * Get student by student ID
     * GET /api/students/student-id/{studentId}
     */
    @GET
    @Path("/student-id/{studentId}")
    public Response getStudentByStudentId(@PathParam("studentId") String studentId) {
        logger.info("GET /api/students/student-id/{} - Fetching student", studentId);
        Student student = studentService.getStudentByStudentId(studentId);
        if (student == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("success", false, "message", "Student not found")).build();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", student);
        return Response.ok(response).build();
    }

    /**
     * Update student
     * PUT /api/students/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateStudent(@PathParam("id") Long id, Student student) {
        logger.info("PUT /api/students/{} - Updating student", id);
        try {
            student.setId(id);
            Student updated = studentService.updateStudent(student);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Student updated successfully");
            response.put("data", updated);
            return Response.ok(response).build();
        } catch (Exception e) {
            logger.error("Error updating student", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("success", false, "error", e.getMessage())).build();
        }
    }

    /**
     * Delete student
     * DELETE /api/students/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteStudent(@PathParam("id") Long id) {
        logger.info("DELETE /api/students/{} - Deleting student", id);
        try {
            studentService.deleteStudent(id);
            return Response.ok(Map.of("success", true, "message", "Student deleted successfully")).build();
        } catch (Exception e) {
            logger.error("Error deleting student", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("success", false, "error", e.getMessage())).build();
        }
    }

    /**
     * Get all students
     * GET /api/students
     */
    @GET
    public Response getAllStudents() {
        logger.info("GET /api/students - Fetching all students");
        List<Student> students = studentService.getAllStudents();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", students);
        response.put("count", students.size());
        return Response.ok(response).build();
    }

    /**
     * Get students by status
     * GET /api/students/status/{status}
     */
    @GET
    @Path("/status/{status}")
    public Response getStudentsByStatus(@PathParam("status") String status) {
        logger.info("GET /api/students/status/{} - Fetching students", status);
        try {
            Student.StudentStatus studentStatus = Student.StudentStatus.valueOf(status.toUpperCase());
            List<Student> students = studentService.getStudentsByStatus(studentStatus);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", students);
            response.put("count", students.size());
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("success", false, "error", "Invalid status")).build();
        }
    }

    /**
     * Search students
     * GET /api/students/search?keyword=...
     */
    @GET
    @Path("/search")
    public Response searchStudents(@QueryParam("keyword") String keyword) {
        logger.info("GET /api/students/search?keyword={} - Searching students", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("success", false, "error", "Keyword parameter is required")).build();
        }
        List<Student> students = studentService.searchStudents(keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", students);
        response.put("count", students.size());
        return Response.ok(response).build();
    }

    // ========== Academic Profile Endpoints ==========

    /**
     * Get academic profile by student ID
     * GET /api/students/{studentId}/academic-profile
     */
    @GET
    @Path("/{studentId}/academic-profile")
    public Response getAcademicProfile(@PathParam("studentId") String studentId) {
        logger.info("GET /api/students/{}/academic-profile - Fetching academic profile", studentId);
        AcademicProfile profile = studentService.getAcademicProfileByStudentId(studentId);
        if (profile == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("success", false, "message", "Academic profile not found")).build();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", profile);
        return Response.ok(response).build();
    }

    /**
     * Create or update academic profile
     * POST /api/students/{studentId}/academic-profile
     */
    @POST
    @Path("/{studentId}/academic-profile")
    public Response createOrUpdateAcademicProfile(@PathParam("studentId") String studentId, AcademicProfile profile) {
        logger.info("POST /api/students/{}/academic-profile - Creating/updating academic profile", studentId);
        try {
            profile.setStudentId(studentId);
            AcademicProfile existing = studentService.getAcademicProfileByStudentId(studentId);
            AcademicProfile saved;
            if (existing != null) {
                profile.setId(existing.getId());
                saved = studentService.updateAcademicProfile(profile);
            } else {
                saved = studentService.createAcademicProfile(profile);
            }
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Academic profile saved successfully");
            response.put("data", saved);
            return Response.ok(response).build();
        } catch (Exception e) {
            logger.error("Error saving academic profile", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("success", false, "error", e.getMessage())).build();
        }
    }

    // ========== Disciplinary Record Endpoints ==========

    /**
     * Create disciplinary record
     * POST /api/students/{studentId}/disciplinary-records
     */
    @POST
    @Path("/{studentId}/disciplinary-records")
    public Response createDisciplinaryRecord(@PathParam("studentId") String studentId, DisciplinaryRecord record) {
        logger.info("POST /api/students/{}/disciplinary-records - Creating disciplinary record", studentId);
        try {
            record.setStudentId(studentId);
            DisciplinaryRecord saved = studentService.createDisciplinaryRecord(record);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Disciplinary record created successfully");
            response.put("data", saved);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            logger.error("Error creating disciplinary record", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("success", false, "error", e.getMessage())).build();
        }
    }

    /**
     * Get disciplinary records by student ID
     * GET /api/students/{studentId}/disciplinary-records
     */
    @GET
    @Path("/{studentId}/disciplinary-records")
    public Response getDisciplinaryRecords(@PathParam("studentId") String studentId) {
        logger.info("GET /api/students/{}/disciplinary-records - Fetching disciplinary records", studentId);
        List<DisciplinaryRecord> records = studentService.getDisciplinaryRecordsByStudentId(studentId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", records);
        response.put("count", records.size());
        return Response.ok(response).build();
    }

    /**
     * Update disciplinary record
     * PUT /api/students/disciplinary-records/{id}
     */
    @PUT
    @Path("/disciplinary-records/{id}")
    public Response updateDisciplinaryRecord(@PathParam("id") Long id, DisciplinaryRecord record) {
        logger.info("PUT /api/students/disciplinary-records/{} - Updating disciplinary record", id);
        try {
            record.setId(id);
            DisciplinaryRecord updated = studentService.updateDisciplinaryRecord(record);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Disciplinary record updated successfully");
            response.put("data", updated);
            return Response.ok(response).build();
        } catch (Exception e) {
            logger.error("Error updating disciplinary record", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("success", false, "error", e.getMessage())).build();
        }
    }

    // ========== Enrollment Endpoints ==========

    /**
     * Create enrollment
     * POST /api/students/{studentId}/enrollments
     */
    @POST
    @Path("/{studentId}/enrollments")
    public Response createEnrollment(@PathParam("studentId") String studentId, Enrollment enrollment) {
        logger.info("POST /api/students/{}/enrollments - Creating enrollment", studentId);
        try {
            enrollment.setStudentId(studentId);
            Enrollment saved = studentService.createEnrollment(enrollment);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Enrollment created successfully");
            response.put("data", saved);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            logger.error("Error creating enrollment", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("success", false, "error", e.getMessage())).build();
        }
    }

    /**
     * Get enrollments by student ID
     * GET /api/students/{studentId}/enrollments
     */
    @GET
    @Path("/{studentId}/enrollments")
    public Response getEnrollments(@PathParam("studentId") String studentId) {
        logger.info("GET /api/students/{}/enrollments - Fetching enrollments", studentId);
        List<Enrollment> enrollments = studentService.getEnrollmentsByStudentId(studentId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", enrollments);
        response.put("count", enrollments.size());
        return Response.ok(response).build();
    }

    /**
     * Get enrollments by semester
     * GET /api/students/enrollments?semester=...&academicYear=...
     */
    @GET
    @Path("/enrollments")
    public Response getEnrollmentsBySemester(
            @QueryParam("semester") String semester,
            @QueryParam("academicYear") String academicYear) {
        logger.info("GET /api/students/enrollments?semester={}&academicYear={}", semester, academicYear);
        if (semester == null || academicYear == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("success", false, "error", "Semester and academicYear parameters are required")).build();
        }
        List<Enrollment> enrollments = studentService.getEnrollmentsBySemester(semester, academicYear);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", enrollments);
        response.put("count", enrollments.size());
        return Response.ok(response).build();
    }

    /**
     * Drop enrollment
     * POST /api/students/enrollments/{id}/drop
     */
    @POST
    @Path("/enrollments/{id}/drop")
    public Response dropEnrollment(@PathParam("id") Long id, Map<String, String> request) {
        logger.info("POST /api/students/enrollments/{}/drop - Dropping enrollment", id);
        try {
            String reason = request != null ? request.get("reason") : "Student request";
            Enrollment dropped = studentService.dropEnrollment(id, reason);
            if (dropped == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("success", false, "message", "Enrollment not found")).build();
            }
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Enrollment dropped successfully");
            response.put("data", dropped);
            return Response.ok(response).build();
        } catch (Exception e) {
            logger.error("Error dropping enrollment", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("success", false, "error", e.getMessage())).build();
        }
    }
}
