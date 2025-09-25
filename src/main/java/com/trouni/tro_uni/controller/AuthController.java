package com.trouni.tro_uni.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.response.AuthResponse;
import com.trouni.tro_uni.dto.request.LoginRequest;
import com.trouni.tro_uni.dto.request.SignupRequest;
import com.trouni.tro_uni.dto.response.UserResponse;
import com.trouni.tro_uni.dto.request.GoogleLoginRequest;
import com.trouni.tro_uni.dto.request.ForgotPasswordRequest;
import com.trouni.tro_uni.dto.response.ForgotPasswordResponse;
import com.trouni.tro_uni.dto.request.ResetPasswordRequest;
import com.trouni.tro_uni.dto.response.ResetPasswordResponse;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AuthController - Controller xử lý các API xác thực
 * <p>
 * Chức năng chính:
 * - Đăng nhập user (login)
 * - Đăng ký user mới (signup)
 * - Đăng xuất user (logout)
 * - Lấy thông tin user hiện tại
 * - Test endpoint để kiểm tra hoạt động
 * <p>
 * @author TroUni Team
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private final AuthService authService;

    /**
     * API đăng nhập user
     * <p>
     * Endpoint: POST /api/auth/login
     * <p>
     * Request body:
     * {
     *   "usernameOrEmail": "username hoặc email",
     *   "password": "password"
     * }
     * <p>
     * Response thành công:
     * {
     *   "token": "JWT token",
     *   "id": 1,
     *   "username": "username",
     *   "email": "email@example.com",
     *   "role": "STUDENT"
     * }
     * <p>
     * @param loginRequest - Thông tin đăng nhập
     * @return ResponseEntity - Response chứa JWT token và thông tin user
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("INVALID_CREDENTIALS", "Invalid username/email or password!"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("USER_NOT_FOUND", "User not found!"));
        }
    }
    
    /**
     * API đăng ký user mới
     * <p>
     * Endpoint: POST /api/auth/signup
     * <p>
     * Request body:
     * {
     *   "username": "username",
     *   "email": "email@example.com",
     *   "password": "password",
     *   "firstName": "First Name",
     *   "lastName": "Last Name"
     * }
     * <p>
     * Response thành công:
     * {
     *   "message": "User registered successfully!"
     * }
     * <p>
     * @param signUpRequest - Thông tin đăng ký
     * @return ResponseEntity - Response thông báo kết quả đăng ký
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            Map<String, String> response = authService.registerUser(signUpRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Registration successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("REGISTRATION_ERROR", e.getMessage()));
        }
    }
    
    /**
     * API đăng xuất user
     * <p>
     * Endpoint: POST /api/auth/logout
     * <p>
     * Headers:
     * Authorization: Bearer <JWT_TOKEN>
     * <p>
     * Response thành công:
     * {
     *   "message": "User logged out successfully!"
     * }
     * <p>
     * @param request - HttpServletRequest để lấy token
     * @return ResponseEntity - Response thông báo đăng xuất thành công
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        try {
            // Extract JWT token từ Authorization header
            String token = extractTokenFromRequest(request);
            
            // Logout với token để blacklist
            authService.logout(token);
            
            return ResponseEntity.ok(ApiResponse.success("User logged out successfully!", null));
        } catch (AppException e) {
            // Sử dụng GlobalExceptionHandler để xử lý AppException
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("LOGOUT_FAILED", "Unexpected error occurred during logout"));
        }
    }
    
    /**
     * Extract JWT token từ HttpServletRequest
     * <p>
     * @param request - HttpServletRequest
     * @return String - JWT token hoặc null nếu không tìm thấy
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    
    /**
     * API đăng nhập bằng Google OAuth
     * <p>
     * Endpoint: POST /auth/google-login
     * <p>
     * Request Body:
     * {
     *   "accessToken": "google_access_token"
     * }
     * <p>
     * Response thành công:
     * {
     *   "success": true,
     *   "message": "Google login successful!",
     *   "data": {
     *     "token": "jwt_token",
     *     "id": "uuid",
     *     "username": "username",
     *     "email": "email@example.com",
     *     "role": "STUDENT"
     *   }
     * }
     * 
     * @param googleLoginRequest - Request chứa Google access token
     * @return ResponseEntity - Response chứa JWT token và thông tin user
     */
    @PostMapping("/google-login")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(@Valid @RequestBody GoogleLoginRequest googleLoginRequest) throws JsonProcessingException {
        AuthResponse response = authService.authenticateWithGoogle(googleLoginRequest);
        return ResponseEntity.ok(ApiResponse.success("Google login successful!", response));
    }
    
    /**
     * API lấy thông tin user hiện tại đang đăng nhập
     * <p>
     * Endpoint: GET /api/auth/me
     * <p>
     * Headers:
     * Authorization: Bearer <JWT_TOKEN>
     * <p>
     * Response thành công:
     * {
     *   "id": 1,
     *   "username": "username",
     *   "email": "email@example.com",
     *   "role": "STUDENT"
     * }
     * 
     * @return ResponseEntity - Response chứa thông tin user hiện tại
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        User currentUser = authService.getCurrentUser();
        UserResponse userResponse = UserResponse.fromUser(currentUser);
        return ResponseEntity.ok(ApiResponse.success("User information retrieved successfully", userResponse));
    }

    /**
     * API quên mật khẩu - gửi email reset password
     * <p>
     * Endpoint: POST /auth/forgot-password
     * <p>
     * Request body:
     * {
     *   "email": "email@example.com"
     * }
     * <p>
     * Response thành công:
     * {
     *   "success": true,
     *   "message": "Password reset instructions have been sent to your email address."
     * }
     * <p>
     * @param request - Thông tin email cần reset password
     * @return ResponseEntity - Response thông báo kết quả
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            ForgotPasswordResponse response = authService.forgotPassword(request);
            return ResponseEntity.ok(ApiResponse.success("Password reset email sent successfully", response));
        } catch (AppException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    /**
     * API reset mật khẩu với token
     * <p>
     * Endpoint: POST /auth/reset-password
     * <p>
     * Request body:
     * {
     *   "token": "reset_token_from_email",
     *   "newPassword": "new_password"
     * }
     * <p>
     * Response thành công:
     * {
     *   "success": true,
     *   "message": "Password has been reset successfully. You can now login with your new password."
     * }
     * <p>
     * @param request - Thông tin token và password mới
     * @return ResponseEntity - Response thông báo kết quả
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            ResetPasswordResponse response = authService.resetPassword(request);
            return ResponseEntity.ok(ApiResponse.success("Password reset successfully", response));
        } catch (AppException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

}
