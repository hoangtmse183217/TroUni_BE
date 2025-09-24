package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.ApiResponse;
import com.trouni.tro_uni.dto.AuthResponse;
import com.trouni.tro_uni.dto.LoginRequest;
import com.trouni.tro_uni.dto.SignupRequest;
import com.trouni.tro_uni.dto.UserResponse;
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("AUTHENTICATION_FAILED", "Authentication failed!"));
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("REGISTRATION_FAILED", "Registration failed!"));
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
     * API test để kiểm tra hoạt động của auth endpoints
     * <p>
     * Endpoint: GET /api/auth/test
     * <p>
     * Response:
     * {
     *   "message": "Auth endpoints are working!"
     * }
     * <p>
     * @return ResponseEntity - Response test
     */
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("Auth endpoints are working!", null));
    }

    /**
     * API test signup không gửi email
     * <p>
     * Endpoint: POST /api/auth/test-signup
     * <p>
     * @param signUpRequest - Thông tin đăng ký
     * @return ResponseEntity - Response test
     */
    @PostMapping("/test-signup")
    public ResponseEntity<?> testSignup(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            // Test chỉ tạo user và profile, không gửi email
            String message = authService.registerUserWithoutEmail(signUpRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(message, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("TEST_SIGNUP_ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("TEST_SIGNUP_FAILED", "Registration failed!"));
        }
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
        try {
            User currentUser = authService.getCurrentUser();
            UserResponse userResponse = UserResponse.fromUser(currentUser);
            return ResponseEntity.ok(ApiResponse.success("User information retrieved successfully", userResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("UNAUTHORIZED", "User not authenticated"));
        }
    }
}
