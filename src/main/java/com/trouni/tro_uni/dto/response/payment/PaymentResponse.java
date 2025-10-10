package com.trouni.tro_uni.dto.response.payment;

import com.trouni.tro_uni.entity.Payment;
import com.trouni.tro_uni.enums.PaymentMethod;
import com.trouni.tro_uni.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * PaymentResponse - Response chung cho Payment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {

    UUID id;

    UUID userId;

    UUID subscriptionId;

    BigDecimal amount;

    PaymentMethod paymentMethod;

    String transactionCode;

    PaymentStatus status;

    LocalDateTime createdAt;

    /**
     * Convert Payment entity to PaymentResponse
     */
    public static PaymentResponse fromPayment(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .userId(payment.getUser().getId())
                .subscriptionId(payment.getSubscription() != null ? payment.getSubscription().getId() : null)
                .amount(payment.getAmount())
                .paymentMethod(PaymentMethod.valueOf(payment.getPaymentMethod()))
                .transactionCode(payment.getTransactionCode())
                .status(PaymentStatus.valueOf(payment.getStatus()))
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
