package io.github.matthewjacobsd.sanaspace.utils;

import io.github.matthewjacobsd.sanaspace.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }

    @ExceptionHandler(ExpDoctor.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseUtil<Object> handleDoctorNotFound(ExpDoctor e) {
        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", "DOCTOR_NOT_FOUND");
        return ApiResponseUtil.error(e.getMessage(), 404, details);
    }

    @ExceptionHandler(ExpMedication.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseUtil<Object> handleMedicationNotFound(ExpMedication e) {
        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", "MEDICATION_NOT_FOUND");
        return ApiResponseUtil.error(e.getMessage(), 404, details);
    }

    @ExceptionHandler(ExpInsurance.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseUtil<Object> handleInsuranceNotFound(ExpInsurance e) {
        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", "INSURANCE_NOT_FOUND");
        return ApiResponseUtil.error(e.getMessage(), 404, details);
    }

    @ExceptionHandler(ExpPatient.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseUtil<Object> handlePatientNotFound(ExpPatient e) {
        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", "PATIENT_NOT_FOUND");
        return ApiResponseUtil.error(e.getMessage(), 404, details);
    }

    @ExceptionHandler(ExpPrescription.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseUtil<Object> handlePrescriptionNotFound(ExpPrescription e) {
        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", "PRESCRIPTION_NOT_FOUND");
        return ApiResponseUtil.error(e.getMessage(), 404, details);
    }

    @ExceptionHandler(ExpVisit.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseUtil<Object> handleVisitNotFound(ExpVisit e) {
        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", "VISIT_NOT_FOUND");
        return ApiResponseUtil.error(e.getMessage(), 404, details);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseUtil<Object> handleIllegalArgument(IllegalArgumentException e) {
        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", "INVALID_INPUT");
        return ApiResponseUtil.error(e.getMessage(), 400, details);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponseUtil<Object> handleGeneralException(Exception e) {
        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", "SERVER_ERROR");
        return ApiResponseUtil.error("An unexpected error occurred: " + e.getMessage(), 500, details);
    }
}