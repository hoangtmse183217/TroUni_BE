package com.trouni.tro_uni.dto.request.payment;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * PaymentWebhookRequest - Request từ webhook của ngân hàng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentWebhookRequest {

    String transactionCode;

    BigDecimal amount;

    String status;

    String description;

    String bankCode;

//    String timestamp;
}
