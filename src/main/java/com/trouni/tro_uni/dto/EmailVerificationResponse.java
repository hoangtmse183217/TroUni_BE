package com.trouni.tro_uni.dto;

import com.trouni.tro_uni.entity.EmailVerification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * EmailVerificationResponse - DTO cho response thông tin email verification
 * <p>
 * Chức năng chính:
 * - Trả về thông tin email verification an toàn
 * - Sử dụng trong các API email verification
 * - Không trả về sensitive data như password hash
 * <p>
 * @author TroUni Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationResponse {
    
    private UUID id;
    private String email;
    private String username;
    private String verificationCode;
    private boolean isVerified;
    private int attempts;
    private int maxAttempts;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime verifiedAt;
    private boolean isExpired;
    private boolean canAttempt;
    
    /**
     * Tạo EmailVerificationResponse từ EmailVerification entity
     * <p>
     * @param emailVerification - EmailVerification entity
     * @return EmailVerificationResponse - Response DTO
     */
    public static EmailVerificationResponse fromEmailVerification(EmailVerification emailVerification) {
        return EmailVerificationResponse.builder()
                .id(emailVerification.getId())
                .email(emailVerification.getEmail())
                .username(emailVerification.getUsername())
                .verificationCode(emailVerification.getVerificationCode())
                .isVerified(emailVerification.isVerified())
                .attempts(emailVerification.getAttempts())
                .maxAttempts(emailVerification.getMaxAttempts())
                .expiresAt(emailVerification.getExpiresAt())
                .createdAt(emailVerification.getCreatedAt())
                .verifiedAt(emailVerification.getVerifiedAt())
                .isExpired(emailVerification.isExpired())
                .canAttempt(emailVerification.canAttempt())
                .build();
    }
}
