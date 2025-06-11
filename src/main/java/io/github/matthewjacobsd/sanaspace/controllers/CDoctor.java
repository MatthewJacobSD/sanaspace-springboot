package io.github.matthewjacobsd.sanaspace.controllers;

import io.github.matthewjacobsd.sanaspace.models.Doctor;
import io.github.matthewjacobsd.sanaspace.services.SDoctor;
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
 * REST controller for managing Doctor entities.
 * Supports full CRUD operations with pagination and partial updates.
 */
@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CDoctor {

    private final LoggerUtil logger = new LoggerUtil(CDoctor.class);
    private final SDoctor doctorS;
    private final HttpServletRequest request;

    /**
     * Creates a new Doctor.
     *
     * @param d The Doctor object to be saved.
     * @return ResponseEntity with ApiResponse containing the saved Doctor.
     */
    @PostMapping
    public ResponseEntity<ApiResponseUtil<Doctor>> createDoctor(@Valid @RequestBody Doctor d) {
        long startTime = logger.startOperation("Saving doctor...",
                Map.of("firstName", d.getFirstName(), "lastName", d.getLastName(),
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Doctor saved = doctorS.saveDoctor(d);
            logger.success("Doctor saved successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(saved), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to save doctor: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to create doctor: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Retrieves a paginated list of Doctors.
     *
     * @param page Page number (1-based index).
     * @param limit Number of results per page.
     * @return ResponseEntity with ApiResponse containing paginated Doctor data.
     */
    @GetMapping
    public ResponseEntity<ApiResponseUtil<PaginationResponse<Doctor>>> getPagedDoctors(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        long startTime = logger.startOperation("Fetching doctors...",
                Map.of("page", page, "limit", limit,
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, limit);
            Page<Doctor> doctorPage = doctorS.getPagedDoctors(pageRequest);
            PaginationResponse<Doctor> response = new PaginationResponse<>(doctorPage);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("page", page);
            metadata.put("limit", limit);
            logger.success("Doctors fetched successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(response, metadata), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to fetch doctors: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to fetch doctors: " + e.getMessage(), 500, errorDetails),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Retrieves a specific Doctor by ID.
     *
     * @param id Unique ID of the Doctor.
     * @return ResponseEntity with ApiResponse containing the requested Doctor.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Doctor>> fetchDoctorById(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Fetching doctor by ID...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Doctor doctor = doctorS.fetchDoctorById(id);
            logger.success("Doctor fetched successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(doctor), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to fetch doctor: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to fetch doctor: " + e.getMessage(), 404, errorDetails),
                HttpStatus.NOT_FOUND
            );
        }
    }

    /**
     * Updates an existing Doctor by ID.
     *
     * @param id Unique ID of the Doctor.
     * @param d Updated Doctor object.
     * @return ResponseEntity with ApiResponse containing the updated Doctor.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Doctor>> updateDoctor(@PathVariable("id") String id, @Valid @RequestBody Doctor d) {
        long startTime = logger.startOperation("Updating doctor...",
                Map.of("id", id, "firstName", d.getFirstName(), "lastName", d.getLastName(),
                        "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Doctor updated = doctorS.updateDoctor(id, d);
            logger.success("Doctor updated successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(updated), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to update doctor: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to update doctor: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Partially updates a Doctor by ID.
     *
     * @param id Unique ID of the Doctor.
     * @param updates Map of fields to be updated.
     * @return ResponseEntity with ApiResponse containing the updated Doctor.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Doctor>> updateDoctorFields(@PathVariable("id") String id,
                                                                     @NotNull @RequestBody Map<String, Object> updates) {
        long startTime = logger.startOperation("Partially updating Doctor...",
                Map.of("id", id, "updates", updates.keySet(), "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            Doctor updated = doctorS.updateDoctorFields(id, updates);
            logger.success("Doctor partially updated successfully", startTime);
            return new ResponseEntity<>(ApiResponseUtil.success(updated), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to partially update doctor: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to partially update doctor: " + e.getMessage(), 400, errorDetails),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Deletes a Doctor by ID.
     *
     * @param id Unique ID of the Doctor.
     * @return ResponseEntity with ApiResponse indicating successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseUtil<Map<String, Object>>> deleteDoctor(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Deleting doctor...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        try {
            doctorS.deleteDoctor(id);
            logger.success("Doctor deleted successfully", startTime);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deleted", true);
            return new ResponseEntity<>(ApiResponseUtil.success(responseData), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to delete doctor: " + e.getMessage(), startTime, e, request.getRequestURI());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("exception", e.getClass().getSimpleName());
            errorDetails.put("details", e.getMessage());
            return new ResponseEntity<>(
                ApiResponseUtil.error("Failed to delete doctor: " + e.getMessage(), 404, errorDetails),
                HttpStatus.NOT_FOUND
            );
        }
    }
}