package io.github.matthewjacobsd.sanaspace.controllers;

import io.github.matthewjacobsd.sanaspace.models.Insurance;
import io.github.matthewjacobsd.sanaspace.services.SInsurance;
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
@RequestMapping("/api/insurances")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CInsurance {

    private final LoggerUtil logger = new LoggerUtil(CInsurance.class);
    private final SInsurance insuranceS;
    private final HttpServletRequest request;

    // Creates a new Insurance entity
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseUtil<Insurance> createInsurance(@Valid @RequestBody Insurance i) {
        long startTime = logger.startOperation("Saving insurance...",
                Map.of("companyName", i.getCompanyName(), "method", request.getMethod(), "uri", request.getRequestURI()));
        Insurance saved = insuranceS.saveInsurance(i);
        logger.success("Insurance saved successfully", startTime);
        return ApiResponseUtil.success(saved);
    }

    // Retrieves all Insurances with pagination
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Page<Insurance>> fetchAllInsurances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        long startTime = logger.startOperation("Fetching all insurances...",
                Map.of("page", page, "size", size, "method", request.getMethod(), "uri", request.getRequestURI()));
        Page<Insurance> insurances = insuranceS.fetchAllInsurances(page, size);
        logger.success("Insurances fetched successfully", startTime);
        return ApiResponseUtil.success(insurances, Map.of("page", page, "size", size));
    }

    // Retrieves a specific Insurance by ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Insurance> fetchInsuranceById(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Fetching insurance by ID...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        Insurance insurance = insuranceS.fetchInsuranceById(id);
        logger.success("Insurance fetched successfully", startTime);
        return ApiResponseUtil.success(insurance);
    }

    // Updates an Insurance by ID
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Insurance> updateInsurance(@PathVariable("id") String id, @Valid @RequestBody Insurance i) {
        long startTime = logger.startOperation("Updating insurance...",
                Map.of("id", id, "companyName", i.getCompanyName(), "method", request.getMethod(), "uri", request.getRequestURI()));
        Insurance updated = insuranceS.updateInsurance(id, i);
        logger.success("Insurance updated successfully", startTime);
        return ApiResponseUtil.success(updated);
    }

    // Partially updates an Insurance by ID
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Insurance> updateInsuranceFields(@PathVariable("id") String id,
                                                           @NotNull @RequestBody Map<String, Object> updates) {
        long startTime = logger.startOperation("Partially updating insurance...",
                Map.of("id", id, "updates", updates.keySet(), "method", request.getMethod(), "uri", request.getRequestURI()));
        Insurance updated = insuranceS.updateInsuranceFields(id, updates);
        logger.success("Insurance partially updated successfully", startTime);
        return ApiResponseUtil.success(updated);
    }

    // Deletes an Insurance by ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponseUtil<Map<String, Boolean>> deleteInsurance(@PathVariable("id") String id) {
        long startTime = logger.startOperation("Deleting insurance...",
                Map.of("id", id, "method", request.getMethod(), "uri", request.getRequestURI()));
        insuranceS.deleteInsurance(id);
        logger.success("Insurance deleted successfully", startTime);
        return ApiResponseUtil.success(Map.of("deleted", true));
    }
}