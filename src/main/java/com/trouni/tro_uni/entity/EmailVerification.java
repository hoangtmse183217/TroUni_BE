package com.trouni.tro_uni.entity;

import com.trouni.tro_uni.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * EmailVerification Entity - Entity lưu trữ mã xác thực email
 * <p>
 * Chức năng chính:
 * - Lưu mã xác thực 6 số cho email
 * - Quản lý thời hạn hết hạn (5 phút)
 * - Liên kết với User entity
 * - Tracking trạng thái verification
 * <p>
 * @author TroUni Team
 * @version 1.0
 */
@Entity
@Table(name = "email_verifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification {

    // ===============================
    // Primary Key
    // ===============================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ===============================
    // Basic Fields
    // ===============================

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "username", nullable = false, length = 50)
    private String username; // Username từ signup

    @Column(name = "verification_code", nullable = false, length = 6)
    private String verificationCode; // Mã 6 số

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "attempts", nullable = false)
    private int attempts = 0; // Số lần thử nhập sai

    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts = 3; // Tối đa 3 lần thử

    // ===============================
    // Relationship Fields
    // ===============================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Cho phép NULL cho trường hợp signup
    private User user;

    // ===============================
    // Time Fields
    // ===============================

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "password_hash")
    private String passwordHash; // Password đã hash

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole = UserRole.STUDENT; // Role của user

    @Column(name = "type", nullable = false, length = 50)
    private String type = "EMAIL_VERIFICATION"; // Loại verification (EMAIL_VERIFICATION, PASSWORD_RESET)

    // ===============================
    // Constructors
    // ===============================

    // ===============================
    // Business Methods
    // ===============================

    /**
     * Kiểm tra mã xác thực có đúng không
     * <p>
     * @param code - Mã xác thực cần kiểm tra
     * @return boolean - true nếu đúng
     */
    public boolean verifyCode(String code) {
        if (isExpired() || isMaxAttemptsReached()) {
            return false;
        }

        attempts++;
        if (verificationCode.equals(code)) {
            isVerified = true;
            verifiedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }

    /**
     * Kiểm tra mã có hết hạn không
     * <p>
     * @return boolean - true nếu đã hết hạn
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Kiểm tra đã thử quá số lần cho phép chưa
     * <p>
     * @return boolean - true nếu đã thử quá số lần
     */
    public boolean isMaxAttemptsReached() {
        return attempts >= maxAttempts;
    }

    /**
     * Kiểm tra có thể thử verify không
     * <p>
     * @return boolean - true nếu có thể thử
     */
    public boolean canAttempt() {
        return !isVerified && !isExpired() && !isMaxAttemptsReached();
    }

    /**
     * Tạo mã verification mới với thời hạn 5 phút
     * <p>
     * @param newCode - Mã mới
     */
    public void generateNewCode(String newCode) {
        this.verificationCode = newCode;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(5);
        this.attempts = 0;
        this.isVerified = false;
        this.verifiedAt = null;
    }
}
