package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.request.SignupRequest;
import com.trouni.tro_uni.entity.EmailVerification;
import com.trouni.tro_uni.entity.Profile;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.AuthenticationErrorCode;
import com.trouni.tro_uni.exception.errorcode.GeneralErrorCode;
import com.trouni.tro_uni.repository.EmailVerificationRepository;
import com.trouni.tro_uni.repository.ProfileRepository;
import com.trouni.tro_uni.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * EmailVerificationService - Service quản lý xác thực email

 * Chức năng chính:
 * - Tạo và gửi mã xác thực email
 * - Verify mã xác thực
 * - Quản lý thời hạn và attempts
 * - Cleanup expired verifications
 * - Rate limiting
 *
 * @author TroUni Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final VerificationCodeService verificationCodeService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // Rate limiting: tối đa 3 lần gửi email trong 1 giờ
    private static final int MAX_EMAILS_PER_HOUR = 3;

    /**
     * Verify mã xác thực email
     *
     * @param email - Email cần verify
     * @param verificationCode - Mã xác thực
     * @return boolean - true nếu verify thành công
     * @throws AppException - Khi có lỗi trong quá trình verify
     */
    @Transactional
    public boolean verifyEmail(String email, String verificationCode) {
        try {
            // Tìm EmailVerification
            Optional<EmailVerification> verificationOpt = emailVerificationRepository
                    .findByEmail(email);

            if (verificationOpt.isEmpty()) {
                throw new AppException(AuthenticationErrorCode.EMAIL_NOT_VERIFIED);
            }

            EmailVerification verification = verificationOpt.get();

            // Kiểm tra có thể thử verify không
            if (!verification.canAttempt()) {
                if (verification.isExpired()) {
                    throw new AppException(AuthenticationErrorCode.TOKEN_EXPIRED);
                }
                if (verification.isMaxAttemptsReached()) {
                    throw new AppException(AuthenticationErrorCode.ACCOUNT_LOCKED);
                }
            }

            // Verify mã
            boolean isValid = verification.verifyCode(verificationCode);

            if (isValid) {
                // Lưu trạng thái verified
                emailVerificationRepository.save(verification);

                // Nếu là signup (chưa có user), tạo user
                if (verification.getUser() == null) {
                    User newUser = createUserAfterVerification(verification);
                    log.info("User created after email verification: {} ({})", newUser.getUsername(), newUser.getEmail());
                    
                    // Xóa record email verification sau khi tạo user thành công
                    emailVerificationRepository.delete(verification);
                    log.info("Email verification record deleted for: {}", email);
                }

                // Gửi email chào mừng
                String username = verification.getUser() != null ? verification.getUser().getUsername() : verification.getUsername();
                emailService.sendWelcomeEmail(email, username);

                log.info("Email verified successfully for: {}", email);
                return true;
            } else {
                // Lưu số lần thử
                emailVerificationRepository.save(verification);

                if (verification.isMaxAttemptsReached()) {
                    throw new AppException(AuthenticationErrorCode.ACCOUNT_LOCKED);
                }

                throw new AppException(AuthenticationErrorCode.INVALID_CREDENTIALS);
            }

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to verify email {}: {}", email, e.getMessage());
            throw new AppException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gửi lại mã xác thực
     *
     * @param email - Email cần gửi lại mã
     * @return EmailVerification - EmailVerification entity mới
     * @throws AppException - Khi có lỗi trong quá trình gửi lại
     */
    @Transactional
    public EmailVerification resendVerificationCode(String email) {
        try {
            // Kiểm tra rate limiting
            checkRateLimit(email);

            // Tìm verification hiện tại
            Optional<EmailVerification> existingVerification = emailVerificationRepository
                    .findByEmail(email);

            if (existingVerification.isEmpty()) {
                throw new AppException(AuthenticationErrorCode.USER_NOT_FOUND);
            }

            EmailVerification verification = existingVerification.get();

            // Tạo mã mới
            String newCode = verificationCodeService.generateValidCode();
            verification.generateNewCode(newCode);

            // Lưu mã mới
            EmailVerification savedVerification = emailVerificationRepository.save(verification);

            // Gửi email mới
            emailService.sendVerificationEmail(
                    email,
                    newCode,
                    verification.getUser().getUsername()
            );

            log.info("Verification code resent for: {}", email);
            return savedVerification;

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to resend verification for {}: {}", email, e.getMessage());
            throw new AppException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Kiểm tra rate limiting
     *
     * @param email - Email cần kiểm tra
     * @throws AppException - Khi vượt quá giới hạn
     */
    private void checkRateLimit(String email) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long attemptsInLastHour = emailVerificationRepository.countAttemptsByEmailSince(email, oneHourAgo);

        if (attemptsInLastHour >= MAX_EMAILS_PER_HOUR) {
            throw new AppException(GeneralErrorCode.RATE_LIMIT_EXCEEDED);
        }
    }

    /**
     * Cleanup expired verifications (chạy mỗi 30 phút)
     */
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30 phút
    @Transactional
    public void cleanupExpiredVerifications() {
        try {
            int deletedCount = emailVerificationRepository.deleteExpiredVerifications(LocalDateTime.now());
            if (deletedCount > 0) {
                log.info("Cleaned up {} expired email verifications", deletedCount);
            }
        } catch (Exception e) {
            log.error("Failed to cleanup expired verifications: {}", e.getMessage());
        }
    }

    /**
     * Lấy thông tin verification của một email
     *
     * @param email - Email cần lấy thông tin
     * @return Optional<EmailVerification> - Thông tin verification
     */
    public Optional<EmailVerification> getVerificationInfo(String email) {
        return emailVerificationRepository.findByEmail(email);
    }


    /**
     * Tạo User sau khi verify OTP thành công
     *
     * @param emailVerification - EmailVerification đã verify
     * @return User - User đã được tạo
     */
    @Transactional
    public User createUserAfterVerification(EmailVerification emailVerification) {
        try {
            // Tạo user với thông tin cơ bản (email, username và password hash)
            User user = new User();
            user.setEmail(emailVerification.getEmail());
            user.setUsername(emailVerification.getUsername()); // Sử dụng username từ database
            user.setPassword(emailVerification.getPasswordHash()); // Sử dụng password đã hash từ database
            user.setRole(emailVerification.getUserRole()); // Sử dụng role từ signup
    
            // Lưu User vào database
            User savedUser = userRepository.save(user);

            // Tạo Profile cho user (để trống, user sẽ cập nhật sau)
            Profile profile = new Profile();
            profile.setUser(savedUser);
            // Không set fullName, user sẽ cập nhật sau
            profileRepository.save(profile);

            log.info("User created successfully after verification: {} ({})", savedUser.getUsername(), savedUser.getEmail());
            return savedUser;

        } catch (Exception e) {
            log.error("Failed to create user after verification: {}", e.getMessage());
            throw new AppException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Tạo và gửi mã xác thực email cho signup (chưa tạo user trong database)
     *
     * @param signupRequest - Thông tin signup
     * @throws AppException - Khi có lỗi trong quá trình tạo
     */
    @Transactional
    public void createAndSendVerificationForSignup(SignupRequest signupRequest) {
        try {
            // Kiểm tra rate limiting
            checkRateLimit(signupRequest.getEmail());

            // Xóa các verification cũ chưa verify của email này
            emailVerificationRepository.deleteUnverifiedByEmail(signupRequest.getEmail());

            // Tạo mã xác thực mới
            String verificationCode = verificationCodeService.generateValidCode();

            // Tạo EmailVerification entity với thông tin tạm thời
            EmailVerification emailVerification = new EmailVerification();
            emailVerification.setEmail(signupRequest.getEmail());
            emailVerification.setUsername(signupRequest.getUsername());
            emailVerification.setVerificationCode(verificationCode);
            emailVerification.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // 5 phút
            emailVerification.setCreatedAt(LocalDateTime.now());
            emailVerification.setAttempts(0);
            emailVerification.setVerified(false);
            // Không set user vì chưa tạo user trong database
            
            // Hash password trước khi lưu
            String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());
            emailVerification.setPasswordHash(hashedPassword);
            
            // Set role từ signup request
            emailVerification.setUserRole(signupRequest.getRole() != null ? signupRequest.getRole() : UserRole.STUDENT);

            // Lưu vào database
            emailVerificationRepository.save(emailVerification);

            // Gửi email
            emailService.sendVerificationEmail(
                    signupRequest.getEmail(),
                    verificationCode,
                    signupRequest.getUsername() // Sử dụng username thực tế
            );

            log.info("Verification code created and sent for signup: {} ({})", signupRequest.getUsername(), signupRequest.getEmail());

        } catch (Exception e) {
            log.error("Failed to create verification for signup: {}", e.getMessage());
            throw new AppException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Tạo và gửi email reset password
     * 
     * @param user - User cần reset password
     * @throws AppException - Khi có lỗi trong quá trình tạo
     */
    @Transactional
    public void createAndSendPasswordResetEmail(User user) {
        try {
            // Kiểm tra rate limiting
            checkRateLimit(user.getEmail());

            // Xóa các password reset token cũ của user này
            emailVerificationRepository.deletePasswordResetTokensByUserId(user.getId());

            // Tạo reset token
            String resetToken = verificationCodeService.generateValidCode();

            // Tạo EmailVerification entity cho password reset
            EmailVerification passwordReset = new EmailVerification();
            passwordReset.setEmail(user.getEmail());
            passwordReset.setUsername(user.getUsername());
            passwordReset.setVerificationCode(resetToken);
            passwordReset.setExpiresAt(LocalDateTime.now().plusMinutes(15)); // 15 phút
            passwordReset.setCreatedAt(LocalDateTime.now());
            passwordReset.setAttempts(0);
            passwordReset.setVerified(false);
            passwordReset.setUser(user);
            passwordReset.setType("PASSWORD_RESET"); // Đánh dấu loại password reset

            // Lưu vào database
            emailVerificationRepository.save(passwordReset);

            // Gửi email reset password
            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    resetToken,
                    user.getUsername()
            );

            log.info("Password reset email sent to: {} ({})", user.getUsername(), user.getEmail());

        } catch (Exception e) {
            log.error("Failed to create password reset email for user {}: {}", user.getEmail(), e.getMessage());
            throw new AppException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Validate password reset token
     * 
     * @param token - Reset token
     * @return Map<String, Object> - Thông tin từ token
     * @throws AppException - Khi token không hợp lệ
     */
    public Map<String, Object> validatePasswordResetToken(String token) {
        try {
            // Tìm password reset token
            Optional<EmailVerification> resetOpt = emailVerificationRepository
                    .findByVerificationCodeAndType(token, "PASSWORD_RESET");

            if (resetOpt.isEmpty()) {
                throw new AppException(AuthenticationErrorCode.TOKEN_INVALID);
            }

            EmailVerification reset = resetOpt.get();

            // Kiểm tra token có hết hạn không
            if (reset.isExpired()) {
                emailVerificationRepository.delete(reset);
                throw new AppException(AuthenticationErrorCode.TOKEN_EXPIRED);
            }

            // Kiểm tra đã verify chưa
            if (reset.isVerified()) {
                throw new AppException(AuthenticationErrorCode.TOKEN_INVALID);
            }

            // Trả về thông tin từ token
            return Map.of(
                "userId", reset.getUser().getId().toString(),
                "email", reset.getEmail(),
                "username", reset.getUsername()
            );

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to validate password reset token: {}", e.getMessage());
            throw new AppException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Xóa password reset token sau khi sử dụng
     * 
     * @param token - Token cần xóa
     */
    @Transactional
    public void deletePasswordResetToken(String token) {
        try {
            emailVerificationRepository.deleteByVerificationCodeAndType(token, "PASSWORD_RESET");
            log.info("Password reset token deleted: {}", token);
        } catch (Exception e) {
            log.error("Failed to delete password reset token: {}", e.getMessage());
        }
    }

}
