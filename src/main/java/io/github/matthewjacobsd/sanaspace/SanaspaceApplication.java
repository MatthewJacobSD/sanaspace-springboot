package io.github.matthewjacobsd.sanaspace;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import io.github.matthewjacobsd.sanaspace.utils.LoggerUtil;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class SanaspaceApplication {

    private static final LoggerUtil logger = new LoggerUtil(SanaspaceApplication.class);

    // Start the application
    public static void main(String[] args) {
        long startTime = logger.startOperation("Starting Sanaspace application", Map.of());
        
        try {
            SpringApplication.run(SanaspaceApplication.class, args);
            logger.success("Sanaspace application started successfully", startTime);
        } catch (Exception e) {
            logger.error("Failed to start Sanaspace application", startTime, e, "Application startup");
            throw e;
        }
    }

    // Shows database tables
    @Bean
    CommandLineRunner logDatabaseTablesResults(DataSource data) {
        return args -> {
            long startTime = logger.startOperation("Fetching database tables", Map.of());
            JdbcTemplate jdbcTemplate = new JdbcTemplate(data);

            try {
                // Query list to show tables
                List<String> tables = jdbcTemplate.queryForList("SHOW TABLES", String.class);
                
                if (tables.isEmpty()) {
                    logger.error("No tables found in database", startTime, 
                              new RuntimeException("No tables found"), "Database inspection");
                } else {
                    logger.info("Tables found in database:");
                    tables.forEach(table -> logger.info(" - " + table));
                    logger.success("Successfully fetched database tables", startTime);
                }
            } catch (Exception e) {
                logger.error("Error fetching database tables", startTime, e, "Database inspection");
            }
        };
    }
}