package io.github.matthewjacobsd.sanaspace.controllers;

import io.github.matthewjacobsd.sanaspace.models.Prescription;
import io.github.matthewjacobsd.sanaspace.services.SPrescription;
import io.github.matthewjacobsd.sanaspace.utils.ApiResponseUtil;
import io.github.matthewjacobsd.sanaspace.utils.LoggerUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CPrescription {

    private final LoggerUtil logger = new LoggerUtil(CPrescription.class);
    private final SPrescription prescriptionS;
    private final HttpServletRequest request;

    // Creates a new Prescription entity
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseUtil<Prescription> createPrescription(@Valid @RequestBody Prescription p) {
        long startTime = logger.startOperation("Saving prescription...",
                Map.of("dosageAmount", p.getDosageAmount(), "prescriptionDate", p.getPrescriptionDate(),
                       "method", request.getMethod(), "uri", request.getRequestURI()));
        Prescription saved = prescriptionS.savePrescription(p);
        logger.success("Prescription saved successfully", startTime);
        return ApiResponseUtil.success(saved);
    }

    // Retrieves all Prescriptions with pagination
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Page<Prescription>> fetchAllPrescriptions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        long startTime = logger.startOperation("Fetching all prescriptions...",
                Map.of("page", page, "size", size, "method", request.getMethod(), "uri", request.getRequestURI()));
        Page<Prescription> prescriptions = prescriptionS.fetchAllPrescriptions(page, size);
        logger.success("Prescriptions fetched successfully", startTime);
        return ApiResponseUtil.success(prescriptions, Map.of("page", page, "size", size));
    }

    // Retrieves a specific Prescription by ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Prescription> fetchPrescriptionById(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Fetching prescription by ID...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        Prescription prescription = prescriptionS.fetchPrescriptionById(id);
        logger.success("Prescription fetched successfully", startTime);
        return ApiResponseUtil.success(prescription);
    }

    // Updates a Prescription by ID
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Prescription> updatePrescription(@PathVariable("id") String id, @Valid @RequestBody Prescription p) {
        long startTime = logger.startOperation("Updating prescription...",
                Map.of("id", id, "dosageAmount", p.getDosageAmount(), "method", request.getMethod(), "uri", request.getRequestURI()));
        Prescription updated = prescriptionS.updatePrescription(id, p);
        logger.success("Prescription updated successfully", startTime);
        return ApiResponseUtil.success(updated);
    }

    // Partially updates a Prescription by ID
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Prescription> updatePrescriptionFields(@PathVariable("id") String id,
                                                                 @NotNull @RequestBody Map<String, Object> updates) {
        long startTime = logger.startOperation("Partially updating prescription...",
                Map.of("id", id, "updates", updates.keySet(), "method", request.getMethod(), "uri", request.getRequestURI()));
        Prescription updated = prescriptionS.updatePrescriptionFields(id, updates);
        logger.success("Prescription partially updated successfully", startTime);
        return ApiResponseUtil.success(updated);
    }

    // Deletes a Prescription by ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Map<String, Boolean>> deletePrescription(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Deleting prescription...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        prescriptionS.deletePrescription(id);
        logger.success("Prescription deleted successfully", startTime);
        return ApiResponseUtil.success(Map.of("deleted", true));
    }
}