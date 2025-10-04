package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.response.BlacklistStatsResponse;
import com.trouni.tro_uni.dto.response.TokenCheckResponse;
import com.trouni.tro_uni.dto.response.UserResponse;
import com.trouni.tro_uni.dto.request.UpdateUserRequest;
import com.trouni.tro_uni.dto.request.AdminUpdateUserRequest;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.repository.UserRepository;
import com.trouni.tro_uni.service.AuthService;
import com.trouni.tro_uni.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.UUID;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UserController - Controller cho các API debug và testing
 * <p>
 * Chức năng chính:
 * - Lấy danh sách tất cả users
 * - Lấy thông tin user theo username
 * - Tạo test user
 * - Test password matching
 * <p>
 * Lưu ý: Sử dụng Map thay vì trả về Entity trực tiếp để tránh circular reference
 * 
 * @author TroUni Team
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * API lấy danh sách tất cả users

     * Endpoint: GET /api/debug/users

     * @return ResponseEntity - Danh sách users dưới dạng Map
     */

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", userResponses));
    }


    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            UserResponse userResponse = UserResponse.fromUser(user);
            return ResponseEntity.ok(ApiResponse.success("User found", userResponse));
        }
        return ResponseEntity.notFound().build();
    }


    /**
     * API kiểm tra token có bị blacklist không

     * Endpoint: POST /api/debug/check-token

     * @param request - Map chứa token
     * @return ResponseEntity - Kết quả kiểm tra token
     */
    @PostMapping("/check-token")
    public ResponseEntity<?> checkToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("TOKEN_REQUIRED", "Token is required"));
            }

            boolean isBlacklisted = tokenBlacklistService.isTokenBlacklisted(token);
            TokenCheckResponse response = isBlacklisted ? 
                TokenCheckResponse.blacklistedToken() : 
                TokenCheckResponse.validToken();

            return ResponseEntity.ok(ApiResponse.success("Token check completed", response));
        } catch (AppException e) {
            // Sử dụng GlobalExceptionHandler để xử lý AppException
            throw e;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("TOKEN_CHECK_ERROR", e.getMessage()));
        }
    }

    /**
     * API lấy thống kê blacklist

     * Endpoint: GET /api/debug/blacklist-stats

     * @return ResponseEntity - Thống kê blacklist
     */
    @GetMapping("/blacklist-stats")
    public ResponseEntity<?> getBlacklistStats() {
        try {
            long totalBlacklisted = tokenBlacklistService.getBlacklistCount();
            long expiredTokens = tokenBlacklistService.getExpiredTokenCount();
            long activeTokens = totalBlacklisted - expiredTokens;

            BlacklistStatsResponse stats = BlacklistStatsResponse.fromStats(
                totalBlacklisted, expiredTokens, activeTokens);

            return ResponseEntity.ok(ApiResponse.success("Blacklist stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("STATS_ERROR", e.getMessage()));
        }
    }
    
    /**
     * API cập nhật thông tin user của bản thân

     * Endpoint: PUT /api/users/me

     * @param updateRequest - Thông tin cập nhật user
     * @return ResponseEntity - Response chứa user đã cập nhật
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(@Valid @RequestBody UpdateUserRequest updateRequest) {
        try {
            User currentUser = authService.getCurrentUser();
            User updatedUser = authService.updateCurrentUser(currentUser, updateRequest);
            UserResponse userResponse = UserResponse.fromUser(updatedUser);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully!", userResponse));
        } catch (AppException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("USER_UPDATE_FAILED", "Failed to update user!"));
        }
    }
    
    /**
     * API admin/manager cập nhật thông tin user khác

     * Endpoint: PUT /api/users/{userId}

     * @param userId - UUID của user cần cập nhật
     * @param updateRequest - Thông tin cập nhật user
     * @return ResponseEntity - Response chứa user đã cập nhật
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<UserResponse>> adminUpdateUser(@PathVariable UUID userId, @Valid @RequestBody AdminUpdateUserRequest updateRequest) {
        try {
            User currentUser = authService.getCurrentUser();
            User updatedUser = authService.adminUpdateUser(currentUser, userId, updateRequest);
            UserResponse userResponse = UserResponse.fromUser(updatedUser);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully!", userResponse));
        } catch (AppException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("USER_UPDATE_FAILED", "Failed to update user!"));
        }
    }
    
    /**
     * API vô hiệu hóa tài khoản user (soft delete)

     * Endpoint: DELETE /api/users/{userId}

     * @param userId - UUID của user cần vô hiệu hóa
     * @return ResponseEntity - Response chứa user đã bị vô hiệu hóa
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<UserResponse>> deleteUser(@PathVariable UUID userId) {
        try {
            User currentUser = authService.getCurrentUser();
            User deletedUser = authService.deleteUser(currentUser, userId);
            UserResponse userResponse = UserResponse.fromUser(deletedUser);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully!", userResponse));
        } catch (AppException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("USER_DELETE_FAILED", "Failed to delete user!"));
        }
    }
    
    /**
     * API xóa hoàn toàn user khỏi database (hard delete) - Chỉ dành cho Admin

     * Endpoint: DELETE /api/users/{userId}/hard-delete

     * @param userId - UUID của user cần xóa hoàn toàn
     * @return ResponseEntity - Response chứa thông tin user đã xóa và số lượng records bị ảnh hưởng
     */
    @DeleteMapping("/{userId}/hard-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> hardDeleteUser(@PathVariable UUID userId) {
        try {
            User currentUser = authService.getCurrentUser();
            authService.hardDeleteUser(currentUser, userId);
            return ResponseEntity.ok(ApiResponse.success("User permanently deleted from database!", userId.toString()));
        } catch (AppException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("USER_HARD_DELETE_FAILED", "Failed to permanently delete user!"));
        }
    }

}

