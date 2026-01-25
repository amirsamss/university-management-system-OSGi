package com.example.university.exam.api;

import com.example.university.exam.model.Exam;
import com.example.university.exam.model.Grade;
import com.example.university.exam.service.GradingService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Component(service = Object.class, property = {
    "service.exported.interfaces=*",
    "service.exported.configs=org.apache.cxf.rs",
    "cxf.jaxrs.address=/api/exam"
})
@Path("/api/exam")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExamResource {

    private GradingService gradingService;

    @Reference
    public void setGradingService(GradingService gradingService) {
        this.gradingService = gradingService;
    }

    @POST
    @Path("/schedule")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response scheduleExam(Exam exam) {
        try {
            Exam scheduled = gradingService.scheduleExam(exam);
            return Response.ok(scheduled).build();
        } catch (Exception e) {
            return Response.serverError().entity("Error scheduling exam").build();
        }
    }

    @GET
    @Path("/all-exams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllExams() {
        List<Exam> exams = gradingService.getAllExams();
        return Response.ok(exams).build();
    }

    @POST
    @Path("/submit-grade")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitGrade(Grade grade) {
        try {
            Grade saved = gradingService.submitGrade(grade);
            return Response.ok(saved).build();
        } catch (Exception e) {
            return Response.serverError().entity("Error submitting grade").build();
        }
    }

    @GET
    @Path("/all-grades")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGrades() {
        List<Grade> grades = gradingService.getAllGrades();
        return Response.ok(grades).build();
    }

    @GET
    @Path("/gpa/{studentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGPA(@PathParam("studentId") String studentId) {
        Double gpa = gradingService.calculateGPA(studentId);
        return Response.ok(gpa).build();
    }

    @GET
    @Path("/transcript/{studentId}")
    @Produces(MediaType.TEXT_PLAIN) // 👈 Note: Plain text for the transcript report
    public Response getTranscript(@PathParam("studentId") String studentId) {
        String report = gradingService.generateTranscript(studentId);
        return Response.ok(report).build();
    }
}