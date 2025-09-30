package com.trouni.tro_uni.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TokenCheckResponse - DTO cho response kiểm tra token
 * <p>
 * Chức năng chính:
 * - Trả về kết quả kiểm tra token blacklist
 * - Sử dụng trong API check token status
 * - Chứa thông tin về trạng thái token
 * <p>
 * @author TroUni Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenCheckResponse {
    
    private boolean isBlacklisted;
    private String message;
    private String tokenStatus;
    
    /**
     * Tạo response cho token hợp lệ
     * <p>
     * @return TokenCheckResponse - Response cho token hợp lệ
     */
    public static TokenCheckResponse validToken() {
        return TokenCheckResponse.builder()
                .isBlacklisted(false)
                .message("Token is valid")
                .tokenStatus("VALID")
                .build();
    }
    
    /**
     * Tạo response cho token bị blacklist
     * <p>
     * @return TokenCheckResponse - Response cho token bị blacklist
     */
    public static TokenCheckResponse blacklistedToken() {
        return TokenCheckResponse.builder()
                .isBlacklisted(true)
                .message("Token is blacklisted")
                .tokenStatus("BLACKLISTED")
                .build();
    }
}
