package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.AuthResponse;
import com.trouni.tro_uni.dto.LoginRequest;
import com.trouni.tro_uni.dto.SignupRequest;
import com.trouni.tro_uni.entity.Profile;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.AuthenticationErrorCode;
import com.trouni.tro_uni.exception.errorcode.GeneralErrorCode;
import com.trouni.tro_uni.exception.errorcode.TokenErrorCode;
import com.trouni.tro_uni.repository.ProfileRepository;
import com.trouni.tro_uni.repository.UserRepository;
import com.trouni.tro_uni.util.JwtUtil;
import com.trouni.tro_uni.service.EmailVerificationService;
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
import org.springframework.transaction.annotation.Propagation;

import java.util.Map;
import java.util.UUID;

/**
 * AuthService - Service xử lý xác thực và đăng ký người dùng
 * 
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

    /**
     * Xác thực người dùng đăng nhập
     * 
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
        return new AuthResponse(jwt,
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                userPrincipal.getRole().getValue());
    }

    /**
     * Đăng ký tài khoản người dùng mới (chỉ gửi OTP, chưa tạo user)
     * 
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
     * 
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
     * 
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
     * Gửi email verification trong transaction riêng biệt
     * 
     * Sử dụng REQUIRES_NEW để tạo transaction mới, tránh rollback transaction chính
     * nếu có lỗi khi gửi email
     *
     * @param user - User cần gửi email verification
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendVerificationEmailAsync(User user) {
        try {
            emailVerificationService.createAndSendVerification(user);
            log.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", user.getEmail(), e.getMessage());
            // Không re-throw exception để tránh rollback transaction chính
        }
    }

    /**
     * Đăng ký user mà không gửi email (dành cho test)
     *
     * @param signUpRequest - Thông tin đăng ký
     * @return String - Thông báo đăng ký thành công
     * @throws AppException - Khi username hoặc email đã tồn tại
     */
    @Transactional
    public String registerUserWithoutEmail(SignupRequest signUpRequest) {
        // Bước 1: Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new AppException(AuthenticationErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // Bước 2: Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new AppException(AuthenticationErrorCode.EMAIL_EXISTED);
        }

        // Bước 3: Tạo User entity mới
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(signUpRequest.getRole() != null ? signUpRequest.getRole() : UserRole.STUDENT);

        // Bước 4: Lưu User vào database
        User savedUser = userRepository.save(user);

        // Bước 5: Tạo Profile cho user
        Profile profile = new Profile();
        profile.setUser(savedUser);
        if (signUpRequest.getFirstName() != null && signUpRequest.getLastName() != null) {
            profile.setFullName(signUpRequest.getFirstName() + " " + signUpRequest.getLastName());
        }

        // Bước 6: Lưu Profile vào database
        profileRepository.save(profile);

        return "User registered successfully! (Test mode - no email sent)";
    }
}
