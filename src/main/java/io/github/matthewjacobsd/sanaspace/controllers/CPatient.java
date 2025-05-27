package io.github.matthewjacobsd.sanaspace.controllers;

import io.github.matthewjacobsd.sanaspace.models.Patient;
import io.github.matthewjacobsd.sanaspace.services.SPatient;
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
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CPatient {

    private final LoggerUtil logger = new LoggerUtil(CPatient.class);
    private final SPatient patientS;
    private final HttpServletRequest request;

    // Creates a new Patient entity
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseUtil<Patient> createPatient(@Valid @RequestBody Patient p) {
        long startTime = logger.startOperation("Saving patient...",
                Map.of("firstName", p.getFirstName(), "lastName", p.getLastName(),
                       "method", request.getMethod(), "uri", request.getRequestURI()));
        Patient saved = patientS.savePatient(p);
        logger.success("Patient saved successfully", startTime);
        return ApiResponseUtil.success(saved);
    }

    // Retrieves all Patients with pagination
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Page<Patient>> fetchAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        long startTime = logger.startOperation("Fetching all patients...",
                Map.of("page", page, "size", size, "method", request.getMethod(), "uri", request.getRequestURI()));
        Page<Patient> patients = patientS.fetchAllPatients(page, size);
        logger.success("Patients fetched successfully", startTime);
        return ApiResponseUtil.success(patients, Map.of("page", page, "size", size));
    }

    // Retrieves a specific Patient by ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Patient> fetchPatientById(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Fetching patient by ID...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        Patient patient = patientS.fetchPatientById(id);
        logger.success("Patient fetched successfully", startTime);
        return ApiResponseUtil.success(patient);
    }

    // Updates a Patient by ID
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Patient> updatePatient(@PathVariable("id") String id, @Valid @RequestBody Patient p) {
        long startTime = logger.startOperation("Updating patient...",
                Map.of("id", id, "firstName", p.getFirstName(), "lastName", p.getLastName(),
                       "method", request.getMethod(), "uri", request.getRequestURI()));
        Patient updated = patientS.updatePatient(id, p);
        logger.success("Patient updated successfully", startTime);
        return ApiResponseUtil.success(updated);
    }

    // Partially updates a Patient by ID
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Patient> updatePatientFields(@PathVariable("id") String id,
                                                       @NotNull @RequestBody Map<String, Object> updates) {
        long startTime = logger.startOperation("Partially updating patient...",
                Map.of("id", id, "updates", updates.keySet(), "method", request.getMethod(), "uri", request.getRequestURI()));
        Patient updated = patientS.updatePatientFields(id, updates);
        logger.success("Patient partially updated successfully", startTime);
        return ApiResponseUtil.success(updated);
    }

    // Deletes a Patient by ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Map<String, Boolean>> deletePatient(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Deleting patient...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        patientS.deletePatient(id);
        logger.success("Patient deleted successfully", startTime);
        return ApiResponseUtil.success(Map.of("deleted", true));
    }
}