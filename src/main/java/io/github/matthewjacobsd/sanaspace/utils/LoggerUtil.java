package io.github.matthewjacobsd.sanaspace.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Map;

public class LoggerUtil {

    private final Logger logger; // SLF4J logger instance

    public LoggerUtil(Class<?> clazz) { // Constructor with class for logger initialization
        this.logger = LoggerFactory.getLogger(clazz); // Initializes logger for given class
    }

    public long startOperation(String message, Map<String, Object> metadata) { // Logs start of operation
        long startTime = System.currentTimeMillis(); // Captures start time
        logger.info("[START] {} - {} - {}", message, metadata, Instant.now()); // Logs start message
        return startTime; // Returns start time for duration tracking
    }

    public void success(String message, long startTime) { // Logs successful operation
        logger.info("[SUCCESS] {} - Took {}ms", message, System.currentTimeMillis() - startTime); // Logs success with duration
    }

    public void error(String message, long startTime, Exception e, String requestInfo) { // Logs error with stack trace
        String stackTrace = Arrays.stream(e.getStackTrace()) // Gets stack trace
                .limit(3) // Limits to top 3 elements
                .map(StackTraceElement::toString) // Converts to string
                .collect(Collectors.joining("\n")); // Joins with newlines
        logger.error("[ERROR] {}: {} - Request: {} - Took {}ms\nStack: {}", // Logs error details
                message, e.getMessage(), requestInfo, System.currentTimeMillis() - startTime, stackTrace);
    }

    public void info(String message) { // Logs info message
        logger.info("[INFO] {}", message); // Logs info with prefix
    }

    public void warn(String message) { // Logs warning message
        logger.warn("[WARN] {}", message); // Logs warning with prefix
    }
}