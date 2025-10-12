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
public enum PackageErrorCode {
    PACKAGE_NOT_FOUND("PACKAGE_NOT_FOUND", "Package not found", HttpStatus.NOT_FOUND),
    PACKAGE_ALREADY_EXISTS("PACKAGE_ALREADY_EXISTS", "Package already exists", HttpStatus.CONFLICT),
    PACKAGE_INVALID("PACKAGE_INVALID", "Invalid package", HttpStatus.BAD_REQUEST),
    PACKAGE_DISABLED("PACKAGE_DISABLED", "Package is disabled", HttpStatus.FORBIDDEN),
    PACKAGE_EXPIRED("PACKAGE_EXPIRED", "Package has expired", HttpStatus.FORBIDDEN),
    PACKAGE_IN_USE("PACKAGE_IN_USE", "Cannot delete package that is currently in use", HttpStatus.CONFLICT),
    PACKAGE_LIMIT_EXCEEDED("PACKAGE_LIMIT_EXCEEDED", "Package limit exceeded", HttpStatus.FORBIDDEN),
    PACKAGE_CREATION_FAILED("PACKAGE_CREATION_FAILED", "Failed to create package", HttpStatus.INTERNAL_SERVER_ERROR),
    PACKAGE_UPDATE_FAILED("PACKAGE_UPDATE_FAILED", "Failed to update package", HttpStatus.INTERNAL_SERVER_ERROR),
    PACKAGE_DELETE_FAILED("PACKAGE_DELETE_FAILED", "Failed to delete package", HttpStatus.INTERNAL_SERVER_ERROR),
    PACKAGE_ACCESS_DENIED("PACKAGE_ACCESS_DENIED", "Access denied to package", HttpStatus.FORBIDDEN),
    PACKAGE_FEATURE_NOT_AVAILABLE("PACKAGE_FEATURE_NOT_AVAILABLE", "Feature not available in this package", HttpStatus.FORBIDDEN),
    PACKAGE_PRICE_INVALID("PACKAGE_PRICE_INVALID", "Invalid package price", HttpStatus.BAD_REQUEST),
    PACKAGE_DURATION_INVALID("PACKAGE_DURATION_INVALID", "Invalid package duration", HttpStatus.BAD_REQUEST);

    String code;
    String message;
    HttpStatusCode statusCode;
}
