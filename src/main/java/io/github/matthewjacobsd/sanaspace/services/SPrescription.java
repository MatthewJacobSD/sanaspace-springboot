package io.github.matthewjacobsd.sanaspace.services;

import io.github.matthewjacobsd.sanaspace.exceptions.ExpPrescription;
import io.github.matthewjacobsd.sanaspace.models.Prescription;
import io.github.matthewjacobsd.sanaspace.repositories.RPrescription;
import io.github.matthewjacobsd.sanaspace.utils.GlobalExceptionHandler.SupplierWithException;
import io.github.matthewjacobsd.sanaspace.utils.LoggerUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Map;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class SPrescription {

    private final LoggerUtil logger = new LoggerUtil(SPrescription.class);
    private final RPrescription prescriptionR;

    // Helper method to handle operations and exceptions
    private <T> T handleOperation(String operation, long startTime, SupplierWithException<T> supplier) {
        try {
            T result = supplier.get();
            logger.success("✅ " + operation, startTime);
            return result;
        } catch (ExpPrescription e) {
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

    // Saves a new Prescription entity
    public Prescription savePrescription(@NotNull Prescription p) {
        long startTime = logger.startOperation("Saving prescription...", Map.of("dosageAmount", p.getDosageAmount(), "prescriptionDate", p.getPrescriptionDate()));
        return handleOperation("save prescription", startTime, () -> prescriptionR.save(p));
    }

    // Retrieves all Prescriptions with pagination
    public Page<Prescription> fetchAllPrescriptions(int page, int size) {
        long startTime = logger.startOperation("Fetching prescriptions...", Map.of("page", page, "size", size));
        return handleOperation("fetch all prescriptions", startTime, () ->
                prescriptionR.findAll(PageRequest.of(page, size)));
    }

    // Retrieves a Prescription by ID
    public Prescription fetchPrescriptionById(String id) {
        long startTime = logger.startOperation("Fetching single prescription...", Map.of("id", id));
        return handleOperation("fetch prescription by ID", startTime, () ->
                prescriptionR.findById(id).orElseThrow(() -> new ExpPrescription(id)));
    }

    // Updates a Prescription by ID
    public Prescription updatePrescription(String id, Prescription p) {
        long startTime = logger.startOperation("Updating prescription...", Map.of("id", id, "dosageAmount", p.getDosageAmount()));
        return handleOperation("update prescription", startTime, () -> {
            Prescription existingPrescription = prescriptionR.findById(id).orElseThrow(() -> new ExpPrescription(id));
            existingPrescription.setPrescriptionDate(p.getPrescriptionDate());
            existingPrescription.setDosageAmount(p.getDosageAmount());
            existingPrescription.setDuration(p.getDuration());
            existingPrescription.setComment(p.getComment());
            existingPrescription.setPatient(p.getPatient());
            existingPrescription.setMedication(p.getMedication());
            existingPrescription.setDoctor(p.getDoctor());
            return prescriptionR.save(existingPrescription);
        });
    }

    // Partial update of a Prescription by ID
    public Prescription updatePrescriptionFields(@NotNull String id, @NotNull Map<String, Object> updates) {
        long startTime = logger.startOperation("Updating prescription fields...", Map.of("id", id, "updates", updates.keySet()));
        return handleOperation("partial update prescription", startTime, () -> {
            Prescription prescription = prescriptionR.findById(id).orElseThrow(() -> new ExpPrescription(id));
            updates.forEach((field, value) -> {
                switch (field) {
                    case "prescriptionDate" -> prescription.setPrescriptionDate(value != null ? LocalDate.parse((String) value) : null);
                    case "dosageAmount" -> prescription.setDosageAmount(value != null ? ((Number) value).intValue() : 0);
                    case "duration" -> prescription.setDuration(value != null ? ((Number) value).intValue() : 0);
                    case "comment" -> prescription.setComment(value != null ? (String) value : null);
                    default -> throw new IllegalArgumentException("Invalid field: " + field);
                }
            });
            return prescriptionR.save(prescription);
        });
    }

    // Deletes a Prescription by ID
    public void deletePrescription(String id) {
        long startTime = logger.startOperation("Deleting prescription...", Map.of("id", id));
        handleOperation("delete prescription", startTime, () -> {
            Prescription prescription = prescriptionR.findById(id).orElseThrow(() -> new ExpPrescription(id));
            prescriptionR.delete(prescription);
            return null;
        });
    }
}