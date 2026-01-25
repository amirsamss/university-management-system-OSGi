package com.example.university.fee.service;

import com.example.university.fee.model.AccountStatement;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Account Statement management
 * UC-29: View Account Statements
 */
public interface AccountStatementService {

    /**
     * Get account statement for a student
     * UC-29: View Account Statements
     */
    List<AccountStatement> getAccountStatement(String studentId);

    /**
     * Get account statement for a specific term
     */
    List<AccountStatement> getAccountStatementByTerm(String studentId, String academicYear, String semester);

    /**
     * Get account statement for a date range
     */
    List<AccountStatement> getAccountStatementByDateRange(String studentId, LocalDate startDate, LocalDate endDate);

    /**
     * Generate tax form data for a student for a specific year
     */
    List<AccountStatement> generateTaxForm(String studentId, String taxYear);

    /**
     * Create a statement entry (used internally)
     */
    AccountStatement createStatementEntry(AccountStatement statement);
}
