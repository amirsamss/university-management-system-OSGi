package com.example.university.fee.service.impl;

import com.example.university.fee.model.FeeStructure;
import com.example.university.fee.model.FeeItem;
import com.example.university.fee.service.FeeStructureService;
import com.example.university.fee.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * Fee Structure Service Implementation - JPA with Supabase
 * UC-23: Configure Tuition Fee
 */
public class FeeStructureServiceImpl implements FeeStructureService {

    @Override
    public FeeStructure saveFeeStructure(FeeStructure feeStructure) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.beginTransaction();
            
            // Check if fee structure already exists
            FeeStructure existing = getFeeStructure(
                feeStructure.getAcademicYear(),
                feeStructure.getDepartment(),
                feeStructure.getStudentType()
            );
            
            if (existing != null && (feeStructure.getId() == null || !existing.getId().equals(feeStructure.getId()))) {
                // Update existing structure - get managed entity
                FeeStructure managed = em.find(FeeStructure.class, existing.getId());
                if (managed != null) {
                    managed.setCostPerCredit(feeStructure.getCostPerCredit());
                    managed.setStatus(feeStructure.getStatus());
                    
                    // Clear old fee items (orphanRemoval will delete them)
                    managed.getFeeItems().clear();
                    em.flush(); // Ensure deletions are processed
                    
                    // Add new fee items - create new instances and add to managed entity
                    for (FeeItem item : feeStructure.getFeeItems()) {
                        FeeItem newItem = new FeeItem();
                        newItem.setName(item.getName());
                        newItem.setDescription(item.getDescription());
                        newItem.setAmount(item.getAmount());
                        newItem.setFeeType(item.getFeeType());
                        newItem.setMandatory(item.getMandatory());
                        newItem.setRefundable(item.getRefundable());
                        // Use addFeeItem which sets the bidirectional relationship
                        managed.addFeeItem(newItem);
                    }
                    
                    feeStructure = em.merge(managed);
                } else {
                    // Fallback: merge the existing entity
                    existing.setCostPerCredit(feeStructure.getCostPerCredit());
                    existing.setStatus(feeStructure.getStatus());
                    existing.getFeeItems().clear();
                    for (FeeItem item : feeStructure.getFeeItems()) {
                        FeeItem newItem = new FeeItem();
                        newItem.setName(item.getName());
                        newItem.setDescription(item.getDescription());
                        newItem.setAmount(item.getAmount());
                        newItem.setFeeType(item.getFeeType());
                        newItem.setMandatory(item.getMandatory());
                        newItem.setRefundable(item.getRefundable());
                        existing.addFeeItem(newItem);
                    }
                    feeStructure = em.merge(existing);
                }
            } else {
                // New fee structure
                // Save items temporarily
                List<FeeItem> itemsToAdd = new java.util.ArrayList<>(feeStructure.getFeeItems());
                feeStructure.getFeeItems().clear();
                
                // Persist the structure first (without items) to get an ID
                if (feeStructure.getId() == null) {
                    em.persist(feeStructure);
                    em.flush(); // Force flush to get the ID assigned
                } else {
                    feeStructure = em.merge(feeStructure);
                    em.flush();
                }
                
                // Get the managed entity from the EntityManager
                FeeStructure managed = em.find(FeeStructure.class, feeStructure.getId());
                if (managed == null) {
                    throw new RuntimeException("Failed to retrieve managed fee structure after persist");
                }
                
                // Now create and persist items directly, referencing the managed structure
                for (FeeItem item : itemsToAdd) {
                    FeeItem newItem = new FeeItem();
                    newItem.setName(item.getName());
                    newItem.setDescription(item.getDescription());
                    newItem.setAmount(item.getAmount());
                    newItem.setFeeType(item.getFeeType());
                    newItem.setMandatory(item.getMandatory());
                    newItem.setRefundable(item.getRefundable());
                    // Set the managed structure reference
                    newItem.setFeeStructure(managed);
                    // Add to the managed structure's collection
                    managed.getFeeItems().add(newItem);
                    // Persist the item directly
                    em.persist(newItem);
                }
                
                // The structure is already managed, no need to merge
                feeStructure = managed;
            }
            
            JpaUtil.commitTransaction();
            return feeStructure;
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to save fee structure: " + e.getMessage(), e);
        }
    }

    @Override
    public FeeStructure getFeeStructureById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        return em.find(FeeStructure.class, id);
    }

    @Override
    public FeeStructure getFeeStructure(String academicYear, String department, String studentType) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<FeeStructure> query = em.createQuery(
            "SELECT f FROM FeeStructure f WHERE f.academicYear = :year " +
            "AND f.department = :dept AND f.studentType = :type", 
            FeeStructure.class);
        query.setParameter("year", academicYear);
        query.setParameter("dept", department);
        query.setParameter("type", studentType);
        
        List<FeeStructure> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<FeeStructure> getAllFeeStructures() {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<FeeStructure> query = em.createQuery(
            "SELECT f FROM FeeStructure f ORDER BY f.academicYear DESC, f.department", 
            FeeStructure.class);
        return query.getResultList();
    }

    @Override
    public List<FeeStructure> getFeeStructuresByAcademicYear(String academicYear) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<FeeStructure> query = em.createQuery(
            "SELECT f FROM FeeStructure f WHERE f.academicYear = :year ORDER BY f.department", 
            FeeStructure.class);
        query.setParameter("year", academicYear);
        return query.getResultList();
    }

    @Override
    public void deleteFeeStructure(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.beginTransaction();
            FeeStructure feeStructure = em.find(FeeStructure.class, id);
            if (feeStructure != null) {
                em.remove(feeStructure);
                JpaUtil.commitTransaction();
            }
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to delete fee structure: " + e.getMessage(), e);
        }
    }
}
