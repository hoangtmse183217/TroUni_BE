package com.trouni.tro_uni.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ResendCodeResponse - DTO cho response resend verification code
 * <p>
 * Chức năng chính:
 * - Trả về kết quả resend code
 * - Chứa thông tin về thời gian hết hạn
 * - Sử dụng trong API resend verification code
 * <p>
 * @author TroUni Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResendCodeResponse {
    
    private String email;
    private LocalDateTime expiresAt;
    private String message;
    
    /**
     * Tạo response cho resend code thành công
     * <p>
     * @param email - Email đã gửi code
     * @param expiresAt - Thời gian hết hạn
     * @return ResendCodeResponse - Response thành công
     */
    public static ResendCodeResponse success(String email, LocalDateTime expiresAt) {
        return ResendCodeResponse.builder()
                .email(email)
                .expiresAt(expiresAt)
                .message("New verification code sent!")
                .build();
    }
}
