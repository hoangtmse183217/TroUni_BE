package com.trouni.tro_uni.dto.request.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * VietQRPaymentRequest - Request để tạo thanh toán VietQR
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VietQRPaymentRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    BigDecimal amount;

    String description;

    UUID subscriptionId; // Optional - nếu thanh toán cho subscription

    UUID packageId; // Optional - nếu thanh toán cho package
}
