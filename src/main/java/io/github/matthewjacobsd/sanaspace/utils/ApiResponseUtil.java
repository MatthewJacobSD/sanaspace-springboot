package io.github.matthewjacobsd.sanaspace.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

// 🚀 Standardizes API responses to match frontend IApiResponse
@Data
@AllArgsConstructor
public class ApiResponseUtil<T> {
    private boolean success;
    private Status status;
    private String message;
    private T data;
    private Map<String, Object> metadata;
    private String timestamp;

    // Inner Status class to match frontend IResponseStatus
    @Data
    @AllArgsConstructor
    public static class Status {
        private int code;
        private String message;
        private String type;
        private Map<String, Object> details; // Optional for errors
    }

    // Success response with data
    public static <T> ApiResponseUtil<T> success(T data) {
        return new ApiResponseUtil<>(
            true,
            new Status(200, "Operation successful", "success", null),
            "Operation successful",
            data,
            null,
            Instant.now().toString()
        );
    }

    // Success response with data and metadata
    public static <T> ApiResponseUtil<T> success(T data, Map<String, Object> metadata) {
        return new ApiResponseUtil<>(
            true,
            new Status(200, "Operation successful", "success", null),
            "Operation successful",
            data,
            metadata,
            Instant.now().toString()
        );
    }

    // Error response with message, code, and optional details
    public static <T> ApiResponseUtil<T> error(String message, int code, Map<String, Object> details) {
        return new ApiResponseUtil<>(
            false,
            new Status(code, message, "error", details),
            message,
            null,
            null,
            Instant.now().toString()
        );
    }
}