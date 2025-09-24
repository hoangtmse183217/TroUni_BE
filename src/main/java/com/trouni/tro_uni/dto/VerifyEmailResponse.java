package com.trouni.tro_uni.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * VerifyEmailResponse - DTO cho response verify email
 * <p>
 * Chức năng chính:
 * - Trả về kết quả verify email
 * - Chứa thông tin về trạng thái verification
 * - Sử dụng trong API verify email
 * <p>
 * @author TroUni Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailResponse {
    
    private String email;
    private boolean verified;
    private String message;
    private LocalDateTime verifiedAt;
    
    /**
     * Tạo response cho email đã verify thành công
     * <p>
     * @param email - Email đã verify
     * @param verifiedAt - Thời gian verify
     * @return VerifyEmailResponse - Response thành công
     */
    public static VerifyEmailResponse success(String email, LocalDateTime verifiedAt) {
        return VerifyEmailResponse.builder()
                .email(email)
                .verified(true)
                .message("Email verified successfully!")
                .verifiedAt(verifiedAt)
                .build();
    }
}
