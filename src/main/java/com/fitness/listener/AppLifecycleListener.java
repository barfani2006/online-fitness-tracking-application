package com.fitness.listener;

import com.fitness.util.AsyncAuditLogger;
import com.fitness.util.DBConnectionManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application Lifecycle Listener managing background services and resources.
 * Handles graceful startup and shutdown of connection pools and multithreaded logging workers.
 */
@WebListener
public class AppLifecycleListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppLifecycleListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing PulseFit Online Fitness Tracking Application...");
        try {
            // Warm up connection manager and verify database configuration
            DBConnectionManager.getInstance();
            logger.info("HikariCP connection pool initialized successfully.");

            // Start the asynchronous audit logging worker
            AsyncAuditLogger.getInstance();
            logger.info("Multithreaded AsyncAuditLogger worker started.");
        } catch (Exception e) {
            logger.error("Error during application initialization: {}", e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Shutting down PulseFit Online Fitness Tracking Application...");

        // 1. Shutdown multithreaded audit logger
        try {
            AsyncAuditLogger.getInstance().shutdown();
            logger.info("AsyncAuditLogger shutdown complete.");
        } catch (Exception e) {
            logger.error("Error shutting down AsyncAuditLogger: {}", e.getMessage());
        }

        // 2. Close HikariCP connection pool
        try {
            DBConnectionManager.getInstance().shutdown();
            logger.info("HikariCP connection pool closed.");
        } catch (Exception e) {
            logger.error("Error closing DBConnectionManager: {}", e.getMessage());
        }
    }
}
