package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.response.AuthResponse;
import com.trouni.tro_uni.dto.request.LoginRequest;
import com.trouni.tro_uni.dto.request.SignupRequest;
import com.trouni.tro_uni.dto.request.GoogleLoginRequest;
import com.trouni.tro_uni.dto.request.UpdateUserRequest;
import com.trouni.tro_uni.dto.request.AdminUpdateUserRequest;
import com.trouni.tro_uni.dto.request.ForgotPasswordRequest;
import com.trouni.tro_uni.dto.response.ForgotPasswordResponse;
import com.trouni.tro_uni.dto.request.ResetPasswordRequest;
import com.trouni.tro_uni.dto.response.ResetPasswordResponse;
import com.trouni.tro_uni.entity.Profile;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.enums.AccountStatus;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.AuthenticationErrorCode;
import com.trouni.tro_uni.exception.errorcode.GeneralErrorCode;
import com.trouni.tro_uni.exception.errorcode.TokenErrorCode;
import com.trouni.tro_uni.mapper.UserMapper;
import com.trouni.tro_uni.repository.ProfileRepository;
import com.trouni.tro_uni.repository.UserRepository;
import com.trouni.tro_uni.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;
import java.util.UUID;

/**
 * AuthService - Service xử lý xác thực và đăng ký người dùng

 * Chức năng chính:
 * - Xác thực đăng nhập (username/email + password)
 * - Đăng ký tài khoản mới
 * - Quản lý JWT token
 * - Lấy thông tin user hiện tại
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    // Dependencies injection
    private final AuthenticationManager authenticationManager;  // Quản lý xác thực
    private final UserRepository userRepository;               // Repository cho User
    private final ProfileRepository profileRepository;         // Repository cho Profile
    private final PasswordEncoder passwordEncoder;             // Mã hóa password
    private final JwtUtil jwtUtil;                             // Utility tạo JWT token
    private final EmailVerificationService emailVerificationService; // Service xác thực email
    private final UserMapper userMapper;                       // MapStruct mapper for User

    /**
     * Xác thực người dùng đăng nhập

     * Quy trình:
     * 1. Tạo UsernamePasswordAuthenticationToken với thông tin đăng nhập
     * 2. Sử dụng AuthenticationManager để xác thực
     * 3. Lưu authentication vào SecurityContext
     * 4. Tạo JWT token từ thông tin user
     * 5. Trả về AuthResponse chứa token và thông tin user
     * 
     * @param loginRequest - Thông tin đăng nhập (username/email + password)
     * @return AuthResponse - Chứa JWT token và thông tin user
     */
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        // Bước 0: Kiểm tra user có đăng ký bằng Google không
        User existingUser = userRepository.findByUsername(loginRequest.getUsernameOrEmail())
                .orElse(userRepository.findByEmail(loginRequest.getUsernameOrEmail()).orElse(null));
        
        if (existingUser != null && existingUser.isGoogleAccount()) {
            throw new AppException(AuthenticationErrorCode.ACCOUNT_REGISTERED_WITH_GOOGLE);
        }
        
        // Bước 0.5: Kiểm tra trạng thái tài khoản
        if (existingUser != null && existingUser.getStatus() != AccountStatus.ACTIVE) {
            checkStatusAccount(existingUser);
        }
        
        // Bước 1: Tạo authentication token
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()));

        // Bước 2: Lưu authentication vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Bước 3: Lấy thông tin user từ authentication
        User userPrincipal = (User) authentication.getPrincipal();
        
        // Bước 4: Tạo JWT token
        String jwt = jwtUtil.generateToken(userPrincipal);

        // Bước 5: Trả về response với token và thông tin user
        return AuthResponse.builder()
                .token(jwt)
                .id(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .email(userPrincipal.getEmail())
                .role(userPrincipal.getRole().getValue().toUpperCase())
                .build();
    }

    /**
     * Đăng ký tài khoản người dùng mới (chỉ gửi OTP, chưa tạo user)

     * Quy trình:
     * 1. Kiểm tra email đã tồn tại chưa
     * 2. Tạo và gửi mã xác thực email
     * 3. Lưu tạm thông tin vào email_verifications table
     * 4. User sẽ được tạo sau khi xác thực OTP thành công
     * 
     * @param signUpRequest - Thông tin đăng ký
     * @return Map<String, String> - Thông báo và email để frontend giữ lại
     * @throws AppException - Khi email đã tồn tại
     */
    public Map<String, String> registerUser(SignupRequest signUpRequest) {
        // Bước 1: Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            // Kiểm tra thêm: nếu email đã tồn tại và là Google account
            User existingUser = userRepository.findByEmail(signUpRequest.getEmail()).orElse(null);
            if (existingUser != null && existingUser.isGoogleAccount()) {
                throw new AppException(AuthenticationErrorCode.ACCOUNT_REGISTERED_WITH_GOOGLE);
            }
            throw new AppException(AuthenticationErrorCode.EMAIL_EXISTED);
        }

        // Bước 2: Gửi OTP và lưu tạm thông tin
        try {
            emailVerificationService.createAndSendVerificationForSignup(signUpRequest);
            log.info("Verification email sent to: {}", signUpRequest.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", signUpRequest.getEmail(), e.getMessage());
            throw new AppException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }

        // Bước 3: Trả về response với email để frontend giữ lại
        return Map.of(
            "message", "Verification code sent to your email! Please check your email and verify your account.",
            "email", signUpRequest.getEmail()
        );
    }

    /**
     * Đăng xuất người dùng

     * Quy trình:
     * 1. Lấy JWT token từ SecurityContext
     * 2. Blacklist token để ngăn chặn sử dụng lại
     * 3. Xóa authentication khỏi SecurityContext
     * 
     * @param token - JWT token cần blacklist (optional)
     * @throws AppException - Khi có lỗi trong quá trình logout
     */
    public void logout(String token) {
        try {
            // Nếu có token, blacklist nó
            if (token != null && !token.trim().isEmpty()) {
                User currentUser = getCurrentUser();
                if (currentUser == null) {
                    throw new AppException(TokenErrorCode.LOGOUT_USER_NOT_AUTHENTICATED);
                }
                jwtUtil.blacklistToken(token, currentUser.getId());
            }
        } catch (AppException e) {
            // Re-throw AppException
            throw e;
        } catch (Exception e) {
            // Log lỗi và throw AppException
            log.error("Error during logout: {}", e.getMessage());
            throw new AppException(TokenErrorCode.LOGOUT_FAILED);
        } finally {
            // Luôn xóa authentication khỏi SecurityContext
            SecurityContextHolder.clearContext();
        }
    }
    
    /**
     * Đăng xuất người dùng (không có token)

     * Chỉ xóa authentication khỏi SecurityContext
     * Sử dụng khi không có token để blacklist
     */
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Lấy thông tin user hiện tại đang đăng nhập
     * 
     * @return User - Thông tin user hiện tại
     * @throws AppException - Khi không có user nào đăng nhập hoặc không tìm thấy user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(AuthenticationErrorCode.USER_NOT_FOUND));
        }
        throw new AppException(AuthenticationErrorCode.UNAUTHORIZED);
    }

    /**
     * Lấy ID của user hiện tại đang đăng nhập
     *
     * @return UUID - ID của user hiện tại
     * @throws AppException - Khi không có user nào đăng nhập
     */
    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Đăng nhập bằng Google OAuth
     * <p>
     * Chức năng chính:
     * - Validate Google access token
     * - Lấy thông tin user từ Google API
     * - Tạo user mới nếu chưa tồn tại
     * - Trả về JWT token và thông tin user
     * 
     * @param googleLoginRequest - Request chứa Google access token
     * @return AuthResponse - Response chứa JWT token và user info
     * @throws AppException - Khi access token không hợp lệ hoặc có lỗi xảy ra
     */
    @Transactional
    public AuthResponse authenticateWithGoogle(GoogleLoginRequest googleLoginRequest) throws JsonProcessingException {
        try {
            // Bước 1: Lấy thông tin user từ Google API
            String userInfoEndpoint = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + 
                    googleLoginRequest.getAccessToken();
            
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(userInfoEndpoint, String.class);
            
            // Bước 2: Kiểm tra access token có hợp lệ không
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new AppException(AuthenticationErrorCode.TOKEN_INVALID);
            }
            
            // Bước 3: Parse JSON response từ Google
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode userInfoJson = objectMapper.readTree(response.getBody());
            
            // Bước 4: Lấy các trường thông tin từ Google
            String email = userInfoJson.get("email").asText();
            String name = userInfoJson.get("name").asText();
            String picture = userInfoJson.has("picture") ? userInfoJson.get("picture").asText() : "";
            String gender = userInfoJson.has("gender") ? userInfoJson.get("gender").asText() : "";
            
            // Bước 5: Kiểm tra user đã tồn tại chưa
            User existingUser = userRepository.findByEmail(email).orElse(null);
            
            if (existingUser != null) {
                // User đã tồn tại - kiểm tra có phải Google account không
                if (!existingUser.isGoogleAccount()) {
                    throw new AppException(AuthenticationErrorCode.ACCOUNT_REGISTERED_WITH_GOOGLE);
                }
                
                // Kiểm tra trạng thái tài khoản
                if (existingUser.getStatus() != AccountStatus.ACTIVE) {
                    checkStatusAccount(existingUser);
                }
            } else {
                // User chưa tồn tại - tạo user mới
                existingUser = createGoogleUser(email, name, picture, gender);
            }
            
            // Bước 6: Tạo JWT token
            String jwt = jwtUtil.generateToken(existingUser);
            
            // Bước 7: Trả về response
            return AuthResponse.builder()
                    .token(jwt)
                    .id(existingUser.getId())
                    .username(existingUser.getUsername())
                    .email(existingUser.getEmail())
                    .role(existingUser.getRole().name())
                    .build();
            
        } catch (Exception e) {
            log.error("Error during Google authentication: {}", e.getMessage());
            if (e instanceof AppException) {
                throw e;
            }
            throw new AppException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void checkStatusAccount(User existingUser) {
        switch (existingUser.getStatus()) {
            case LOCKED:
                throw new AppException(AuthenticationErrorCode.ACCOUNT_LOCKED);
            case SUSPENDED:
                throw new AppException(AuthenticationErrorCode.ACCOUNT_DISABLED);
            case DELETED:
                throw new AppException(AuthenticationErrorCode.USER_NOT_FOUND);
        }
    }

    /**
     * Tạo user mới từ thông tin Google
     */
    private User createGoogleUser(String email, String name, String picture, String gender) {
        // Tạo username từ email (phần trước @)
        String username = email.split("@")[0];
        
        // Đảm bảo username unique
        String originalUsername = username;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = originalUsername + counter;
            counter++;
        }
        
        // Tạo User entity
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setGoogleAccount(true);
        user.setRole(UserRole.STUDENT); // Default role
        
        // Lưu User
        User savedUser = userRepository.save(user);
        
        // Tạo Profile cho user
        Profile profile = new Profile();
        profile.setUser(savedUser);
        profile.setFullName(name);
        profile.setAvatarUrl(picture);
        
        // Set gender từ Google (nếu có)
        if (gender != null && !gender.isEmpty()) {
            profile.setGender(gender);
        }
        
        // Lưu Profile
        profileRepository.save(profile);
        
        log.info("Created new Google user: {} ({})", username, email);
        return savedUser;
    }
    
    /**
     * Kiểm tra và cập nhật username nếu cần thiết
     */
    private void validateAndUpdateUsername(User user, String newUsername) {
        if (newUsername != null && !newUsername.trim().isEmpty()) {
            String trimmedUsername = newUsername.trim();
            if (!trimmedUsername.equals(user.getUsername())) {
                if (userRepository.existsByUsername(trimmedUsername)) {
                    throw new AppException(AuthenticationErrorCode.USERNAME_ALREADY_EXISTS);
                }
                user.setUsername(trimmedUsername);
            }
        }
    }
    
    /**
     * Kiểm tra và cập nhật email nếu cần thiết
     */
    private void validateAndUpdateEmail(User user, String newEmail) {
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            String trimmedEmail = newEmail.trim();
            if (!trimmedEmail.equals(user.getEmail())) {
                if (userRepository.existsByEmail(trimmedEmail)) {
                    throw new AppException(AuthenticationErrorCode.EMAIL_ALREADY_EXISTS);
                }
                user.setEmail(trimmedEmail);
            }
        }
    }

    /**
     * Cập nhật thông tin user của bản thân
     */
    public User updateCurrentUser(User currentUser, UpdateUserRequest updateRequest) {
        // Kiểm tra và cập nhật username (nếu có thay đổi)
        validateAndUpdateUsername(currentUser, updateRequest.getUsername());
        
        // Kiểm tra và cập nhật email (nếu có thay đổi)
        validateAndUpdateEmail(currentUser, updateRequest.getEmail());
        
        // Lưu user đã cập nhật
        User savedUser = userRepository.save(currentUser);
        
        log.info("Updated user: {}", currentUser.getUsername());
        return savedUser;
    }
    
    /**
     * Admin/Manager cập nhật thông tin user khác
     */
    public User adminUpdateUser(User currentUser, UUID targetUserId, AdminUpdateUserRequest updateRequest) {
        // Kiểm tra quyền admin/manager
        if (currentUser.getRole() != UserRole.ADMIN && currentUser.getRole() != UserRole.MANAGER) {
            throw new AppException(AuthenticationErrorCode.ACCESS_DENIED);
        }
        
        // Tìm user cần cập nhật
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new AppException(AuthenticationErrorCode.USER_NOT_FOUND));
        
        // Kiểm tra quyền thay đổi role (chỉ admin mới có thể thay đổi role)
        if (updateRequest.getRole() != null && currentUser.getRole() != UserRole.ADMIN) {
            throw new AppException(AuthenticationErrorCode.ACCESS_DENIED);
        }
        
        // Cập nhật username (nếu có)
        validateAndUpdateUsername(targetUser, updateRequest.getUsername());
        
        // Cập nhật email (nếu có)
        validateAndUpdateEmail(targetUser, updateRequest.getEmail());
        
        // Use mapper to update fields
        userMapper.updateUserFields(updateRequest, targetUser);
        
        // Lưu user đã cập nhật
        User savedUser = userRepository.save(targetUser);
        
        log.info("Admin {} updated user: {}", currentUser.getUsername(), targetUser.getUsername());
        return savedUser;
    }
    
    /**
     * Vô hiệu hóa tài khoản user (soft delete)
     */
    public User deleteUser(User currentUser, UUID targetUserId) {
        // Kiểm tra quyền admin/manager
        if (currentUser.getRole() != UserRole.ADMIN && currentUser.getRole() != UserRole.MANAGER) {
            throw new AppException(AuthenticationErrorCode.ACCESS_DENIED);
        }
        
        // Không thể xóa chính mình
        if (currentUser.getId().equals(targetUserId)) {
            throw new AppException(AuthenticationErrorCode.CANNOT_DELETE_SELF);
        }
        
        // Tìm user cần vô hiệu hóa
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new AppException(AuthenticationErrorCode.USER_NOT_FOUND));
        
        // Vô hiệu hóa tài khoản (soft delete)
        targetUser.setStatus(AccountStatus.DELETED);
        User savedUser = userRepository.save(targetUser);
        
        log.info("Admin {} deleted user: {}", currentUser.getUsername(), targetUser.getUsername());
        return savedUser;
    }
    
    /**
     * Xóa hoàn toàn user khỏi database (hard delete) - Chỉ dành cho Admin
     */
    @Transactional
    public void hardDeleteUser(User currentUser, UUID targetUserId) {
        // Kiểm tra quyền admin (chỉ admin mới được hard delete)
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new AppException(AuthenticationErrorCode.ACCESS_DENIED);
        }
        
        // Không thể xóa chính mình
        if (currentUser.getId().equals(targetUserId)) {
            throw new AppException(AuthenticationErrorCode.CANNOT_DELETE_SELF);
        }
        
        // Tìm user cần xóa
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new AppException(AuthenticationErrorCode.USER_NOT_FOUND));
        
        // Lưu thông tin user trước khi xóa để trả về response

        try {
            // Xóa tất cả dữ liệu liên quan đến user
            // Cascade sẽ tự động xóa Profile, Subscription, Payment, UserVerification
            // Cần xóa thủ công các mối quan hệ khác
            
            // Xóa Bookmark (user bookmarked rooms)
            userRepository.deleteUserBookmarks(targetUserId);
            
            // Xóa Review (user reviewed rooms)
            userRepository.deleteUserReviews(targetUserId);
            
            // Xóa Report (user reported content)
            userRepository.deleteUserReports(targetUserId);
            
            // Xóa Notification (user received notifications)
            userRepository.deleteUserNotifications(targetUserId);
            
            // Xóa Room và các mối quan hệ liên quan (nếu user là landlord)
            if (targetUser.getRole() == UserRole.LANDLORD) {
                userRepository.deleteUserRoomsAndRelated(targetUserId);
            }
            
            // Xóa UserVerification (user verification records)
            userRepository.deleteUserVerifications(targetUserId);
            
            // Cuối cùng xóa User (cascade sẽ xóa Profile, Subscription, Payment)
            userRepository.delete(targetUser);
            
            log.info("Admin {} hard deleted user: {}.",
                    currentUser.getUsername(), targetUser.getUsername());
            
        } catch (Exception e) {
            log.error("Error during hard delete of user {}: {}", targetUser.getUsername(), e.getMessage());
            throw new AppException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Gửi email reset password
     */
    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        try {
            // Bước 1: Kiểm tra email có tồn tại không
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AppException(AuthenticationErrorCode.USER_NOT_FOUND));
            
            // Bước 2: Kiểm tra tài khoản có phải Google account không
            if (user.isGoogleAccount()) {
                throw new AppException(AuthenticationErrorCode.ACCOUNT_REGISTERED_WITH_GOOGLE);
            }
            
            // Bước 3: Kiểm tra trạng thái tài khoản
            if (user.getStatus() != AccountStatus.ACTIVE) {
                checkStatusAccount(user);
            }
            
            // Bước 4: Tạo và gửi reset password email
            emailVerificationService.createAndSendPasswordResetEmail(user);
            
            log.info("Password reset email sent to: {}", request.getEmail());
            
            return ForgotPasswordResponse.builder()
                    .success(true)
                    .message("Password reset instructions have been sent to your email address.")
                    .build();
                
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during forgot password for email {}: {}", request.getEmail(), e.getMessage());
            throw new AppException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Reset password với token
     */
    @Transactional
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        try {
            // Bước 1: Validate và lấy thông tin từ token
            Map<String, Object> tokenData = emailVerificationService.validatePasswordResetToken(request.getToken());
            
            // Bước 2: Lấy user ID từ token
            String userIdStr = (String) tokenData.get("userId");
            UUID userId = UUID.fromString(userIdStr);
            
            // Bước 3: Tìm user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(AuthenticationErrorCode.USER_NOT_FOUND));
            
            // Bước 4: Kiểm tra tài khoản có phải Google account không
            if (user.isGoogleAccount()) {
                throw new AppException(AuthenticationErrorCode.ACCOUNT_REGISTERED_WITH_GOOGLE);
            }
            
            // Bước 5: Cập nhật password mới
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
            
            // Bước 6: Xóa token đã sử dụng
            emailVerificationService.deletePasswordResetToken(request.getToken());
            
            log.info("Password reset successfully for user: {}", user.getUsername());
            
            return ResetPasswordResponse.builder()
                    .success(true)
                    .message("Password has been reset successfully. You can now login with your new password.")
                    .build();
                
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during password reset: {}", e.getMessage());
            throw new AppException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
