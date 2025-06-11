package io.github.matthewjacobsd.sanaspace.controllers;

import io.github.matthewjacobsd.sanaspace.models.Patient;
import io.github.matthewjacobsd.sanaspace.services.SPatient;
import io.github.matthewjacobsd.sanaspace.utils.ApiResponseUtil;
import io.github.matthewjacobsd.sanaspace.utils.LoggerUtil;
import io.github.matthewjacobsd.sanaspace.utils.PaginationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for managing Patient entities.
 * Supports full CRUD operations including pagination and partial updates.
 */
@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CPatient {

    private final LoggerUtil logger = new LoggerUtil(CPatient.class);
    private final SPatient patientS;
    private final HttpServletRequest request;

    /**
     * Creates a new Patient.
     *
     * @param p The Patient object to be saved.
     * @return ResponseEntity with ApiResponse containing the saved Patient.
     */
    @PostMapping
    public ResponseEntity<ApiResponseUtil<Patient>> createPatient(@Valid @RequestBody Patient p) {
        long startTime = logger.startOperation("Saving patient...",
                Map.of("firstName", p.getFirstName(), "lastName", p.getLastName(),
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Patient saved = patientS.savePatient(p);
            logger.success("Patient saved successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(saved), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to save patient: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to create patient: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Retrieves a paginated list of Patients.
     *
     * @param page Page number (1-based index).
     * @param limit Number of results per page.
     * @return ResponseEntity with ApiResponse containing paginated Patient data.
     */
    @GetMapping
    public ResponseEntity<ApiResponseUtil<PaginationResponse<Patient>>> getPagedPatients(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        long startTime = logger.startOperation("Fetching patients...",
                Map.of("page", page, "limit", limit,
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, limit);
            Page<Patient> patientPage = patientS.getPagedPatients(pageRequest);
            PaginationResponse<Patient> response = new PaginationResponse<>(patientPage);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("page", page);
            metadata.put("limit", limit);
            logger.success("Patients fetched successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(response, metadata), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to fetch patients: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to fetch patients: " + e.getMessage(), 500, errorDetails),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Retrieves a specific Patient by ID.
     *
     * @param id Unique ID of the Patient.
     * @return ResponseEntity with ApiResponse containing the requested Patient.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Patient>> fetchPatientById(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Fetching patient by ID...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Patient patient = patientS.fetchPatientById(id);
            logger.success("Patient fetched successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(patient), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to fetch patient: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to fetch patient: " + e.getMessage(), 404, errorDetails),
                HttpStatus.NOT_FOUND
            );
        }
    }

    /**
     * Updates an existing Patient by ID.
     *
     * @param id Unique ID of the Patient.
     * @param p Updated Patient object.
     * @return ResponseEntity with ApiResponse containing the updated Patient.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Patient>> updatePatient(@PathVariable("id") String id, @Valid @RequestBody Patient p) {
        long startTime = logger.startOperation("Updating patient...",
                Map.of("id", id, "firstName", p.getFirstName(), "lastName", p.getLastName(),
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Patient updated = patientS.updatePatient(id, p);
            logger.success("Patient updated successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(updated), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to update patient: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to update patient: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Partially updates a Patient by ID.
     *
     * @param id Unique ID of the Patient.
     * @param updates Map of fields to be updated.
     * @return ResponseEntity with ApiResponse containing the updated Patient.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Patient>> updatePatientFields(@PathVariable("id") String id,
                                                                       @NotNull @RequestBody Map<String, Object> updates) {
        long startTime = logger.startOperation("Partially updating patient...",
                Map.of("id", id, "updates", updates.keySet(), "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Patient updated = patientS.updatePatientFields(id, updates);
            logger.success("Patient partially updated successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(updated), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to partially update patient: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to partially update patient: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Deletes a Patient by ID.
     *
     * @param id Unique ID of the Patient.
     * @return ResponseEntity with ApiResponse indicating successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Map<String, Object>>> deletePatient(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Deleting patient...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            patientS.deletePatient(id);
            logger.success("Patient deleted successfully", startTime);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deleted", true);
            return new ResponseEntity<>(ApiResponseUtil.success(responseData), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to delete patient: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to delete patient: " + e.getMessage(), 404, errorDetails),
                HttpStatus.NOT_FOUND
            );
        }
    }
}