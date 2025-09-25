package com.trouni.tro_uni.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * VerificationStatusResponse - DTO cho response trạng thái verification
 * <p>
 * Chức năng chính:
 * - Trả về trạng thái verification của email
 * - Chứa thông tin về attempts và expiry
 * - Sử dụng trong API get verification status
 * <p>
 * @author TroUni Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationStatusResponse {
    
    private String email;
    private boolean verified;
    private int attempts;
    private int maxAttempts;
    private LocalDateTime expiresAt;
    private boolean canAttempt;
    private boolean isExpired;
    
    /**
     * Tạo response từ EmailVerification entity
     * <p>
     * @param email - Email
     * @param verified - Trạng thái verified
     * @param attempts - Số lần thử
     * @param maxAttempts - Số lần thử tối đa
     * @param expiresAt - Thời gian hết hạn
     * @param canAttempt - Có thể thử không
     * @param isExpired - Đã hết hạn chưa
     * @return VerificationStatusResponse - Response DTO
     */
    public static VerificationStatusResponse fromVerification(
            String email, boolean verified, int attempts, int maxAttempts,
            LocalDateTime expiresAt, boolean canAttempt, boolean isExpired) {
        return VerificationStatusResponse.builder()
                .email(email)
                .verified(verified)
                .attempts(attempts)
                .maxAttempts(maxAttempts)
                .expiresAt(expiresAt)
                .canAttempt(canAttempt)
                .isExpired(isExpired)
                .build();
    }
}
