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
public enum ValidationErrorCode {
    INVALID_MESSAGE_KEY("INVALID_MESSAGE_KEY", "Invalid message key", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR("VALIDATION_ERROR", "Validation failed", HttpStatus.BAD_REQUEST),
    INVALID_INPUT("INVALID_INPUT", "Invalid input data", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD("MISSING_REQUIRED_FIELD", "Required field is missing", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("INVALID_EMAIL_FORMAT", "Invalid email format", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_FORMAT("INVALID_PHONE_FORMAT", "Invalid phone number format", HttpStatus.BAD_REQUEST),
    INVALID_DATE_FORMAT("INVALID_DATE_FORMAT", "Invalid date format", HttpStatus.BAD_REQUEST),
    INVALID_NUMBER_FORMAT("INVALID_NUMBER_FORMAT", "Invalid number format", HttpStatus.BAD_REQUEST),
    FIELD_TOO_LONG("FIELD_TOO_LONG", "Field value exceeds maximum length", HttpStatus.BAD_REQUEST),
    FIELD_TOO_SHORT("FIELD_TOO_SHORT", "Field value is below minimum length", HttpStatus.BAD_REQUEST),
    INVALID_ENUM_VALUE("INVALID_ENUM_VALUE", "Invalid enum value", HttpStatus.BAD_REQUEST),
    INVALID_JSON_FORMAT("INVALID_JSON_FORMAT", "Invalid JSON format", HttpStatus.BAD_REQUEST);

    String code;
    String message;
    HttpStatusCode statusCode;
}

