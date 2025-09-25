package com.trouni.tro_uni.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ResendCodeRequest - DTO cho request gửi lại mã xác thực
 * <p>
 * Chức năng chính:
 * - Chứa email cần gửi lại mã
 * - Validation cho email format
 * - Sử dụng trong API resend verification code
 * <p>
 * @author TroUni Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResendCodeRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
}
