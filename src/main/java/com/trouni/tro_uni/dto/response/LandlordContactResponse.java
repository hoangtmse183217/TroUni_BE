package com.trouni.tro_uni.dto.response;

import com.trouni.tro_uni.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * LandlordContactResponse - DTO cho thông tin liên hệ chủ trọ
 * Chỉ hiển thị thông tin cần thiết cho việc liên hệ
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandlordContactResponse {
    
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    
    public static LandlordContactResponse fromUser(User user) {
        return LandlordContactResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getProfile() != null ? user.getProfile().getFullName() : null)
                .phoneNumber(user.getProfile() != null ? user.getProfile().getPhoneNumber() : null)
                .avatarUrl(user.getProfile() != null ? user.getProfile().getAvatarUrl() : null)
                .build();
    }
}
