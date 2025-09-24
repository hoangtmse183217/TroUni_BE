package com.trouni.tro_uni.dto;

import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UserResponse - DTO cho response thông tin user
 * <p>
 * Chức năng chính:
 * - Trả về thông tin user an toàn (không có password)
 * - Sử dụng trong các API trả về thông tin user
 * - Có thể customize fields cần trả về
 * <p>
 * @author TroUni Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private UUID id;
    private String username;
    private String email;
    private UserRole role;
    private boolean phoneVerified;
    private String idVerificationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Tạo UserResponse từ User entity
     * <p>
     * @param user - User entity
     * @return UserResponse - Response DTO
     */
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .phoneVerified(user.isPhoneVerified())
                .idVerificationStatus(user.getIdVerificationStatus() != null ? 
                    user.getIdVerificationStatus().toString() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
