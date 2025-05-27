package io.github.matthewjacobsd.sanaspace.utils;

import io.github.matthewjacobsd.sanaspace.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

// Catches all exceptions for consistent API responses
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final LoggerUtil logger = new LoggerUtil(GlobalExceptionHandler.class);

    // Handles Doctor not found errors
    @ExceptionHandler(ExpDoctor.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseUtil<Object> handleDoctorNotFound(ExpDoctor e) {
        long startTime = logger.startOperation("Handling Doctor not found", Map.of());
        logger.error("❌ Doctor error", startTime, e, "Unknown request");
        return ApiResponseUtil.error(e.getMessage(), "DOCTOR_NOT_FOUND");
    }

    // Handles Medication not found errors
    @ExceptionHandler(ExpMedication.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseUtil<Object> handleMedicationNotFound(ExpMedication e) {
        long startTime = logger.startOperation("Handling Medication not found", Map.of());
        logger.error("❌ Medication error", startTime, e, "Unknown request");
        return ApiResponseUtil.error(e.getMessage(), "MEDICATION_NOT_FOUND");
    }

    // Handles Insurance not found errors
    @ExceptionHandler(ExpInsurance.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseUtil<Object> handleInsuranceNotFound(ExpInsurance e) {
        long startTime = logger.startOperation("Handling Insurance not found", Map.of());
        logger.error("❌ Insurance error", startTime, e, "Unknown request");
        return ApiResponseUtil.error(e.getMessage(), "INSURANCE_NOT_FOUND");
    }

    // Handles Patient not found errors
    @ExceptionHandler(ExpPatient.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseUtil<Object> handlePatientNotFound(ExpPatient e) {
        long startTime = logger.startOperation("Handling Patient not found", Map.of());
        logger.error("❌ Patient error", startTime, e, "Unknown request");
        return ApiResponseUtil.error(e.getMessage(), "PATIENT_NOT_FOUND");
    }

    // Handles Prescription not found errors
    @ExceptionHandler(ExpPrescription.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseUtil<Object> handlePrescriptionNotFound(ExpPrescription e) {
        long startTime = logger.startOperation("Handling Prescription not found", Map.of());
        logger.error("❌ Prescription error", startTime, e, "Unknown request");
        return ApiResponseUtil.error(e.getMessage(), "PRESCRIPTION_NOT_FOUND");
    }

    // Handles Visit not found errors
    @ExceptionHandler(ExpVisit.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseUtil<Object> handleVisitNotFound(ExpVisit e) {
        long startTime = logger.startOperation("Handling Visit not found", Map.of());
        logger.error("❌ Visit error", startTime, e, "Unknown request");
        return ApiResponseUtil.error(e.getMessage(), "VISIT_NOT_FOUND");
    }

    // Handles invalid input errors
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseUtil<Object> handleIllegalArgument(IllegalArgumentException e) {
        long startTime = logger.startOperation("Handling invalid input", Map.of());
        logger.error("❌ Invalid input", startTime, e, "Unknown request");
        return ApiResponseUtil.error(e.getMessage(), "INVALID_INPUT");
    }

    // Catches all other exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponseUtil<Object> handleGeneralException(Exception e) {
        long startTime = logger.startOperation("Handling general error", Map.of());
        logger.error("❌ Unexpected error", startTime, e, "Unknown request");
        return ApiResponseUtil.error("An unexpected error occurred", "SERVER_ERROR");
    }

    // Functional interface for wrapping logic that might throw exceptions
    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }
}