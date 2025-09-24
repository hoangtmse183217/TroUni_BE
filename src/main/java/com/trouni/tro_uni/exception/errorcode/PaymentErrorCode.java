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
public enum PaymentErrorCode {
    PAYMENT_NOT_FOUND("PAYMENT_NOT_FOUND", "Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_FAILED("PAYMENT_FAILED", "Payment processing failed", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_AMOUNT("INVALID_PAYMENT_AMOUNT", "Invalid payment amount", HttpStatus.BAD_REQUEST),
    PAYMENT_ALREADY_PROCESSED("PAYMENT_ALREADY_PROCESSED", "Payment already processed", HttpStatus.CONFLICT),
    INVALID_PAYMENT_METHOD("INVALID_PAYMENT_METHOD", "Invalid payment method", HttpStatus.BAD_REQUEST),
    PAYMENT_REFUND_FAILED("PAYMENT_REFUND_FAILED", "Payment refund failed", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_FUNDS("INSUFFICIENT_FUNDS", "Insufficient funds", HttpStatus.BAD_REQUEST),
    PAYMENT_TIMEOUT("PAYMENT_TIMEOUT", "Payment timeout", HttpStatus.REQUEST_TIMEOUT),
    INVALID_TRANSACTION_CODE("INVALID_TRANSACTION_CODE", "Invalid transaction code", HttpStatus.BAD_REQUEST),
    PAYMENT_CANCELLED("PAYMENT_CANCELLED", "Payment was cancelled", HttpStatus.BAD_REQUEST);

    String code;
    String message;
    HttpStatusCode statusCode;
}


