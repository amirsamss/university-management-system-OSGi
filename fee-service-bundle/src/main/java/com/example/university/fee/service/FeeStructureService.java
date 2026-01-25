package com.example.university.fee.service;

import com.example.university.fee.model.FeeStructure;
import java.util.List;

/**
 * Service interface for Fee Structure management
 * UC-23: Configure Tuition Fee
 */
public interface FeeStructureService {

    /**
     * Create or update a fee structure
     */
    FeeStructure saveFeeStructure(FeeStructure feeStructure);

    /**
     * Get fee structure by ID
     */
    FeeStructure getFeeStructureById(Long id);

    /**
     * Get fee structure by academic year, department, and student type
     */
    FeeStructure getFeeStructure(String academicYear, String department, String studentType);

    /**
     * Get all fee structures
     */
    List<FeeStructure> getAllFeeStructures();

    /**
     * Get fee structures by academic year
     */
    List<FeeStructure> getFeeStructuresByAcademicYear(String academicYear);

    /**
     * Delete fee structure
     */
    void deleteFeeStructure(Long id);
}
