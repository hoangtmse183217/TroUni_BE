package com.trouni.tro_uni.exception.errorcode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum GeneralErrorCode {
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNCATEGORIZED_EXCEPTION("UNCATEGORIZED_EXCEPTION", "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", "Service temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "Resource not found", HttpStatus.NOT_FOUND),
    RESOURCE_ALREADY_EXISTS("RESOURCE_ALREADY_EXISTS", "Resource already exists", HttpStatus.CONFLICT),
    INVALID_INPUT("INVALID_INPUT", "Invalid input provided", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED("ACCESS_DENIED", "Access denied", HttpStatus.FORBIDDEN),
    OPERATION_NOT_ALLOWED("OPERATION_NOT_ALLOWED", "Operation not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    RATE_LIMIT_EXCEEDED("RATE_LIMIT_EXCEEDED", "Rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS),
    MAINTENANCE_MODE("MAINTENANCE_MODE", "Service is under maintenance", HttpStatus.SERVICE_UNAVAILABLE),
    FEATURE_NOT_AVAILABLE("FEATURE_NOT_AVAILABLE", "Feature not available", HttpStatus.BAD_REQUEST),
    CONFIGURATION_ERROR("CONFIGURATION_ERROR", "Configuration error", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_SERVICE_ERROR("EXTERNAL_SERVICE_ERROR", "External service error", HttpStatus.BAD_GATEWAY);

    String code;
    String message;
    HttpStatusCode statusCode;
}


