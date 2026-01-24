package com.example.university.fee.service;

import com.example.university.fee.model.Payment;
import java.time.LocalDate;
import java.util.List;

/**
 * Payment Service Interface
 */
public interface PaymentService {

    Payment recordPayment(Payment payment);
    Payment getPaymentById(Long id);
    Payment getPaymentByReferenceNumber(String referenceNumber);
    List<Payment> getPaymentsByStudent(String studentId);
    List<Payment> getPaymentsByInvoice(Long invoiceId);
    List<Payment> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate);
    Payment reversePayment(Long id, String reason);
}
