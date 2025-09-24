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
public enum SubscriptionErrorCode {
    SUBSCRIPTION_NOT_FOUND("SUBSCRIPTION_NOT_FOUND", "Subscription not found", HttpStatus.NOT_FOUND),
    SUBSCRIPTION_EXPIRED("SUBSCRIPTION_EXPIRED", "Subscription has expired", HttpStatus.BAD_REQUEST),
    SUBSCRIPTION_ALREADY_ACTIVE("SUBSCRIPTION_ALREADY_ACTIVE", "Subscription is already active", HttpStatus.CONFLICT),
    PACKAGE_NOT_FOUND("PACKAGE_NOT_FOUND", "Package not found", HttpStatus.NOT_FOUND),
    SUBSCRIPTION_LIMIT_EXCEEDED("SUBSCRIPTION_LIMIT_EXCEEDED", "Subscription limit exceeded", HttpStatus.PAYMENT_REQUIRED),
    INVALID_SUBSCRIPTION_STATUS("INVALID_SUBSCRIPTION_STATUS", "Invalid subscription status", HttpStatus.BAD_REQUEST),
    SUBSCRIPTION_UPGRADE_FAILED("SUBSCRIPTION_UPGRADE_FAILED", "Subscription upgrade failed", HttpStatus.BAD_REQUEST),
    SUBSCRIPTION_DOWNGRADE_FAILED("SUBSCRIPTION_DOWNGRADE_FAILED", "Subscription downgrade failed", HttpStatus.BAD_REQUEST),
    SUBSCRIPTION_CANCELLATION_FAILED("SUBSCRIPTION_CANCELLATION_FAILED", "Subscription cancellation failed", HttpStatus.BAD_REQUEST);

    String code;
    String message;
    HttpStatusCode statusCode;
}


