package com.example.university.fee.service;

import com.example.university.fee.model.Invoice;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for Invoice management
 * UC-24: Generate Invoices
 * UC-25: Calculate Tuition
 */
public interface InvoiceService {

    /**
     * Calculate tuition for a student based on registered credits and fee structure
     * UC-25: Calculate Tuition
     */
    BigDecimal calculateTuition(String studentId, String academicYear, String semester, 
                                 String department, String studentType, Integer credits);

    /**
     * Generate a single invoice for a student
     * UC-24: Generate Invoices
     */
    Invoice generateInvoice(String studentId, String academicYear, String semester, 
                           String department, String studentType, Integer credits);

    /**
     * Generate invoices for all registered students in a semester (batch job)
     * UC-24: Generate Invoices
     */
    List<Invoice> generateInvoicesBatch(String academicYear, String semester);

    /**
     * Get invoice by ID
     */
    Invoice getInvoiceById(Long id);

    /**
     * Get invoice by invoice number
     */
    Invoice getInvoiceByNumber(String invoiceNumber);

    /**
     * Get invoices by student
     */
    List<Invoice> getInvoicesByStudent(String studentId);

    /**
     * Get invoices by status
     */
    List<Invoice> getInvoicesByStatus(Invoice.InvoiceStatus status);

    /**
     * Get outstanding invoices (unpaid or partially paid)
     */
    List<Invoice> getOutstandingInvoices();
}
