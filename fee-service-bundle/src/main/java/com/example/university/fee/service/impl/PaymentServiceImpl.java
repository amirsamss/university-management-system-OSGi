package com.example.university.fee.service.impl;

import com.example.university.fee.model.Payment;
import com.example.university.fee.service.PaymentService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

/**
 * Payment Service Implementation using OSGi Declarative Services
 * UC-27: Track Payments & Outstanding Fees
 */
@Component(service = PaymentService.class, immediate = true)
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Payment recordPayment(Payment payment) {
        logger.info("Recording payment for student: {}", payment.getStudentId());
        entityManager.persist(payment);
        entityManager.flush();
        return payment;
    }

    @Override
    public Payment getPaymentById(Long id) {
        return entityManager.find(Payment.class, id);
    }

    @Override
    public Payment getPaymentByReferenceNumber(String referenceNumber) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "SELECT p FROM Payment p WHERE p.referenceNumber = :ref", Payment.class);
        query.setParameter("ref", referenceNumber);
        return query.getSingleResult();
    }

    @Override
    public List<Payment> getPaymentsByStudent(String studentId) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "SELECT p FROM Payment p WHERE p.studentId = :studentId ORDER BY p.paymentDate DESC", Payment.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }

    @Override
    public List<Payment> getPaymentsByInvoice(Long invoiceId) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "SELECT p FROM Payment p WHERE p.invoice.id = :invoiceId", Payment.class);
        query.setParameter("invoiceId", invoiceId);
        return query.getResultList();
    }

    @Override
    public List<Payment> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end ORDER BY p.paymentDate DESC", Payment.class);
        query.setParameter("start", startDate);
        query.setParameter("end", endDate);
        return query.getResultList();
    }

    @Override
    public Payment reversePayment(Long id, String reason) {
        Payment payment = getPaymentById(id);
        if (payment != null) {
            payment.setStatus(Payment.PaymentStatus.REVERSED);
            payment.setNotes(reason);
            entityManager.merge(payment);
            logger.info("Payment {} reversed: {}", id, reason);
        }
        return payment;
    }
}
