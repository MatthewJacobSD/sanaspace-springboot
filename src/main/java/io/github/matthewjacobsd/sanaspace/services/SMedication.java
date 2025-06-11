package io.github.matthewjacobsd.sanaspace.services;

import io.github.matthewjacobsd.sanaspace.exceptions.ExpMedication;
import io.github.matthewjacobsd.sanaspace.models.Medication;
import io.github.matthewjacobsd.sanaspace.repositories.RMedication;
import io.github.matthewjacobsd.sanaspace.utils.GlobalExceptionHandler.SupplierWithException;
import io.github.matthewjacobsd.sanaspace.utils.LoggerUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class SMedication {

    private final LoggerUtil logger = new LoggerUtil(SMedication.class);
    private final RMedication medicationR;

    // Helper method to handle operations and exceptions
    private <T> T handleOperation(String operation, long startTime, SupplierWithException<T> supplier) {
        try {
            T result = supplier.get();
            logger.success("✅ " + operation, startTime);
            return result;
        } catch (ExpMedication e) {
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

    // Saves a new Medication entity
    public Medication saveMedication(@NotNull Medication m) {
        long startTime = logger.startOperation("Saving medication...", Map.of("name", m.getName()));
        return handleOperation("save medication", startTime, () -> medicationR.save(m));
    }

    // Retrieves all Medications with pagination
    public Page<Medication> getPagedMedications(Pageable pageable) {
        long startTime = logger.startOperation("Fetching medications...", Map.of("page", pageable.getPageNumber(), "size", pageable.getPageSize()));
        return handleOperation("fetch all medications", startTime, () -> medicationR.findAll(pageable));
    }

    // Retrieves all Medications with pagination (legacy method)
    public Page<Medication> fetchAllMedications(int page, int size) {
        long startTime = logger.startOperation("Fetching medications...", Map.of("page", page, "size", size));
        return handleOperation("fetch all medications", startTime, () ->
                medicationR.findAll(PageRequest.of(page, size)));
    }

    // Retrieves a Medication by ID
    public Medication fetchMedicationById(String id) {
        long startTime = logger.startOperation("Fetching single medication...", Map.of("id", id));
        return handleOperation("fetch medication by ID", startTime, () ->
                medicationR.findById(id).orElseThrow(() -> new ExpMedication(id)));
    }

    // Updates a Medication by ID
    public Medication updateMedication(String id, Medication m) {
        long startTime = logger.startOperation("Updating medication...", Map.of("id", id, "name", m.getName()));
        return handleOperation("update medication", startTime, () -> {
            Medication existingMedication = medicationR.findById(id).orElseThrow(() -> new ExpMedication(id));
            existingMedication.setName(m.getName());
            existingMedication.setSideEffects(m.getSideEffects());
            existingMedication.setBenefits(m.getBenefits());
            return medicationR.save(existingMedication);
        });
    }

    // Partial update of a Medication by ID
    public Medication updateMedicationFields(@NotNull String id, @NotNull Map<String, Object> updates) {
        long startTime = logger.startOperation("Updating medication fields...", Map.of("id", id, "updates", updates.keySet()));
        return handleOperation("partial update medication", startTime, () -> {
            Medication medication = medicationR.findById(id).orElseThrow(() -> new ExpMedication(id));
            updates.forEach((field, value) -> {
                switch (field) {
                    case "name" -> medication.setName(value != null ? (String) value : null);
                    case "sideEffects" -> medication.setSideEffects(value != null ? (String) value : null);
                    case "benefits" -> medication.setBenefits(value != null ? (String) value : null);
                    default -> throw new IllegalArgumentException("Invalid field: " + field);
                }
            });
            return medicationR.save(medication);
        });
    }

    // Deletes a Medication by ID
    public void deleteMedication(String id) {
        long startTime = logger.startOperation("Deleting medication...", Map.of("id", id));
        handleOperation("delete medication", startTime, () -> {
            Medication medication = medicationR.findById(id).orElseThrow(() -> new ExpMedication(id));
            medicationR.delete(medication);
            return null;
        });
    }
}