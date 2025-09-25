package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.response.ProfileResponse;
import com.trouni.tro_uni.dto.request.UpdateProfileRequest;
import com.trouni.tro_uni.entity.Profile;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.AuthenticationErrorCode;
import com.trouni.tro_uni.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * ProfileService - Service xử lý logic liên quan đến Profile
 * <p>
 * Chức năng chính:
 * - Lấy thông tin profile của user
 * - Cập nhật thông tin profile
 * - Quản lý avatar và thông tin cá nhân
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {
    
    private final ProfileRepository profileRepository;
    
    /**
     * Lấy thông tin profile của user hiện tại
     * <p>
     * @param currentUser - User hiện tại đang đăng nhập
     * @return ProfileResponse - Thông tin profile
     * @throws AppException - Khi không tìm thấy profile
     */
    public ProfileResponse getCurrentUserProfile(User currentUser) {
        Profile profile = profileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new AppException(AuthenticationErrorCode.PROFILE_NOT_FOUND));
        
        log.info("Retrieved profile for user: {}", currentUser.getUsername());
        return ProfileResponse.fromProfile(profile);
    }
    
    /**
     * Lấy thông tin profile theo user ID
     * <p>
     * @param userId - ID của user
     * @return ProfileResponse - Thông tin profile
     * @throws AppException - Khi không tìm thấy profile
     */
    public ProfileResponse getProfileByUserId(UUID userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(AuthenticationErrorCode.PROFILE_NOT_FOUND));
        
        log.info("Retrieved profile for user ID: {}", userId);
        return ProfileResponse.fromProfile(profile);
    }
    
    /**
     * Cập nhật profile của user hiện tại
     * <p>
     * @param currentUser - User hiện tại đang đăng nhập
     * @param updateRequest - Thông tin cập nhật
     * @return ProfileResponse - Profile đã được cập nhật
     * @throws AppException - Khi không tìm thấy profile
     */
    public ProfileResponse updateCurrentUserProfile(User currentUser, UpdateProfileRequest updateRequest) {
        Profile profile = profileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new AppException(AuthenticationErrorCode.PROFILE_NOT_FOUND));
        
        // Cập nhật các trường có giá trị
        if (updateRequest.getFullName() != null && !updateRequest.getFullName().trim().isEmpty()) {
            profile.setFullName(updateRequest.getFullName().trim());
        }
        
        if (updateRequest.getGender() != null && !updateRequest.getGender().trim().isEmpty()) {
            profile.setGender(updateRequest.getGender().trim());
        }
        
        if (updateRequest.getPhoneNumber() != null && !updateRequest.getPhoneNumber().trim().isEmpty()) {
            profile.setPhoneNumber(updateRequest.getPhoneNumber().trim());
        }
        
        if (updateRequest.getAvatarUrl() != null && !updateRequest.getAvatarUrl().trim().isEmpty()) {
            profile.setAvatarUrl(updateRequest.getAvatarUrl().trim());
        }
        
        if (updateRequest.getBadge() != null && !updateRequest.getBadge().trim().isEmpty()) {
            profile.setBadge(updateRequest.getBadge().trim());
        }
        
        // Lưu profile đã cập nhật
        Profile savedProfile = profileRepository.save(profile);
        
        log.info("Updated profile for user: {}", currentUser.getUsername());
        return ProfileResponse.fromProfile(savedProfile);
    }
}

