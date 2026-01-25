package com.example.university.fee.service.impl;

import com.example.university.fee.model.AccountStatement;
import com.example.university.fee.service.AccountStatementService;
import com.example.university.fee.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

/**
 * Account Statement Service Implementation - JPA with Supabase
 * UC-29: View Account Statements
 */
public class AccountStatementServiceImpl implements AccountStatementService {

    @Override
    public List<AccountStatement> getAccountStatement(String studentId) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<AccountStatement> query = em.createQuery(
            "SELECT s FROM AccountStatement s WHERE s.studentId = :studentId " +
            "ORDER BY s.transactionDate ASC, s.id ASC", AccountStatement.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }

    @Override
    public List<AccountStatement> getAccountStatementByTerm(String studentId, String academicYear, String semester) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<AccountStatement> query = em.createQuery(
            "SELECT s FROM AccountStatement s WHERE s.studentId = :studentId " +
            "AND s.academicYear = :year AND s.semester = :semester " +
            "ORDER BY s.transactionDate ASC, s.id ASC", AccountStatement.class);
        query.setParameter("studentId", studentId);
        query.setParameter("year", academicYear);
        query.setParameter("semester", semester);
        return query.getResultList();
    }

    @Override
    public List<AccountStatement> getAccountStatementByDateRange(String studentId, LocalDate startDate, LocalDate endDate) {
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<AccountStatement> query = em.createQuery(
            "SELECT s FROM AccountStatement s WHERE s.studentId = :studentId " +
            "AND DATE(s.transactionDate) BETWEEN :start AND :end " +
            "ORDER BY s.transactionDate ASC, s.id ASC", AccountStatement.class);
        query.setParameter("studentId", studentId);
        query.setParameter("start", startDate);
        query.setParameter("end", endDate);
        return query.getResultList();
    }

    @Override
    public List<AccountStatement> generateTaxForm(String studentId, String taxYear) {
        // Get statements for the tax year
        LocalDate startDate = LocalDate.of(Integer.parseInt(taxYear), 1, 1);
        LocalDate endDate = LocalDate.of(Integer.parseInt(taxYear), 12, 31);
        
        EntityManager em = JpaUtil.getEntityManager();
        TypedQuery<AccountStatement> query = em.createQuery(
            "SELECT s FROM AccountStatement s WHERE s.studentId = :studentId " +
            "AND DATE(s.transactionDate) BETWEEN :start AND :end " +
            "AND s.academicYear LIKE :year " +
            "ORDER BY s.transactionDate", AccountStatement.class);
        query.setParameter("studentId", studentId);
        query.setParameter("start", startDate);
        query.setParameter("end", endDate);
        query.setParameter("year", taxYear + "%");
        
        return query.getResultList();
    }

    @Override
    public AccountStatement createStatementEntry(AccountStatement statement) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.beginTransaction();
            em.persist(statement);
            JpaUtil.commitTransaction();
            return statement;
        } catch (Exception e) {
            JpaUtil.rollbackTransaction();
            throw new RuntimeException("Failed to create statement entry: " + e.getMessage(), e);
        }
    }
}
