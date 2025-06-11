package io.github.matthewjacobsd.sanaspace.controllers;

import io.github.matthewjacobsd.sanaspace.models.Insurance;
import io.github.matthewjacobsd.sanaspace.services.SInsurance;
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
 * REST controller for managing Insurance entities.
 * Provides full CRUD operations with pagination and partial updates.
 */
@RestController
@RequestMapping("/api/insurances")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CInsurance {

    private final LoggerUtil logger = new LoggerUtil(CInsurance.class);
    private final SInsurance insuranceS;
    private final HttpServletRequest request;

    /**
     * Creates a new Insurance.
     *
     * @param i The Insurance object to be saved.
     * @return ResponseEntity with ApiResponse containing the saved Insurance.
     */
    @PostMapping
    public ResponseEntity<ApiResponseUtil<Insurance>> createInsurance(@Valid @RequestBody Insurance i) {
        long startTime = logger.startOperation("Saving insurance...",
                Map.of("companyName", i.getCompanyName(), "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Insurance saved = insuranceS.saveInsurance(i);
            logger.success("Insurance saved successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(saved), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to save insurance: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to create insurance: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Retrieves a paginated list of Insurances.
     *
     * @param page Page number (1-based index).
     * @param limit Number of results per page.
     * @return ResponseEntity with ApiResponse containing paginated Insurance data.
     */
    @GetMapping
    public ResponseEntity<ApiResponseUtil<PaginationResponse<Insurance>>> getPagedInsurances(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        long startTime = logger.startOperation("Fetching insurances...",
                Map.of("page", page, "limit", limit,
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, limit);
            Page<Insurance> insurancePage = insuranceS.getPagedInsurances(pageRequest);
            PaginationResponse<Insurance> response = new PaginationResponse<>(insurancePage);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("page", page);
            metadata.put("limit", limit);
            logger.success("Insurances fetched successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(response, metadata), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to fetch insurances: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to fetch insurances: " + e.getMessage(), 500, errorDetails),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Retrieves a specific Insurance by ID.
     *
     * @param id Unique ID of the Insurance.
     * @return ResponseEntity with ApiResponse containing the requested Insurance.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Insurance>> fetchInsuranceById(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Fetching insurance by ID...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Insurance insurance = insuranceS.fetchInsuranceById(id);
            logger.success("Insurance fetched successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(insurance), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to fetch insurance: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to fetch insurance: " + e.getMessage(), 404, errorDetails),
                HttpStatus.NOT_FOUND
            );
        }
    }

    /**
     * Updates an existing Insurance by ID.
     *
     * @param id Unique ID of the Insurance.
     * @param i Updated Insurance object.
     * @return ResponseEntity with ApiResponse containing the updated Insurance.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Insurance>> updateInsurance(@PathVariable("id") String id, @Valid @RequestBody Insurance i) {
        long startTime = logger.startOperation("Updating insurance...",
                Map.of("id", id, "companyName", i.getCompanyName(),
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Insurance updated = insuranceS.updateInsurance(id, i);
            logger.success("Insurance updated successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(updated), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to update insurance: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to update insurance: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Partially updates an Insurance by ID.
     *
     * @param id Unique ID of the Insurance.
     * @param updates Map of fields to be updated.
     * @return ResponseEntity with ApiResponse containing the updated Insurance.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Insurance>> updateInsuranceFields(@PathVariable("id") String id,
                                                                           @NotNull @RequestBody Map<String, Object> updates) {
        long startTime = logger.startOperation("Partially updating insurance...",
                Map.of("id", id, "updates", updates.keySet(), "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Insurance updated = insuranceS.updateInsuranceFields(id, updates);
            logger.success("Insurance partially updated successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(updated), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to partially update insurance: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to partially update insurance: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Deletes an Insurance by ID.
     *
     * @param id Unique ID of the Insurance.
     * @return ResponseEntity with ApiResponse indicating successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Map<String, Object>>> deleteInsurance(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Deleting insurance...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            insuranceS.deleteInsurance(id);
            logger.success("Insurance deleted successfully", startTime);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deleted", true);
            return new ResponseEntity<>(ApiResponseUtil.success(responseData), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to delete insurance: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to delete insurance: " + e.getMessage(), 404, errorDetails),
                HttpStatus.NOT_FOUND
            );
        }
    }
}