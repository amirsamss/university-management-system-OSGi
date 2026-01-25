package com.example.university.lecturer;

import com.example.university.lecturer.demo.InteractiveLecturerDemo;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private InteractiveLecturerDemo demo;
    private Thread demoThread;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Starting Lecturer Management Demo...");
        // Launch the interactive shell in a background thread
        demo = new InteractiveLecturerDemo();
        demoThread = new Thread(demo);
        demoThread.start();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Stopping Lecturer Demo...");
        if (demo != null) {
            demo.stop();
        }
    }
}