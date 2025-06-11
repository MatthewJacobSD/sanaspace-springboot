package io.github.matthewjacobsd.sanaspace.controllers.record;

import io.github.matthewjacobsd.sanaspace.repositories.*;
import io.github.matthewjacobsd.sanaspace.utils.LoggerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for fetching dashboard analytics/statistics.
 * Provides endpoints to retrieve count-based metrics for various entities.
 */
@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CAnalytics {

    private final LoggerUtil logger = new LoggerUtil(CAnalytics.class);
    private final RDoctor doctorR;
    private final RPatient patientR;
    private final RMedication medicationR;
    private final RPrescription prescriptionR;
    private final RVisit visitR;
    private final RInsurance insuranceR;

    /**
     * Retrieves aggregated statistics for display on a dashboard.
     *
     * @return Map containing entity names as keys and their respective counts as values.
     */
    @GetMapping()
    public Map<String, Long> getDashboardStats() {
        long startTime = logger.startOperation("Fetching Dashboard Stats", Map.of(
            "endpoint", "/api/stats",
            "method", "GET"
        ));
        Map<String, Long> stats = new HashMap<>();
        try {
            // Counting Doctors
            stats.put("doctors", doctorR.count());
            logger.info("Doctors count: " + stats.get("doctors"));

            // Counting Patients
            stats.put("patients", patientR.count());
            logger.info("Patients count: " + stats.get("patients"));

            // Counting Medications
            stats.put("medications", medicationR.count());
            logger.info("Medications count: " + stats.get("medications"));

            // Counting Prescriptions
            stats.put("prescriptions", prescriptionR.count());
            logger.info("Prescriptions count: " + stats.get("prescriptions"));

            // Counting Visits
            stats.put("visits", visitR.count());
            logger.info("Visits count: " + stats.get("visits"));

            // Counting Insurances
            stats.put("insurances", insuranceR.count());
            logger.info("Insurances count: " + stats.get("insurances"));

            logger.success("Dashboard stats fetched successfully", startTime);
            return stats;
        } catch (Exception e) {
            logger.error("Failed to fetch dashboard stats", startTime, e,
                Map.of("statsAttempted", stats.keySet()).toString());
            throw e; // Re-throw to maintain existing error handling
        }
    }
}