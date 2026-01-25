package com.example.university.fee.service;

import com.example.university.fee.model.FinancialAid;
import java.util.List;

/**
 * Service interface for Financial Aid management
 * UC-26: Manage Financial Aid
 */
public interface FinancialAidService {

    /**
     * Create a financial aid record
     */
    FinancialAid createFinancialAid(FinancialAid financialAid);

    /**
     * Apply financial aid allocation process (batch)
     * UC-26: Manage Financial Aid
     */
    List<FinancialAid> allocateFinancialAid();

    /**
     * Apply financial aid to a specific student
     */
    FinancialAid applyFinancialAidToStudent(String studentId, String academicYear, String semester);

    /**
     * Get financial aid by ID
     */
    FinancialAid getFinancialAidById(Long id);

    /**
     * Get financial aid by student
     */
    List<FinancialAid> getFinancialAidByStudent(String studentId);

    /**
     * Revoke financial aid (if eligibility breached)
     */
    FinancialAid revokeFinancialAid(Long id, String reason);

    /**
     * Update financial aid
     */
    FinancialAid updateFinancialAid(FinancialAid financialAid);
}
