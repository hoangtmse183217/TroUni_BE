package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.response.ProfileResponse;
import com.trouni.tro_uni.dto.request.UpdateProfileRequest;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.service.AuthService;
import com.trouni.tro_uni.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.UUID;

/**
 * ProfileController - Controller xử lý các API liên quan đến Profile
 * <p>
 * Chức năng chính:
 * - Lấy thông tin profile của user
 * - Cập nhật thông tin profile
 * - Quản lý avatar và thông tin cá nhân
 * 
 * @author TroUni Team
 * @version 1.0
 */
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    
    private final ProfileService profileService;
    private final AuthService authService;
    
    /**
     * API lấy thông tin profile của user hiện tại

     * Endpoint: GET /api/profile/me

     * @return ResponseEntity - Response chứa thông tin profile
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getCurrentUserProfile() {
        User currentUser = authService.getCurrentUser();
        ProfileResponse profile = profileService.getCurrentUserProfile(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully!", profile));
    }
    
    /**
     * API lấy thông tin profile theo user ID

     * Endpoint: GET /api/profile/{userId}

     * @param userId - UUID của user
     * @return ResponseEntity - Response chứa thông tin profile
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfileByUserId(@PathVariable UUID userId) {
        try {
            ProfileResponse profile = profileService.getProfileByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully!", profile));
        } catch (AppException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROFILE_RETRIEVAL_FAILED", "Failed to retrieve profile!"));
        }
    }
    
    /**
     * API cập nhật thông tin profile của user hiện tại

     * Endpoint: PUT /api/profile/me

     * @param updateRequest - Thông tin cập nhật profile
     * @return ResponseEntity - Response chứa profile đã cập nhật
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateCurrentUserProfile(@Valid @RequestBody UpdateProfileRequest updateRequest) {
        try {
            User currentUser = authService.getCurrentUser();
            ProfileResponse updatedProfile = profileService.updateCurrentUserProfile(currentUser, updateRequest);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully!", updatedProfile));
        } catch (AppException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("PROFILE_UPDATE_FAILED", "Failed to update profile!"));
        }
    }
}

