package com.example.university.fee.service;

import com.example.university.fee.model.Refund;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for Refund management
 * UC-28: Process Refund
 */
public interface RefundService {

    /**
     * Process a refund for a student
     * UC-28: Process Refund
     */
    Refund processRefund(String studentId, BigDecimal amount, String reason, 
                        Refund.RefundType refundType, String holdingSemester);

    /**
     * Get refund by ID
     */
    Refund getRefundById(Long id);

    /**
     * Get refunds by student
     */
    List<Refund> getRefundsByStudent(String studentId);

    /**
     * Get refunds by status
     */
    List<Refund> getRefundsByStatus(Refund.RefundStatus status);

    /**
     * Get credit balance for a student (negative outstanding amount)
     */
    BigDecimal getCreditBalance(String studentId);

    /**
     * Approve a refund
     */
    Refund approveRefund(Long id, String approvedBy);

    /**
     * Process/complete a refund
     */
    Refund completeRefund(Long id, String processedBy, String bankReference);
}
