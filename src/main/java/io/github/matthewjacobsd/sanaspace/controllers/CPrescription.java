package io.github.matthewjacobsd.sanaspace.controllers;

import io.github.matthewjacobsd.sanaspace.models.Prescription;
import io.github.matthewjacobsd.sanaspace.services.SPrescription;
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
 * REST controller for managing Prescription entities.
 * Supports standard CRUD operations with pagination and field-level updates.
 */
@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CPrescription {

    private final LoggerUtil logger = new LoggerUtil(CPrescription.class);
    private final SPrescription prescriptionS;
    private final HttpServletRequest request;

    /**
     * Creates a new Prescription.
     *
     * @param p The Prescription object to be saved.
     * @return ResponseEntity with ApiResponse containing the saved Prescription.
     */
    @PostMapping
    public ResponseEntity<ApiResponseUtil<Prescription>> createPrescription(@Valid @RequestBody Prescription p) {
        long startTime = logger.startOperation("Saving prescription...",
                Map.of("dosage", p.getDosage(), "prescriptionDate", p.getPrescriptionDate(),
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Prescription saved = prescriptionS.savePrescription(p);
            logger.success("Prescription saved successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(saved), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to save prescription: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to create prescription: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Retrieves a paginated list of Prescriptions.
     *
     * @param page Page number (1-based index).
     * @param limit Number of results per page.
     * @return ResponseEntity with ApiResponse containing paginated Prescription data.
     */
    @GetMapping
    public ResponseEntity<ApiResponseUtil<PaginationResponse<Prescription>>> getPagedPrescriptions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        long startTime = logger.startOperation("Fetching prescriptions...",
                Map.of("page", page, "limit", limit,
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, limit);
            Page<Prescription> prescriptionPage = prescriptionS.getPagedPrescriptions(pageRequest);
            PaginationResponse<Prescription> response = new PaginationResponse<>(prescriptionPage);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("page", page);
            metadata.put("limit", limit);
            logger.success("Prescriptions fetched successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(response, metadata), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to fetch prescriptions: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to fetch prescriptions: " + e.getMessage(), 500, errorDetails),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Retrieves a specific Prescription by ID.
     *
     * @param id Unique ID of the Prescription.
     * @return ResponseEntity with ApiResponse containing the requested Prescription.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Prescription>> fetchPrescriptionById(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Fetching prescription by ID...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Prescription prescription = prescriptionS.fetchPrescriptionById(id);
            logger.success("Prescription fetched successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(prescription), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to fetch prescription: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to fetch prescription: " + e.getMessage(), 404, errorDetails),
                HttpStatus.NOT_FOUND
            );
        }
    }

    /**
     * Updates an existing Prescription by ID.
     *
     * @param id Unique ID of the Prescription.
     * @param p Updated Prescription object.
     * @return ResponseEntity with ApiResponse containing the updated Prescription.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Prescription>> updatePrescription(@PathVariable("id") String id, @Valid @RequestBody Prescription p) {
        long startTime = logger.startOperation("Updating prescription...",
                Map.of("id", id, "dosage", p.getDosage(),
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Prescription updated = prescriptionS.updatePrescription(id, p);
            logger.success("Prescription updated successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(updated), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to update prescription: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to update prescription: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Partially updates a Prescription by ID.
     *
     * @param id Unique ID of the Prescription.
     * @param updates Map of fields to be updated.
     * @return ResponseEntity with ApiResponse containing the updated Prescription.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Prescription>> updatePrescriptionFields(@PathVariable("id") String id,
                                                                                 @NotNull @RequestBody Map<String, Object> updates) {
        long startTime = logger.startOperation("Partially updating prescription...",
                Map.of("id", id, "updates", updates.keySet(), "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Prescription updated = prescriptionS.updatePrescriptionFields(id, updates);
            logger.success("Prescription partially updated successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(updated), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to partially update prescription: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to partially update prescription: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Deletes a Prescription by ID.
     *
     * @param id Unique ID of the Prescription.
     * @return ResponseEntity with ApiResponse indicating successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Map<String, Object>>> deletePrescription(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Deleting prescription...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            prescriptionS.deletePrescription(id);
            logger.success("Prescription deleted successfully", startTime);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deleted", true);
            return new ResponseEntity<>(ApiResponseUtil.success(responseData), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to delete prescription: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to delete prescription: " + e.getMessage(), 404, errorDetails),
                HttpStatus.NOT_FOUND
            );
        }
    }
}