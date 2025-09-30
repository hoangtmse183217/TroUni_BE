package com.trouni.tro_uni.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GoogleLoginRequest - DTO cho request đăng nhập bằng Google
 * <p>
 * Chức năng chính:
 * - Nhận access token từ Google OAuth
 * - Validate access token với Google API
 * - Tạo hoặc cập nhật user từ Google profile
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginRequest {
    
    /**
     * Google Access Token - Token từ Google OAuth flow
     * - Required: true
     * - Sử dụng để lấy thông tin user từ Google API
     */
    @NotBlank(message = "Access token is required")
    private String accessToken;
}

