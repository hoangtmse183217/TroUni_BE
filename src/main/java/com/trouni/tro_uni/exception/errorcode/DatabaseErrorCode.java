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
public enum DatabaseErrorCode {
    DATABASE_ERROR("DATABASE_ERROR", "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    CONSTRAINT_VIOLATION("CONSTRAINT_VIOLATION", "Database constraint violation", HttpStatus.BAD_REQUEST),
    CONNECTION_FAILED("CONNECTION_FAILED", "Database connection failed", HttpStatus.INTERNAL_SERVER_ERROR),
    TRANSACTION_FAILED("TRANSACTION_FAILED", "Database transaction failed", HttpStatus.INTERNAL_SERVER_ERROR),
    QUERY_TIMEOUT("QUERY_TIMEOUT", "Database query timeout", HttpStatus.REQUEST_TIMEOUT),
    DATA_INTEGRITY_VIOLATION("DATA_INTEGRITY_VIOLATION", "Data integrity violation", HttpStatus.BAD_REQUEST),
    DEADLOCK_DETECTED("DEADLOCK_DETECTED", "Database deadlock detected", HttpStatus.INTERNAL_SERVER_ERROR),
    FOREIGN_KEY_VIOLATION("FOREIGN_KEY_VIOLATION", "Foreign key constraint violation", HttpStatus.BAD_REQUEST);

    String code;
    String message;
    HttpStatusCode statusCode;
}


