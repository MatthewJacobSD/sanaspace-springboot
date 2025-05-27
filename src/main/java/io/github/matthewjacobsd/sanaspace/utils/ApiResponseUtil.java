package io.github.matthewjacobsd.sanaspace.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

// 🚀 Standardizes API responses with extra feedback
@Data
@AllArgsConstructor
public class ApiResponseUtil<T> {
    private boolean success;
    private String message;
    private String errorCode;
    private T data;
    private Map<String, Object> metadata;
    private String timestamp;

    // Success response with data
    public static <T> ApiResponseUtil<T> success(T data) {
        return new ApiResponseUtil<>(true, "Operation successful", null, data, null, Instant.now().toString());
    }

    // Success response with data and metadata
    public static <T> ApiResponseUtil<T> success(T data, Map<String, Object> metadata) {
        return new ApiResponseUtil<>(true, "Operation successful", null, data, metadata, Instant.now().toString());
    }

    // Error response with message and code
    public static <T> ApiResponseUtil<T> error(String message, String errorCode) {
        return new ApiResponseUtil<>(false, message, errorCode, null, null, Instant.now().toString());
    }
}