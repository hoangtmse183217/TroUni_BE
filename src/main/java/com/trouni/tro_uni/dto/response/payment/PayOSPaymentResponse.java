package com.trouni.tro_uni.dto.response.payment;

import com.trouni.tro_uni.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PayOSPaymentResponse {
    private UUID paymentId;
    private String transactionCode;
    private Integer amount;
    private PaymentStatus status;
    private String checkoutUrl;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
