package com.example.university.exam;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bundle Activator for Exam Service Bundle
 * Manages the lifecycle of the OSGi bundle
 */
public class Activator implements BundleActivator {

    private static final Logger logger = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext context) throws Exception {
        logger.info("Exam Service Bundle starting...");
        logger.info("Exam Service Bundle started successfully");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        logger.info("Exam Service Bundle stopping...");
        logger.info("Exam Service Bundle stopped");
    }
}
