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
public enum ReportErrorCode {
    REPORT_NOT_FOUND("REPORT_NOT_FOUND", "Report not found", HttpStatus.NOT_FOUND),
    INVALID_REPORT_TYPE("INVALID_REPORT_TYPE", "Invalid report type", HttpStatus.BAD_REQUEST),
    REPORT_ALREADY_EXISTS("REPORT_ALREADY_EXISTS", "Report already exists", HttpStatus.CONFLICT),
    CANNOT_REPORT_SELF("CANNOT_REPORT_SELF", "Cannot report yourself", HttpStatus.BAD_REQUEST),
    REPORT_ACCESS_DENIED("REPORT_ACCESS_DENIED", "Access denied to report", HttpStatus.FORBIDDEN),
    INVALID_REPORT_REASON("INVALID_REPORT_REASON", "Invalid report reason", HttpStatus.BAD_REQUEST),
    REPORT_ALREADY_PROCESSED("REPORT_ALREADY_PROCESSED", "Report already processed", HttpStatus.CONFLICT),
    INVALID_REPORT_STATUS("INVALID_REPORT_STATUS", "Invalid report status", HttpStatus.BAD_REQUEST);

    String code;
    String message;
    HttpStatusCode statusCode;
}


