package com.trouni.tro_uni.dto.response.payment;

import com.trouni.tro_uni.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * VietQRPaymentResponse - Response sau khi tạo thanh toán VietQR
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VietQRPaymentResponse {

    UUID paymentId;

    String transactionCode;

    BigDecimal amount;

    PaymentStatus status;

    String qrCodeBase64; // QR code dạng base64 để hiển thị

    String qrCodeUrl; // URL của VietQR API

    String description;

    LocalDateTime createdAt;

    LocalDateTime expiresAt; // Thời gian hết hạn của QR code

    // Thông tin ngân hàng
    String bankAccountNumber;

    String bankAccountName;

    String bankName;
}
