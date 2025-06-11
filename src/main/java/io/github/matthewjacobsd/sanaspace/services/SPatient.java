package io.github.matthewjacobsd.sanaspace.services;

import io.github.matthewjacobsd.sanaspace.exceptions.ExpPatient;
import io.github.matthewjacobsd.sanaspace.models.Patient;
import io.github.matthewjacobsd.sanaspace.repositories.RPatient;
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
public class SPatient {

    private final LoggerUtil logger = new LoggerUtil(SPatient.class);
    private final RPatient patientR;

    // Helper method to handle operations and exceptions
    private <T> T handleOperation(String operation, long startTime, SupplierWithException<T> supplier) {
        try {
            T result = supplier.get();
            logger.success("✅ " + operation, startTime);
            return result;
        } catch (ExpPatient e) {
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

    // Saves a new Patient entity
    public Patient savePatient(@NotNull Patient p) {
        long startTime = logger.startOperation("Saving patient...", Map.of("firstName", p.getFirstName(), "lastName", p.getLastName()));
        return handleOperation("save patient", startTime, () -> patientR.save(p));
    }

    // Retrieves all Patients with pagination
    public Page<Patient> getPagedPatients(Pageable pageable) {
        long startTime = logger.startOperation("Fetching patients...", Map.of("page", pageable.getPageNumber(), "size", pageable.getPageSize()));
        return handleOperation("fetch all patients", startTime, () -> patientR.findAll(pageable));
    }

    // Retrieves all Patients with pagination (legacy method)
    public Page<Patient> fetchAllPatients(int page, int size) {
        long startTime = logger.startOperation("Fetching patients...", Map.of("page", page, "size", size));
        return handleOperation("fetch all patients", startTime, () ->
                patientR.findAll(PageRequest.of(page, size)));
    }

    // Retrieves a Patient by ID
    public Patient fetchPatientById(String id) {
        long startTime = logger.startOperation("Fetching single patient...", Map.of("id", id));
        return handleOperation("fetch patient by ID", startTime, () ->
                patientR.findById(id).orElseThrow(() -> new ExpPatient(id)));
    }

    // Updates a Patient by ID
    public Patient updatePatient(String id, Patient p) {
        long startTime = logger.startOperation("Updating patient...", Map.of("id", id, "firstName", p.getFirstName(), "lastName", p.getLastName()));
        return handleOperation("update patient", startTime, () -> {
            Patient existingPatient = patientR.findById(id).orElseThrow(() -> new ExpPatient(id));
            existingPatient.setFirstName(p.getFirstName());
            existingPatient.setLastName(p.getLastName());
            existingPatient.setPostcode(p.getPostcode());
            existingPatient.setAddress(p.getAddress());
            existingPatient.setPhoneNumber(p.getPhoneNumber());
            existingPatient.setEmail(p.getEmail());
            existingPatient.setInsurance(p.getInsurance());
            return patientR.save(existingPatient);
        });
    }

    // Partial update of a Patient by ID
    public Patient updatePatientFields(@NotNull String id, @NotNull Map<String, Object> updates) {
        long startTime = logger.startOperation("Updating patient fields...", Map.of("id", id, "updates", updates.keySet()));
        return handleOperation("partial update patient", startTime, () -> {
            Patient patient = patientR.findById(id).orElseThrow(() -> new ExpPatient(id));
            updates.forEach((field, value) -> {
                switch (field) {
                    case "firstName" -> patient.setFirstName(value != null ? (String) value : null);
                    case "lastName" -> patient.setLastName(value != null ? (String) value : null);
                    case "postcode" -> patient.setPostcode(value != null ? (String) value : null);
                    case "address" -> patient.setAddress(value != null ? (String) value : null);
                    case "phoneNumber" -> patient.setPhoneNumber(value != null ? (String) value : null);
                    case "email" -> patient.setEmail(value != null ? (String) value : null);
                    default -> throw new IllegalArgumentException("Invalid field: " + field);
                }
            });
            return patientR.save(patient);
        });
    }

    // Deletes a Patient by ID
    public void deletePatient(String id) {
        long startTime = logger.startOperation("Deleting patient...", Map.of("id", id));
        handleOperation("delete patient", startTime, () -> {
            Patient patient = patientR.findById(id).orElseThrow(() -> new ExpPatient(id));
            patientR.delete(patient);
            return null;
        });
    }
}