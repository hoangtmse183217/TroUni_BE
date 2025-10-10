package com.trouni.tro_uni.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * VietQRService - Service xử lý tạo VietQR code
 *
 * Chức năng:
 * - Tạo QR code theo chuẩn VietQR
 * - Generate QR code dạng base64
 * - Generate URL VietQR API
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VietQRService {

    @Value("${vietqr.bank.bin:970422}")
    String bankBin; // Mã BIN ngân hàng (VD: 970422 - MB Bank)

    @Value("${vietqr.bank.account:0123456789}")
    String bankAccount; // Số tài khoản ngân hàng

    @Value("${vietqr.bank.name:NGUYEN VAN A}")
    String bankAccountName; // Tên chủ tài khoản

    @Value("${vietqr.bank.display-name:MB Bank}")
    String bankDisplayName; // Tên ngân hàng hiển thị

    @Value("${vietqr.template:compact2}")
    String template; // Template VietQR (compact, compact2, qr_only, print)

    /**
     * Tạo VietQR code dạng base64
     *
     * @param amount - Số tiền
     * @param description - Nội dung chuyển khoản
     * @param transactionCode - Mã giao dịch
     * @return String - QR code dạng base64
     */
    public String generateVietQRBase64(BigDecimal amount, String description, String transactionCode) {
        try {
            // Tạo URL VietQR
            String qrContent = buildVietQRContent(amount, description, transactionCode);

            // Tạo QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 400, 400, hints);

            // Convert to PNG
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] qrCodeBytes = outputStream.toByteArray();

            // Convert to Base64
            String base64QR = Base64.getEncoder().encodeToString(qrCodeBytes);

            log.info("Generated VietQR code for transaction: {}", transactionCode);
            return base64QR;

        } catch (WriterException | IOException e) {
            log.error("Error generating VietQR code: {}", e.getMessage());
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    /**
     * Tạo URL VietQR API
     *
     * @param amount - Số tiền
     * @param description - Nội dung chuyển khoản
     * @param transactionCode - Mã giao dịch
     * @return String - URL VietQR
     */
    public String generateVietQRUrl(BigDecimal amount, String description, String transactionCode) {
        String addInfo = description + " - Ma GD: " + transactionCode;

        // Format: https://img.vietqr.io/image/{BANK_BIN}-{ACCOUNT_NO}-{TEMPLATE}.png?amount={AMOUNT}&addInfo={INFO}&accountName={ACC_NAME}
        return String.format(
            "https://img.vietqr.io/image/%s-%s-%s.png?amount=%s&addInfo=%s&accountName=%s",
            bankBin,
            bankAccount,
            template,
            amount.intValue(),
            encodeUrl(addInfo),
            encodeUrl(bankAccountName)
        );
    }

    /**
     * Build nội dung QR code theo chuẩn VietQR
     */
    private String buildVietQRContent(BigDecimal amount, String description, String transactionCode) {
        // Sử dụng URL VietQR API để tạo QR code
        return generateVietQRUrl(amount, description, transactionCode);
    }

    /**
     * Encode URL parameter
     */
    private String encodeUrl(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * Get bank account info
     */
    public Map<String, String> getBankInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("bankName", bankDisplayName);
        info.put("accountNumber", bankAccount);
        info.put("accountName", bankAccountName);
        info.put("bankBin", bankBin);
        return info;
    }
}
