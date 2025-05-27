package io.github.matthewjacobsd.sanaspace.controllers;

import io.github.matthewjacobsd.sanaspace.models.Visit;
import io.github.matthewjacobsd.sanaspace.models.keys.VisitId;
import io.github.matthewjacobsd.sanaspace.services.SVisit;
import io.github.matthewjacobsd.sanaspace.utils.ApiResponseUtil;
import io.github.matthewjacobsd.sanaspace.utils.LoggerUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/visits")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CVisit {

    private final LoggerUtil logger = new LoggerUtil(CVisit.class);
    private final SVisit visitS;
    private final HttpServletRequest request;

    // Creates a new Visit entity
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseUtil<Visit> createVisit(@Valid @RequestBody Visit v) {
        long startTime = logger.startOperation("Saving visit...",
                Map.of("visitDate", v.getVisitDate(), "method", request.getMethod(), "uri", request.getRequestURI()));
        Visit saved = visitS.saveVisit(v);
        logger.success("Visit saved successfully", startTime);
        return ApiResponseUtil.success(saved);
    }

    // Retrieves all Visits with pagination
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Page<Visit>> fetchAllVisits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        long startTime = logger.startOperation("Fetching all visits...",
                Map.of("page", page, "size", size, "method", request.getMethod(), "uri", request.getRequestURI()));
        Page<Visit> visits = visitS.fetchAllVisits(page, size);
        logger.success("Visits fetched successfully", startTime);
        return ApiResponseUtil.success(visits, Map.of("page", page, "size", size));
    }

    // Retrieves a specific Visit by composite ID
    @GetMapping("/{doctorId}/{visitDate}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Visit> fetchVisitById(
            @PathVariable("patientId") String patientId,
            @PathVariable("doctorId") String doctorId,
            @PathVariable("visitDate") String visitDate) {
        VisitId id = new VisitId(patientId, doctorId, LocalDate.parse(visitDate));
        long startTime = logger.startOperation("Fetching visit by ID...",
                Map.of("patientId", patientId, "doctorId", doctorId, "visitDate", visitDate, "method", request.getMethod(), "uri", request.getRequestURI()));
        Visit visit = visitS.fetchVisitById(id);
        logger.success("Visit fetched successfully", startTime);
        return ApiResponseUtil.success(visit);
    }

    // Updates a Visit by composite ID
    @PutMapping("/{doctorId}/{visitDate}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Visit> updateVisit(
            @PathVariable("patientId") String patientId,
            @PathVariable("doctorId") String doctorId,
            @PathVariable("visitDate") String visitDate,
            @Valid @RequestBody Visit v) {
        VisitId id = new VisitId(patientId, doctorId, LocalDate.parse(visitDate));
        long startTime = logger.startOperation("Updating visit...", 
                Map.of("patientId", patientId, "doctorId", doctorId, "visitDate", visitDate, "method", request.getMethod(), "uri", request.getRequestURI()));
        Visit updated = visitS.updateVisit(id, v);
        logger.success("Visit updated successfully", startTime);
        return ApiResponseUtil.success(updated);
    }

    // Partially updates a Visit by composite ID
    @PatchMapping("/{doctorId}/{visitDate}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Visit> updateVisitFields(
            @PathVariable("patientId") String patientId,
            @PathVariable("doctorId") String doctorId,
            @PathVariable("visitDate") String visitDate,
            @NotNull @RequestBody Map<String, Object> updates) {
        VisitId id = new VisitId(patientId, doctorId, LocalDate.parse(visitDate));
        long startTime = logger.startOperation("Partially updating visit...", 
                Map.of("patientId", patientId, "doctorId", doctorId, "visitDate", visitDate, "updates", updates.keySet(), "method", request.getMethod(), "uri", request.getRequestURI()));
        Visit updated = visitS.updateVisitFields(id, updates);
        logger.success("Visit partially updated successfully", startTime);
        return ApiResponseUtil.success(updated);
    }

    // Deletes a Visit by composite ID
    @DeleteMapping("/{doctorId}/{visitDate}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Map<String, Boolean>> deleteVisit(
            @PathVariable("patientId") String patientId,
            @PathVariable("doctorId") String doctorId,
            @PathVariable("visitDate") String visitDate) {
        VisitId id = new VisitId(patientId, doctorId, LocalDate.parse(visitDate));
        long startTime = logger.startOperation("Deleting visit...", 
                Map.of("patientId", patientId, "doctorId", doctorId, "visitDate", visitDate, "method", request.getMethod(), "uri", request.getRequestURI()));
        visitS.deleteVisit(id);
        logger.success("Visit deleted successfully", startTime);
        return ApiResponseUtil.success(Map.of("deleted", true));
    }
}