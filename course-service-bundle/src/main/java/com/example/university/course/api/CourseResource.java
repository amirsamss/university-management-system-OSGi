package com.example.university.course.api;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.example.university.course.service.CourseService;
import com.example.university.course.model.Course;
import com.example.university.course.model.CourseEnrollment;
import com.example.university.course.model.CourseSchedule;
import com.example.university.course.model.CoursePrerequisite;

/**
 * Course REST API Resource - UC5 to UC8
 */
@Component(
    service = CourseResource.class,
    immediate = true,
    properties = {
        "osgi.jaxrs.resource=true",
        "osgi.http.whiteboard.context.select=(osgi.http.whiteboard.context.name=default)"
    }
)
@Path("/api/courses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourseResource {

    private CourseService courseService;

    @Reference
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    // UC5: View Course Information / Configure Course Catalog
    
    @POST
    public Response addCourse(Course course) {
        try {
            Course savedCourse = courseService.addCourse(course);
            return Response.status(Response.Status.CREATED).entity(savedCourse).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("Failed to add course: " + e.getMessage()))
                .build();
        }
    }

    @PUT
    public Response updateCourse(Course course) {
        try {
            Course updatedCourse = courseService.updateCourse(course);
            return Response.ok(updatedCourse).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("Failed to update course: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getCourseById(@PathParam("id") Long id) {
        Course course = courseService.getCourseById(id);
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(createErrorResponse("Course not found"))
                .build();
        }
        return Response.ok(course).build();
    }

    @GET
    @Path("/code/{courseCode}")
    public Response getCourseByCourseCode(@PathParam("courseCode") String courseCode) {
        Course course = courseService.getCourseByCourseCode(courseCode);
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(createErrorResponse("Course not found"))
                .build();
        }
        return Response.ok(course).build();
    }

    @GET
    public Response getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return Response.ok(courses).build();
    }

    @GET
    @Path("/department/{department}")
    public Response getCoursesByDepartment(@PathParam("department") String department) {
        List<Course> courses = courseService.getCoursesByDepartment(department);
        return Response.ok(courses).build();
    }

    @GET
    @Path("/year/{academicYear}")
    public Response getCoursesByAcademicYear(@PathParam("academicYear") String academicYear) {
        List<Course> courses = courseService.getCoursesByAcademicYear(academicYear);
        return Response.ok(courses).build();
    }

    @GET
    @Path("/semester/{semester}")
    public Response getCoursesBySemester(@PathParam("semester") String semester) {
        List<Course> courses = courseService.getCoursesBySemester(semester);
        return Response.ok(courses).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCourse(@PathParam("id") Long id) {
        try {
            courseService.deleteCourse(id);
            return Response.ok(createSuccessResponse("Course deleted successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("Failed to delete course: " + e.getMessage()))
                .build();
        }
    }

    // UC6: Manage Course Enrollment

    @POST
    @Path("/enrollments")
    public Response enrollStudent(CourseEnrollment enrollment) {
        try {
            // Validate capacity
            if (!courseService.validateEnrollmentCapacity(enrollment.getCourseId())) {
                return Response.status(Response.Status.CONFLICT)
                    .entity(createErrorResponse("Course is at full capacity"))
                    .build();
            }
            
            CourseEnrollment savedEnrollment = courseService.enrollStudent(enrollment);
            return Response.status(Response.Status.CREATED).entity(savedEnrollment).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("Failed to enroll student: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/enrollments/{id}")
    public Response getEnrollmentById(@PathParam("id") Long id) {
        CourseEnrollment enrollment = courseService.getEnrollmentById(id);
        if (enrollment == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(createErrorResponse("Enrollment not found"))
                .build();
        }
        return Response.ok(enrollment).build();
    }

    @GET
    @Path("/enrollments/student/{studentId}")
    public Response getEnrollmentsByStudent(@PathParam("studentId") String studentId) {
        List<CourseEnrollment> enrollments = courseService.getEnrollmentsByStudent(studentId);
        return Response.ok(enrollments).build();
    }

    @GET
    @Path("/enrollments/course/{courseId}")
    public Response getEnrollmentsByCourse(@PathParam("courseId") Long courseId) {
        List<CourseEnrollment> enrollments = courseService.getEnrollmentsByCourse(courseId);
        return Response.ok(enrollments).build();
    }

    @POST
    @Path("/enrollments/{id}/drop")
    public Response dropCourse(@PathParam("id") Long id) {
        try {
            courseService.dropCourse(id);
            return Response.ok(createSuccessResponse("Course dropped successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("Failed to drop course: " + e.getMessage()))
                .build();
        }
    }

    @POST
    @Path("/enrollments/{id}/approve")
    public Response approveEnrollment(@PathParam("id") Long id, 
                                     @QueryParam("approvedBy") String approvedBy) {
        try {
            courseService.approveEnrollment(id, approvedBy);
            return Response.ok(createSuccessResponse("Enrollment approved successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("Failed to approve enrollment: " + e.getMessage()))
                .build();
        }
    }

    // UC7: Set Course Timetable

    @POST
    @Path("/schedules")
    public Response addSchedule(CourseSchedule schedule) {
        try {
            CourseSchedule savedSchedule = courseService.addSchedule(schedule);
            return Response.status(Response.Status.CREATED).entity(savedSchedule).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("Failed to add schedule: " + e.getMessage()))
                .build();
        }
    }

    @PUT
    @Path("/schedules")
    public Response updateSchedule(CourseSchedule schedule) {
        try {
            CourseSchedule updatedSchedule = courseService.updateSchedule(schedule);
            return Response.ok(updatedSchedule).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("Failed to update schedule: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/schedules/{id}")
    public Response getScheduleById(@PathParam("id") Long id) {
        CourseSchedule schedule = courseService.getScheduleById(id);
        if (schedule == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(createErrorResponse("Schedule not found"))
                .build();
        }
        return Response.ok(schedule).build();
    }

    @GET
    @Path("/{courseId}/schedules")
    public Response getSchedulesByCourse(@PathParam("courseId") Long courseId) {
        List<CourseSchedule> schedules = courseService.getSchedulesByCourse(courseId);
        return Response.ok(schedules).build();
    }

    @GET
    @Path("/schedules/instructor/{instructorId}")
    public Response getSchedulesByInstructor(@PathParam("instructorId") String instructorId) {
        List<CourseSchedule> schedules = courseService.getSchedulesByInstructor(instructorId);
        return Response.ok(schedules).build();
    }

    @GET
    @Path("/schedules/venue/{venue}")
    public Response getSchedulesByVenue(@PathParam("venue") String venue) {
        List<CourseSchedule> schedules = courseService.getSchedulesByVenue(venue);
        return Response.ok(schedules).build();
    }

    @DELETE
    @Path("/schedules/{id}")
    public Response deleteSchedule(@PathParam("id") Long id) {
        try {
            courseService.deleteSchedule(id);
            return Response.ok(createSuccessResponse("Schedule deleted successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("Failed to delete schedule: " + e.getMessage()))
                .build();
        }
    }

    // UC8: Check Course Prerequisites

    @POST
    @Path("/prerequisites")
    public Response addPrerequisite(CoursePrerequisite prerequisite) {
        try {
            CoursePrerequisite savedPrereq = courseService.addPrerequisite(prerequisite);
            return Response.status(Response.Status.CREATED).entity(savedPrereq).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("Failed to add prerequisite: " + e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/{courseId}/prerequisites")
    public Response getPrerequisitesByCourse(@PathParam("courseId") Long courseId) {
        List<CoursePrerequisite> prerequisites = courseService.getPrerequisitesByCourse(courseId);
        return Response.ok(prerequisites).build();
    }

    @GET
    @Path("/validate-prerequisites/{studentId}/{courseId}")
    public Response validateStudentPrerequisites(@PathParam("studentId") String studentId,
                                               @PathParam("courseId") Long courseId) {
        try {
            boolean isValid = courseService.validateStudentPrerequisites(studentId, courseId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("studentId", studentId);
            response.put("courseId", courseId);
            response.put("prerequisitesValid", isValid);
            
            if (!isValid) {
                List<String> failedPrereqs = courseService.getFailedPrerequisites(studentId, courseId);
                response.put("failedPrerequisites", failedPrereqs);
            }
            
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("Failed to validate prerequisites: " + e.getMessage()))
                .build();
        }
    }

    @DELETE
    @Path("/prerequisites/{id}")
    public Response deletePrerequisite(@PathParam("id") Long id) {
        try {
            courseService.deletePrerequisite(id);
            return Response.ok(createSuccessResponse("Prerequisite deleted successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("Failed to delete prerequisite: " + e.getMessage()))
                .build();
        }
    }

    // Helper Methods
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        return response;
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        return response;
    }
}
