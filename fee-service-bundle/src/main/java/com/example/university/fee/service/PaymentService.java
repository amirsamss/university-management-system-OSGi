package com.example.university.fee.service;

import com.example.university.fee.model.Payment;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Payment management
 * UC-27: Track Payments & Outstanding Fees
 */
public interface PaymentService {

    /**
     * Record a payment
     */
    Payment recordPayment(Payment payment);

    /**
     * Get payment by ID
     */
    Payment getPaymentById(Long id);

    /**
     * Get payment by reference number
     */
    Payment getPaymentByReferenceNumber(String referenceNumber);

    /**
     * Get all payments for a student
     */
    List<Payment> getPaymentsByStudent(String studentId);

    /**
     * Get payments for an invoice
     */
    List<Payment> getPaymentsByInvoice(Long invoiceId);

    /**
     * Get payments by date range
     */
    List<Payment> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Reverse a payment
     */
    Payment reversePayment(Long id, String reason);

    /**
     * Get outstanding fees report
     */
    List<Object[]> getOutstandingFeesReport();
}
