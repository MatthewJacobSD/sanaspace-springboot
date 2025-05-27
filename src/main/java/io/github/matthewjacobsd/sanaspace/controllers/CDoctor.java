package io.github.matthewjacobsd.sanaspace.controllers;

import io.github.matthewjacobsd.sanaspace.models.Doctor;
import io.github.matthewjacobsd.sanaspace.services.SDoctor;
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
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CDoctor {

    private final LoggerUtil logger = new LoggerUtil(CDoctor.class);
    private final SDoctor doctorS;
    private final HttpServletRequest request;

    // Creates a new Doctor entity
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseUtil<Doctor> createDoctor(@Valid @RequestBody Doctor d) {
        long startTime = logger.startOperation("Saving doctor...", 
                Map.of("firstName", d.getFirstName(), "lastName", d.getLastName(), 
                       "method", request.getMethod(), "uri", request.getRequestURI()));
        Doctor saved = doctorS.saveDoctor(d);
        logger.success("Doctor saved successfully", startTime);
        return ApiResponseUtil.success(saved);
    }

    // Retrieves all doctors with pagination
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Page<Doctor>> fetchAllDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        long startTime = logger.startOperation("Fetching all doctors...", 
                Map.of("page", page, "size", size, 
                       "method", request.getMethod(), "uri", request.getRequestURI()));
        Page<Doctor> doctors = doctorS.fetchAllDoctors(page, size);
        logger.success("Doctors fetched successfully", startTime);
        return ApiResponseUtil.success(doctors, Map.of("page", page, "size", size));
    }

    // Retrieves a specific doctor by ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Doctor> fetchDoctorById(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Fetching doctor by ID...", 
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        Doctor doctor = doctorS.fetchDoctorById(id);
        logger.success("Doctor fetched successfully", startTime);
        return ApiResponseUtil.success(doctor);
    }
    // Updates a doctor by ID
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Doctor> updateDoctor(@PathVariable("id") String id, @Valid @RequestBody Doctor d) {
        long startTime = logger.startOperation("Updating doctor...", 
                Map.of("id", id, "firstName", d.getFirstName(), "lastName", d.getLastName(), 
                       "method", request.getMethod(), "uri", request.getRequestURI()));
        Doctor updated = doctorS.updateDoctor(id, d);
        logger.success("Doctor updated successfully", startTime);
        return ApiResponseUtil.success(updated);
    }

     // Partially updates a doctor by ID
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Doctor> updateDoctorFields(@PathVariable("id") String id,
                                                           @NotNull @RequestBody Map<String, Object> updates) {
        long startTime = logger.startOperation("Partially updating Doctor...",
                Map.of("id", id, "updates", updates.keySet(), "method", request.getMethod(), "uri", request.getRequestURI()));
        Doctor updated = doctorS.updateDoctorFields(id, updates);
        logger.success("Doctor partially updated successfully", startTime);
        return ApiResponseUtil.success(updated);
    }

    // Deletes a doctor by ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Map<String, Boolean>> deleteDoctor(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Deleting doctor...", 
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        doctorS.deleteDoctor(id);
        logger.success("Doctor deleted successfully", startTime);
        return ApiResponseUtil.success(Map.of("deleted", true));
    }
}