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
    SUBSCRIPTION_ALREADY_EXISTS("SUBSCRIPTION_ALREADY_EXISTS", "Subscription already exists", HttpStatus.CONFLICT),
    SUBSCRIPTION_EXPIRED("SUBSCRIPTION_EXPIRED", "Subscription has expired", HttpStatus.FORBIDDEN),
    SUBSCRIPTION_NOT_ACTIVE("SUBSCRIPTION_NOT_ACTIVE", "Subscription is not active", HttpStatus.FORBIDDEN),
    SUBSCRIPTION_ALREADY_ACTIVE("SUBSCRIPTION_ALREADY_ACTIVE", "Subscription is already active", HttpStatus.CONFLICT),
    SUBSCRIPTION_ALREADY_CANCELLED("SUBSCRIPTION_ALREADY_CANCELLED", "Subscription is already cancelled", HttpStatus.CONFLICT),
    SUBSCRIPTION_PAYMENT_FAILED("SUBSCRIPTION_PAYMENT_FAILED", "Subscription payment failed", HttpStatus.PAYMENT_REQUIRED),
    SUBSCRIPTION_LIMIT_EXCEEDED("SUBSCRIPTION_LIMIT_EXCEEDED", "Subscription limit exceeded", HttpStatus.FORBIDDEN),
    SUBSCRIPTION_INVALID("SUBSCRIPTION_INVALID", "Invalid subscription", HttpStatus.BAD_REQUEST),
    SUBSCRIPTION_CANCEL_FAILED("SUBSCRIPTION_CANCEL_FAILED", "Failed to cancel subscription", HttpStatus.INTERNAL_SERVER_ERROR),
    SUBSCRIPTION_RENEWAL_FAILED("SUBSCRIPTION_RENEWAL_FAILED", "Failed to renew subscription", HttpStatus.INTERNAL_SERVER_ERROR),
    SUBSCRIPTION_UPGRADE_NOT_ALLOWED("SUBSCRIPTION_UPGRADE_NOT_ALLOWED", "Subscription upgrade not allowed", HttpStatus.FORBIDDEN),
    SUBSCRIPTION_DOWNGRADE_NOT_ALLOWED("SUBSCRIPTION_DOWNGRADE_NOT_ALLOWED", "Subscription downgrade not allowed", HttpStatus.FORBIDDEN),
    SUBSCRIPTION_FEATURE_NOT_AVAILABLE("SUBSCRIPTION_FEATURE_NOT_AVAILABLE", "Feature not available in current subscription", HttpStatus.FORBIDDEN);

    String code;
    String message;
    HttpStatusCode statusCode;
}
