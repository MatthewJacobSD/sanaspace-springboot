package io.github.matthewjacobsd.sanaspace.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Map;

// Consistent logging with extra context
public class LoggerUtil {

    private final Logger logger;

    // Sets up logger for the calling class
    public LoggerUtil(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    // Logs operation start with message and metadata
    public long startOperation(String message, Map<String, Object> metadata) {
        long startTime = System.currentTimeMillis();
        logger.info("🚀 {} - {} - {}", message, metadata, Instant.now());
        return startTime;
    }

    // Logs success with duration
    public void success(String message, long startTime) {
        logger.info("✅ {} - Took {}ms", message, System.currentTimeMillis() - startTime);
    }

    // Logs error with context and stack trace snippet
    public void error(String message, long startTime, Exception e, String requestInfo) {
        String stackTrace = Arrays.stream(e.getStackTrace())
                .limit(3) // Limit to top 3 lines
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
        logger.error("❌ {}: {} - Request: {} - Took {}ms\nStack: {}",
                message, e.getMessage(), requestInfo, System.currentTimeMillis() - startTime, stackTrace);
    }

    // Logs general info
    public void info(String message) {
        logger.info("💡 {}", message);
    }

    // Logs warning
    public void warn(String message) {
        logger.warn("⚠️ {}", message);
    }
}