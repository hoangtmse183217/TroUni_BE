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
public enum NotificationErrorCode {
    NOTIFICATION_NOT_FOUND("NOTIFICATION_NOT_FOUND", "Notification not found", HttpStatus.NOT_FOUND),
    NOTIFICATION_SEND_FAILED("NOTIFICATION_SEND_FAILED", "Failed to send notification", HttpStatus.INTERNAL_SERVER_ERROR),
    NOTIFICATION_ACCESS_DENIED("NOTIFICATION_ACCESS_DENIED", "Access denied to notification", HttpStatus.FORBIDDEN),
    INVALID_NOTIFICATION_TYPE("INVALID_NOTIFICATION_TYPE", "Invalid notification type", HttpStatus.BAD_REQUEST),
    NOTIFICATION_ALREADY_READ("NOTIFICATION_ALREADY_READ", "Notification already read", HttpStatus.CONFLICT),
    INVALID_NOTIFICATION_CONTENT("INVALID_NOTIFICATION_CONTENT", "Invalid notification content", HttpStatus.BAD_REQUEST),
    NOTIFICATION_DELIVERY_FAILED("NOTIFICATION_DELIVERY_FAILED", "Notification delivery failed", HttpStatus.INTERNAL_SERVER_ERROR);

    String code;
    String message;
    HttpStatusCode statusCode;
}


