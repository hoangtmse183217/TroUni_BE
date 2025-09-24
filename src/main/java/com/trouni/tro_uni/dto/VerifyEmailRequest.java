package com.trouni.tro_uni.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * VerifyEmailRequest - DTO cho request verify email
 * <p>
 * Chức năng chính:
 * - Chứa email và mã xác thực 6 số
 * - Validation cho email format và verification code
 * - Sử dụng trong API verify email
 * <p>
 * @author TroUni Team
 * @version 1.0
 */
@Data
public class VerifyEmailRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "\\d{6}", message = "Verification code must be 6 digits")
    private String verificationCode;
}
