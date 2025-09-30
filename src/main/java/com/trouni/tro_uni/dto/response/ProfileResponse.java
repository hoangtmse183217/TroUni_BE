package com.trouni.tro_uni.dto.response;

import com.trouni.tro_uni.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ProfileResponse - DTO cho response thông tin profile
 * <p>
 * Chức năng chính:
 * - Trả về thông tin profile chi tiết của user
 * - Sử dụng trong API lấy profile
 * - Bao gồm tất cả thông tin từ Profile entity
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    
    private UUID id;
    private UUID userId;
    private String fullName;
    private String gender;
    private String phoneNumber;
    private String avatarUrl;
    private String badge;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Tạo ProfileResponse từ Profile entity
     * <p>
     * @param profile - Profile entity
     * @return ProfileResponse - Response DTO
     */
    public static ProfileResponse fromProfile(Profile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .fullName(profile.getFullName())
                .gender(profile.getGender())
                .phoneNumber(profile.getPhoneNumber())
                .avatarUrl(profile.getAvatarUrl())
                .badge(profile.getBadge())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}

