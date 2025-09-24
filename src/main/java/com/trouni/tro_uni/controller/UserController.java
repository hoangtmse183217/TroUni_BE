package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.ApiResponse;
import com.trouni.tro_uni.dto.BlacklistStatsResponse;
import com.trouni.tro_uni.dto.SignupRequest;
import com.trouni.tro_uni.dto.TokenCheckResponse;
import com.trouni.tro_uni.dto.UserResponse;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.repository.UserRepository;
import com.trouni.tro_uni.service.AuthService;
import com.trouni.tro_uni.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            UserResponse userResponse = UserResponse.fromUser(user);
            return ResponseEntity.ok(ApiResponse.success("User found", userResponse));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create-test-user")
    public ResponseEntity<?> createTestUser() {
        try {
            // Check if user already exists
            if (userRepository.findByUsername("testuser").isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Test user already exists", null));
            }

            SignupRequest signupRequest = new SignupRequest();
            signupRequest.setUsername("testuser");
            signupRequest.setEmail("test@example.com");
            signupRequest.setPassword("password123");
            signupRequest.setFirstName("Test");
            signupRequest.setLastName("User");
            signupRequest.setRole(UserRole.STUDENT); // Set role mặc định

            Map<String, String> result = authService.registerUser(signupRequest);
            return ResponseEntity.ok(ApiResponse.success("Test user created successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("CREATE_USER_ERROR", e.getMessage()));
        }
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

}

