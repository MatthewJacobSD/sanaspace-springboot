package io.github.matthewjacobsd.sanaspace.controllers;

import io.github.matthewjacobsd.sanaspace.models.Medication;
import io.github.matthewjacobsd.sanaspace.services.SMedication;
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
@RequestMapping("/api/medications")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CMedication {

    private final LoggerUtil logger = new LoggerUtil(CMedication.class);
    private final SMedication medicationS;
    private final HttpServletRequest request;

    // Creates a new Medication entity
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseUtil<Medication> createMedication(@Valid @RequestBody Medication m) {
        long startTime = logger.startOperation("Saving medication...",
                Map.of("name", m.getName(), "method", request.getMethod(), "uri", request.getRequestURI()));
        Medication saved = medicationS.saveMedication(m);
        logger.success("Medication saved successfully", startTime);
        return ApiResponseUtil.success(saved);
    }

    // Retrieves all Medications with pagination
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Page<Medication>> fetchAllMedications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        long startTime = logger.startOperation("Fetching all medications...",
                Map.of("page", page, "size", size, "method", request.getMethod(), "uri", request.getRequestURI()));
        Page<Medication> medications = medicationS.fetchAllMedications(page, size);
        logger.success("Medications fetched successfully", startTime);
        return ApiResponseUtil.success(medications, Map.of("page", page, "size", size));
    }

    // Retrieves a specific Medication by ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Medication> fetchMedicationById(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Fetching medication by ID...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        Medication medication = medicationS.fetchMedicationById(id);
        logger.success("Medication fetched successfully", startTime);
        return ApiResponseUtil.success(medication);
    }

    // Updates a Medication by ID
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Medication> updateMedication(@PathVariable("id") String id, @Valid @RequestBody Medication m) {
        long startTime = logger.startOperation("Updating medication...",
                Map.of("id", id, "name", m.getName(), "method", request.getMethod(), "uri", request.getRequestURI()));
        Medication updated = medicationS.updateMedication(id, m);
        logger.success("Medication updated successfully", startTime);
        return ApiResponseUtil.success(updated);
    }

    // Partially updates a Medication by ID
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Medication> updateMedicationFields(@PathVariable("id") String id,
                                                             @NotNull @RequestBody Map<String, Object> updates) {
        long startTime = logger.startOperation("Partially updating medication...",
                Map.of("id", id, "updates", updates.keySet(), "method", request.getMethod(), "uri", request.getRequestURI()));
        Medication updated = medicationS.updateMedicationFields(id, updates);
        logger.success("Medication partially updated successfully", startTime);
        return ApiResponseUtil.success(updated);
    }

    // Deletes a Medication by ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Map<String, Boolean>> deleteMedication(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Deleting medication...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        medicationS.deleteMedication(id);
        logger.success("Medication deleted successfully", startTime);
        return ApiResponseUtil.success(Map.of("deleted", true));
    }
}