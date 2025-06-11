package io.github.matthewjacobsd.sanaspace.controllers;

import io.github.matthewjacobsd.sanaspace.models.Visit;
import io.github.matthewjacobsd.sanaspace.models.keys.VisitId;
import io.github.matthewjacobsd.sanaspace.services.SVisit;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for managing Visit entities.
 * Provides endpoints for CRUD operations including pagination and partial updates.
 */
@RestController
@RequestMapping("/api/visits")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CVisit {

    private final LoggerUtil logger = new LoggerUtil(CVisit.class);
    private final SVisit visitS;
    private final HttpServletRequest request;

    /**
     * Creates a new Visit.
     *
     * @param v The Visit object to be saved.
     * @return ResponseEntity with ApiResponse containing the saved Visit.
     */
    @PostMapping
    public ResponseEntity<ApiResponseUtil<Visit>> createVisit(@Valid @RequestBody Visit v) {
        long startTime = logger.startOperation("Saving visit...",
                Map.of("visit", v, "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Visit saved = visitS.saveVisit(v);
            logger.success("Visit saved successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(saved), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to save visit: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to create visit: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Retrieves a paginated list of Visits.
     *
     * @param page Page number (1-based index).
     * @param limit Number of results per page.
     * @return ResponseEntity with ApiResponse containing paginated Visit data.
     */
    @GetMapping
    public ResponseEntity<ApiResponseUtil<PaginationResponse<Visit>>> getPagedVisits(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        long startTime = logger.startOperation("Fetching visits...",
                Map.of("page", page, "limit", limit,
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, limit);
            Page<Visit> visitPage = visitS.getPagedVisits(pageRequest);
            PaginationResponse<Visit> response = new PaginationResponse<>(visitPage);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("page", page);
            metadata.put("limit", limit);
            logger.success("Visits fetched successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(response, metadata), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to fetch visits: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to fetch visits: " + e.getMessage(), 500, errorDetails),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Retrieves a specific Visit by its composite ID.
     *
     * @param patientId Unique ID of the patient.
     * @param doctorId Unique ID of the doctor.
     * @param visitDate Date of the visit.
     * @return ResponseEntity with ApiResponse containing the requested Visit.
     */
    @GetMapping("/{patientId}/{doctorId}/{visitDate}")
    public ResponseEntity<ApiResponseUtil<Visit>> fetchVisitById(
            @PathVariable("patientId") String patientId,
            @PathVariable("doctorId") String doctorId,
            @PathVariable("visitDate") String visitDate) {
        VisitId id = new VisitId(patientId, doctorId, LocalDate.parse(visitDate));
        long startTime = logger.startOperation("Fetching visit by ID...",
                Map.of("patientId", patientId, "doctorId", doctorId, "visitDate", visitDate,
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Visit visit = visitS.fetchVisitById(id);
            logger.success("Visit fetched successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(visit), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to fetch visit: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to fetch visit: " + e.getMessage(), 404, errorDetails),
                HttpStatus.NOT_FOUND
            );
        }
    }

    /**
     * Updates an existing Visit by its composite ID.
     *
     * @param patientId Unique ID of the patient.
     * @param doctorId Unique ID of the doctor.
     * @param visitDate Date of the visit.
     * @param v Updated Visit object.
     * @return ResponseEntity with ApiResponse containing the updated Visit.
     */
    @PutMapping("/{patientId}/{doctorId}/{visitDate}")
    public ResponseEntity<ApiResponseUtil<Visit>> updateVisit(
            @PathVariable("patientId") String patientId,
            @PathVariable("doctorId") String doctorId,
            @PathVariable("visitDate") String visitDate,
            @Valid @RequestBody Visit v) {
        VisitId id = new VisitId(patientId, doctorId, LocalDate.parse(visitDate));
        long startTime = logger.startOperation("Updating visit...",
                Map.of("patientId", patientId, "doctorId", doctorId, "visitDate", visitDate,
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Visit updated = visitS.updateVisit(id, v);
            logger.success("Visit updated successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(updated), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to update visit: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to update visit: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Partially updates a Visit by its composite ID.
     *
     * @param patientId Unique ID of the patient.
     * @param doctorId Unique ID of the doctor.
     * @param visitDate Date of the visit.
     * @param updates Map of fields to be updated.
     * @return ResponseEntity with ApiResponse containing the updated Visit.
     */
    @PatchMapping("/{patientId}/{doctorId}/{visitDate}")
    public ResponseEntity<ApiResponseUtil<Visit>> updateVisitFields(
            @PathVariable("patientId") String patientId,
            @PathVariable("doctorId") String doctorId,
            @PathVariable("visitDate") String visitDate,
            @NotNull @RequestBody Map<String, Object> updates) {
        VisitId id = new VisitId(patientId, doctorId, LocalDate.parse(visitDate));
        long startTime = logger.startOperation("Partially updating visit...",
                Map.of("patientId", patientId, "doctorId", doctorId, "visitDate", visitDate, "updates", updates.keySet(),
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Visit updated = visitS.updateVisitFields(id, updates);
            logger.success("Visit partially updated successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(updated), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to partially update visit: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to partially update visit: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Deletes a Visit by its composite ID.
     *
     * @param patientId Unique ID of the patient.
     * @param doctorId Unique ID of the doctor.
     * @param visitDate Date of the visit.
     * @return ResponseEntity with ApiResponse indicating successful deletion.
     */
    @DeleteMapping("/{patientId}/{doctorId}/{visitDate}")
    public ResponseEntity<ApiResponseUtil<Map<String, Object>>> deleteVisit(
            @PathVariable("patientId") String patientId,
            @PathVariable("doctorId") String doctorId,
            @PathVariable("visitDate") String visitDate) {
        VisitId id = new VisitId(patientId, doctorId, LocalDate.parse(visitDate));
        long startTime = logger.startOperation("Deleting visit...",
                Map.of("patientId", patientId, "doctorId", doctorId, "visitDate", visitDate,
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            visitS.deleteVisit(id);
            logger.success("Visit deleted successfully", startTime);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deleted", true);
            return new ResponseEntity<>(ApiResponseUtil.success(responseData), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to delete visit: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to delete visit: " + e.getMessage(), 404, errorDetails),
                HttpStatus.NOT_FOUND
            );
        }
    }
}