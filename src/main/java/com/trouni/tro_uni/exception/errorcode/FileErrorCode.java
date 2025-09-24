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
public enum FileErrorCode {
    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_TYPE("INVALID_FILE_TYPE", "Invalid file type", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE("FILE_TOO_LARGE", "File size too large", HttpStatus.BAD_REQUEST),
    FILE_NOT_FOUND("FILE_NOT_FOUND", "File not found", HttpStatus.NOT_FOUND),
    FILE_DELETE_FAILED("FILE_DELETE_FAILED", "File deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_ACCESS_DENIED("FILE_ACCESS_DENIED", "Access denied to file", HttpStatus.FORBIDDEN),
    INVALID_FILE_FORMAT("INVALID_FILE_FORMAT", "Invalid file format", HttpStatus.BAD_REQUEST),
    FILE_CORRUPTED("FILE_CORRUPTED", "File is corrupted", HttpStatus.BAD_REQUEST),
    STORAGE_QUOTA_EXCEEDED("STORAGE_QUOTA_EXCEEDED", "Storage quota exceeded", HttpStatus.BAD_REQUEST),
    FILE_PROCESSING_FAILED("FILE_PROCESSING_FAILED", "File processing failed", HttpStatus.INTERNAL_SERVER_ERROR);

    String code;
    String message;
    HttpStatusCode statusCode;
}


