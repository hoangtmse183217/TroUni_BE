package com.trouni.tro_uni.dto.response;

import com.trouni.tro_uni.entity.Profile;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.enums.AccountStatus;
import com.trouni.tro_uni.repository.RoomRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UserResponse - DTO for user information response.
 *
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
    private boolean googleAccount;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private ProfileResponse profile;


    private Integer totalRooms;

    /**
     * Creates a basic UserResponse from a User entity.
     *
     * @param user The User entity.
     * @return A UserResponse DTO.
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
                .googleAccount(user.isGoogleAccount())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .profile(user.getProfile() != null ? ProfileResponse.fromProfile(user.getProfile()) : null)
                .totalRooms(user.getRooms() != null ? user.getRooms().size() : 0)
                .build();
    }
}
