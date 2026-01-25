package com.example.university.fee.api;
import com.example.university.fee.model.Payment;
import com.example.university.fee.service.PaymentService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Payment REST API Resource
 */
@Path("/api/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    private static final Logger logger = LoggerFactory.getLogger(PaymentResource.class);

    private PaymentService paymentService;

    @Reference
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GET
    public Response getAllPayments() {
        logger.info("GET /api/payments - Fetching all payments");
        if (paymentService == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PaymentService not available - returning empty list");
            response.put("data", List.of());
            return Response.ok(response).build();
        }
        List<Payment> payments = paymentService.getPaymentsByStudent(""); // Empty for all
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", payments);
        return Response.ok(response).build();
    }

    @POST
    public Response recordPayment(Payment payment) {
        logger.info("POST /api/payments - Recording payment for student: {}", payment.getStudentId());
        if (paymentService == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "PaymentService not available");
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(response).build();
        }
        try {
            Payment saved = paymentService.recordPayment(payment);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment recorded successfully");
            response.put("data", saved);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            logger.error("Error recording payment", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("success", false, "error", e.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getPaymentById(@PathParam("id") Long id) {
        logger.info("GET /api/payments/{} - Fetching payment", id);
        if (paymentService == null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(Map.of("success", false, "message", "PaymentService not available")).build();
        }
        Payment payment = paymentService.getPaymentById(id);
        if (payment == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("success", false, "message", "Payment not found")).build();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", payment);
        return Response.ok(response).build();
    }

    @GET
    @Path("/student/{studentId}")
    public Response getPaymentsByStudent(@PathParam("studentId") String studentId) {
        logger.info("GET /api/payments/student/{} - Fetching payments", studentId);
        if (paymentService == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", List.of());
            return Response.ok(response).build();
        }
        List<Payment> payments = paymentService.getPaymentsByStudent(studentId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", payments);
        return Response.ok(response).build();
    }

    @GET
    @Path("/date-range")
    public Response getPaymentsByDateRange(
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr) {
        logger.info("GET /api/payments/date-range - Fetching payments");
        if (paymentService == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", List.of());
            return Response.ok(response).build();
        }
        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            List<Payment> payments = paymentService.getPaymentsByDateRange(startDate, endDate);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", payments);
            response.put("count", payments.size());
            return Response.ok(response).build();
        } catch (Exception e) {
            logger.error("Error fetching payments by date range", e);
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("success", false, "error", e.getMessage())).build();
        }
    }
}
