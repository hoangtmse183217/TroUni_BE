package com.trouni.tro_uni.dto.response;

import com.trouni.tro_uni.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * AuthorResponse - DTO cho thông tin tác giả bài đăng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorResponse {
    
    private UUID id;
    private String username;
    private String fullName;
    private String avatarUrl;
    
    public static AuthorResponse fromUser(User user) {
        return AuthorResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getProfile() != null ? user.getProfile().getFullName() : null)
                .avatarUrl(user.getProfile() != null ? user.getProfile().getAvatarUrl() : null)
                .build();
    }
}
