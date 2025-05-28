package io.github.matthewjacobsd.sanaspace.services;

import io.github.matthewjacobsd.sanaspace.exceptions.ExpVisit;
import io.github.matthewjacobsd.sanaspace.models.Visit;
import io.github.matthewjacobsd.sanaspace.models.keys.VisitId;
import io.github.matthewjacobsd.sanaspace.repositories.RVisit;
import io.github.matthewjacobsd.sanaspace.utils.GlobalExceptionHandler.SupplierWithException;
import io.github.matthewjacobsd.sanaspace.utils.LoggerUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class SVisit {

    private final LoggerUtil logger = new LoggerUtil(SVisit.class);
    private final RVisit visitR;

    // Helper method to handle operations and exceptions
    private <T> T handleOperation(String operation, long startTime, SupplierWithException<T> supplier) {
        try {
            T result = supplier.get();
            logger.success("✅ " + operation, startTime);
            return result;
        } catch (ExpVisit e) {
            logger.error("❌ " + e.getMessage(), startTime, e, operation);
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("❌ Invalid input for " + operation, startTime, e, operation);
            throw new IllegalArgumentException("Invalid data: " + e.getMessage() + "; errorCode=INVALID_INPUT");
        } catch (Exception e) {
            logger.error("❌ Failed " + operation, startTime, e, operation);
            throw new RuntimeException("Failed " + operation + ": " + e.getMessage() + "; errorCode=SERVER_ERROR");
        }
    }

    // Saves a new Visit entity
    public Visit saveVisit(@NotNull Visit v) {
        long startTime = logger.startOperation("Saving visit...", Map.of("visitDate", v.getVisitDate()));
        return handleOperation("save visit", startTime, () -> visitR.save(v));
    }

    // Retrieves all Visits with pagination
    public Page<Visit> fetchAllVisits(int page, int size) {
        long startTime = logger.startOperation("Fetching visits...", Map.of("page", page, "size", size));
        return handleOperation("fetch all visits", startTime, () ->
                visitR.findAll(PageRequest.of(page, size)));
    }

    // Retrieves a Visit by ID
    public Visit fetchVisitById(VisitId id) {
        long startTime = logger.startOperation("Fetching single visit...", Map.of("patientId", id.getPatientId(), "doctorId", id.getDoctorId(), "visitDate", id.getVisitDate()));
        return handleOperation("fetch visit by ID", startTime, () ->
                visitR.findById(id).orElseThrow(() -> new ExpVisit(id)));
    }

    // Updates a Visit by ID
    public Visit updateVisit(VisitId id, Visit v) {
        long startTime = logger.startOperation("Updating visit...", Map.of("patientId", id.getPatientId(), "doctorId", id.getDoctorId(), "visitDate", id.getVisitDate()));
        return handleOperation("update visit", startTime, () -> {
            Visit existingVisit = visitR.findById(id).orElseThrow(() -> new ExpVisit(id));
            existingVisit.setSymptoms(v.getSymptoms());
            existingVisit.setDiagnosis(v.getDiagnosis());
            return visitR.save(existingVisit);
        });
    }

    // Partial update of a Visit by ID
    public Visit updateVisitFields(@NotNull VisitId id, @NotNull Map<String, Object> updates) {
        long startTime = logger.startOperation("Updating visit fields...", Map.of("patientId", id.getPatientId(), "doctorId", id.getDoctorId(), "visitDate", id.getVisitDate(), "updates", updates.keySet()));
        return handleOperation("partial update visit", startTime, () -> {
            Visit visit = visitR.findById(id).orElseThrow(() -> new ExpVisit(id));
            updates.forEach((field, value) -> {
            switch (field) {
                case "symptoms" -> visit.setSymptoms(value != null ? (String) value : null);
                case "diagnosis" -> visit.setDiagnosis(value != null ? ((Number) value).intValue() : 0);
                default -> throw new IllegalArgumentException("Invalid field: " + field);
            }
        });
            return visitR.save(visit);
        });
    }

    // Deletes a Visit by ID
    public void deleteVisit(VisitId id) {
        long startTime = logger.startOperation("Deleting visit...", Map.of("patientId", id.getPatientId(), "doctorId", id.getDoctorId(), "visitDate", id.getVisitDate()));
        handleOperation("delete visit", startTime, () -> {
            Visit visit = visitR.findById(id).orElseThrow(() -> new ExpVisit(id));
            visitR.delete(visit);
            return null;
        });
    }
}