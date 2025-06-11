package io.github.matthewjacobsd.sanaspace.services;

import io.github.matthewjacobsd.sanaspace.exceptions.ExpDoctor;
import io.github.matthewjacobsd.sanaspace.models.Doctor;
import io.github.matthewjacobsd.sanaspace.repositories.RDoctor;
import io.github.matthewjacobsd.sanaspace.utils.LoggerUtil;
import io.github.matthewjacobsd.sanaspace.utils.GlobalExceptionHandler.SupplierWithException;
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
public class SDoctor {
    
    private final LoggerUtil logger = new LoggerUtil(SDoctor.class);
    private final RDoctor doctorR;

    // Helper method to handle operations and exceptions
    private <T> T handleOperation(String operation, long startTime, SupplierWithException<T> supplier) {
        try {
            T result = supplier.get();
            logger.success("✅ " + operation, startTime);
            return result;
        } catch (ExpDoctor e) {
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

    // Saves a new Doctor entity
    public Doctor saveDoctor(@NotNull Doctor d) {
        long startTime = logger.startOperation("Saving doctor...", Map.of("firstName", d.getFirstName(), "lastName", d.getLastName()));
        return handleOperation("save doctor", startTime, () -> doctorR.save(d));
    }

    // Retrieves all Doctors with pagination
    public Page<Doctor> getPagedDoctors(Pageable pageable) {
        long startTime = logger.startOperation("Fetching doctors...", Map.of("page", pageable.getPageNumber(), "size", pageable.getPageSize()));
        return handleOperation("fetch all doctors", startTime, () -> doctorR.findAll(pageable));
    }

    // Retrieves all Doctors with pagination (legacy method)
    public Page<Doctor> fetchAllDoctors(int page, int size) {
        long startTime = logger.startOperation("Fetching doctors...", Map.of("page", page, "size", size));
        return handleOperation("fetch all doctors", startTime, () ->
                doctorR.findAll(PageRequest.of(page, size)));
    }

    // Retrieves a Doctor by ID
    public Doctor fetchDoctorById(String id) {
        long startTime = logger.startOperation("Fetching single doctor...", Map.of("id", id));
        return handleOperation("fetch doctor by ID", startTime, () ->
                doctorR.findById(id).orElseThrow(() -> new ExpDoctor(id)));
    }

    // Updates a Doctor by ID
    public Doctor updateDoctor(String id, Doctor d) {
        long startTime = logger.startOperation("Updating doctor...", Map.of("id", id, "firstName", d.getFirstName(), "lastName", d.getLastName()));
        return handleOperation("update doctor", startTime, () -> {
            Doctor existingDoctor = doctorR.findById(id).orElseThrow(() -> new ExpDoctor(id));
            existingDoctor.setFirstName(d.getFirstName());
            existingDoctor.setLastName(d.getLastName());
            existingDoctor.setAddress(d.getAddress());
            existingDoctor.setEmail(d.getEmail());
            if (d.getSpecialization() != null && d.getSpecialization() != Doctor.Specialization.General) {
                Doctor.Specialization.valueOf(d.getSpecialization().name());
                existingDoctor.setSpecialization(d.getSpecialization());
            }
            if (d.getExperience() != null && d.getExperience() != Doctor.Experience.Novice) {
                Doctor.Experience.valueOf(d.getExperience().name());
                existingDoctor.setExperience(d.getExperience());
            }
            return doctorR.save(existingDoctor);
        });
    }

    // Partial update of a Doctor by ID
    public Doctor updateDoctorFields(@NotNull String id, @NotNull Map<String, Object> updates) {
        long startTime = logger.startOperation("Updating doctor fields...", Map.of("id", id, "updates", updates.keySet()));
        return handleOperation("partial update doctor", startTime, () -> {
            Doctor doctor = doctorR.findById(id).orElseThrow(() -> new ExpDoctor(id));
            updates.forEach((field, value) -> {
                switch (field) {
                    case "firstName" -> doctor.setFirstName(value != null ? (String) value : null);
                    case "lastName" -> doctor.setLastName(value != null ? (String) value : null);
                    case "address" -> doctor.setAddress(value != null ? (String) value : null);
                    case "email" -> doctor.setEmail(value != null ? (String) value : null);
                    case "specialization" -> doctor.setSpecialization(value != null ?
                            Doctor.Specialization.valueOf((String) value) : null);
                    case "experience" -> doctor.setExperience(value != null ?
                            Doctor.Experience.valueOf((String) value) : null);
                    default -> throw new IllegalArgumentException("Invalid field: " + field);
                }
            });
            return doctorR.save(doctor);
        });
    }

    // Deletes a Doctor by ID
    public void deleteDoctor(String id) {
        long startTime = logger.startOperation("Deleting doctor...", Map.of("id", id));
        handleOperation("delete doctor", startTime, () -> {
            Doctor doctor = doctorR.findById(id).orElseThrow(() -> new ExpDoctor(id));
            doctorR.delete(doctor);
            return null; 
        });
    }
}