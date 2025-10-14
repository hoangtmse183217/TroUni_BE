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
    PAYMENT_ALREADY_PROCESSED("PAYMENT_ALREADY_PROCESSED", "Payment has already been processed", HttpStatus.CONFLICT),
    PAYMENT_PROCESSING_FAILED("PAYMENT_PROCESSING_FAILED", "Payment processing failed", HttpStatus.PAYMENT_REQUIRED),
    INSUFFICIENT_FUNDS("INSUFFICIENT_FUNDS", "Insufficient funds for transaction", HttpStatus.PAYMENT_REQUIRED),
    INVALID_PAYMENT_METHOD("INVALID_PAYMENT_METHOD", "Invalid payment method", HttpStatus.BAD_REQUEST),
    PAYMENT_METHOD_NOT_SUPPORTED("PAYMENT_METHOD_NOT_SUPPORTED", "Payment method not supported", HttpStatus.BAD_REQUEST),
    PAYMENT_GATEWAY_ERROR("PAYMENT_GATEWAY_ERROR", "Payment gateway error", HttpStatus.BAD_GATEWAY),
    PAYMENT_TIMEOUT("PAYMENT_TIMEOUT", "Payment timeout", HttpStatus.REQUEST_TIMEOUT),
    PAYMENT_CANCELLED("PAYMENT_CANCELLED", "Payment cancelled", HttpStatus.BAD_REQUEST),
    REFUND_NOT_ALLOWED("REFUND_NOT_ALLOWED", "Refund not allowed for this payment", HttpStatus.FORBIDDEN),
    REFUND_FAILED("REFUND_FAILED", "Refund processing failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_AMOUNT_INVALID("PAYMENT_AMOUNT_INVALID", "Invalid payment amount", HttpStatus.BAD_REQUEST),
    CURRENCY_NOT_SUPPORTED("CURRENCY_NOT_SUPPORTED", "Currency not supported", HttpStatus.BAD_REQUEST),
    PAYMENT_DECLINED("PAYMENT_DECLINED", "Payment declined by provider", HttpStatus.PAYMENT_REQUIRED),
    TRANSACTION_ID_ALREADY_EXISTS("TRANSACTION_ID_ALREADY_EXISTS", "Transaction ID already exists", HttpStatus.CONFLICT),
    PAYMENT_VERIFICATION_FAILED("PAYMENT_VERIFICATION_FAILED", "Payment verification failed", HttpStatus.UNAUTHORIZED);

    String code;
    String message;
    HttpStatusCode statusCode;
}
