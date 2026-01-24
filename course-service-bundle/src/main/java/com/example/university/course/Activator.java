package com.example.university.course;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bundle Activator for Course Service Bundle - UC5 to UC8
 */
public class Activator implements BundleActivator {

    private static Logger logger = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext context) throws Exception {
        logger.info("Course Service Bundle started successfully");
        logger.info("Available endpoints:");
        logger.info("  - GET/POST/PUT/DELETE /api/courses");
        logger.info("  - GET /api/courses/{id}");
        logger.info("  - GET /api/courses/code/{courseCode}");
        logger.info("  - GET /api/courses/department/{department}");
        logger.info("  - GET /api/courses/year/{academicYear}");
        logger.info("  - GET /api/courses/semester/{semester}");
        logger.info("  - POST /api/courses/enrollments");
        logger.info("  - GET /api/courses/enrollments/{id}");
        logger.info("  - GET /api/courses/enrollments/student/{studentId}");
        logger.info("  - GET /api/courses/enrollments/course/{courseId}");
        logger.info("  - POST /api/courses/enrollments/{id}/drop");
        logger.info("  - POST /api/courses/enrollments/{id}/approve");
        logger.info("  - POST/PUT /api/courses/schedules");
        logger.info("  - GET /api/courses/schedules/{id}");
        logger.info("  - GET /api/courses/{courseId}/schedules");
        logger.info("  - GET /api/courses/schedules/instructor/{instructorId}");
        logger.info("  - DELETE /api/courses/schedules/{id}");
        logger.info("  - POST /api/courses/prerequisites");
        logger.info("  - GET /api/courses/{courseId}/prerequisites");
        logger.info("  - GET /api/courses/validate-prerequisites/{studentId}/{courseId}");
        logger.info("  - DELETE /api/courses/prerequisites/{id}");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        logger.info("Course Service Bundle stopped");
    }
}
