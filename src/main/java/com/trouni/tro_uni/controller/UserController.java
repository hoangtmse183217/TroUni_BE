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
     * <p>
     * Endpoint: GET /api/debug/users
     * <p>
     * Response:
     * [
     *   {
     *     "id": 1,
     *     "username": "testuser",
     *     "email": "test@example.com",
     *     "role": "STUDENT",
     *     "phoneVerified": false,
     *     "idVerificationStatus": "NOT_VERIFIED",
     *     "createdAt": "2024-01-01T10:00:00",
     *     "updatedAt": "2024-01-01T10:00:00",
     *     "profile": {
     *       "id": 1,
     *       "fullName": "Test User",
     *       "gender": null,
     *       "phoneNumber": null,
     *       "avatarUrl": null,
     *       "badge": null
     *     }
     *   }
     * ]
     * <p>
     * Lưu ý: Sử dụng Map thay vì trả về User entity trực tiếp để tránh circular reference
     * giữa User và Profile entities
     * 
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
     * <p>
     * Endpoint: POST /api/debug/check-token
     * <p>
     * Request body:
     * {
     *   "token": "JWT_TOKEN"
     * }
     * <p>
     * Response:
     * {
     *   "isBlacklisted": true/false,
     *   "message": "Token status"
     * }
     * 
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
     * <p>
     * Endpoint: GET /api/debug/blacklist-stats
     * <p>
     * Response:
     * {
     *   "totalBlacklisted": 5,
     *   "expiredTokens": 2,
     *   "activeTokens": 3
     * }
     * 
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
     * <p>
     * Endpoint: PUT /api/users/me
     * <p>
     * Headers:
     * Authorization: Bearer <JWT_TOKEN>
     * Content-Type: application/json
     * <p>
     * Request Body:
     * {
     *   "username": "new_username",
     *   "email": "new_email@example.com"
     * }
     * <p>
     * Response thành công:
     * {
     *   "success": true,
     *   "message": "User updated successfully!",
     *   "data": {
     *     "id": "uuid",
     *     "username": "new_username",
     *     "email": "new_email@example.com",
     *     "role": "STUDENT",
     *     "status": "ACTIVE",
     *     "createdAt": "2024-01-01T00:00:00",
     *     "updatedAt": "2024-01-01T00:00:00"
     *   }
     * }
     * 
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
     * <p>
     * Endpoint: PUT /api/users/{userId}
     * <p>
     * Headers:
     * Authorization: Bearer <JWT_TOKEN>
     * Content-Type: application/json
     * <p>
     * Path Variables:
     * userId - UUID của user cần cập nhật
     * <p>
     * Request Body:
     * {
     *   "username": "new_username",
     *   "email": "new_email@example.com",
     *   "role": "LANDLORD",
     *   "status": "ACTIVE"
     * }
     * <p>
     * Response thành công:
     * {
     *   "success": true,
     *   "message": "User updated successfully!",
     *   "data": {
     *     "id": "uuid",
     *     "username": "new_username",
     *     "email": "new_email@example.com",
     *     "role": "LANDLORD",
     *     "status": "ACTIVE",
     *     "createdAt": "2024-01-01T00:00:00",
     *     "updatedAt": "2024-01-01T00:00:00"
     *   }
     * }
     * 
     * @param userId - UUID của user cần cập nhật
     * @param updateRequest - Thông tin cập nhật user
     * @return ResponseEntity - Response chứa user đã cập nhật
     */
    @PutMapping("/{userId}")
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
     * <p>
     * Endpoint: DELETE /api/users/{userId}
     * <p>
     * Headers:
     * Authorization: Bearer <JWT_TOKEN>
     * <p>
     * Path Variables:
     * userId - UUID của user cần vô hiệu hóa
     * <p>
     * Response thành công:
     * {
     *   "success": true,
     *   "message": "User deleted successfully!",
     *   "data": {
     *     "id": "uuid",
     *     "username": "username",
     *     "email": "email@example.com",
     *     "role": "STUDENT",
     *     "status": "DELETED",
     *     "createdAt": "2024-01-01T00:00:00",
     *     "updatedAt": "2024-01-01T00:00:00"
     *   }
     * }
     * 
     * @param userId - UUID của user cần vô hiệu hóa
     * @return ResponseEntity - Response chứa user đã bị vô hiệu hóa
     */
    @DeleteMapping("/{userId}")
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

}

