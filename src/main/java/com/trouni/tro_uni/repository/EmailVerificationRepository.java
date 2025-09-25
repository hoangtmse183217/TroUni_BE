package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * EmailVerificationRepository - Repository cho EmailVerification entity

 * Chức năng chính:
 * - CRUD operations cho EmailVerification
 * - Tìm kiếm theo email và user
 * - Cleanup expired verifications
 * - Quản lý verification attempts
 *
 * @author TroUni Team
 * @version 1.0
 */
@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {

    /**
     * Tìm EmailVerification theo email
     *
     * @param email - Email cần tìm
     * @return Optional<EmailVerification> - EmailVerification nếu tìm thấy
     */
    Optional<EmailVerification> findByEmail(String email);

    /**
     * Đếm số lần thử verify của một email trong 1 giờ
     *
     * @param email - Email cần đếm
     * @param oneHourAgo - Thời điểm 1 giờ trước
     * @return long - Số lần thử
     */
    @Query("SELECT COUNT(e) FROM EmailVerification e WHERE e.email = :email AND e.createdAt >= :oneHourAgo")
    long countAttemptsByEmailSince(@Param("email") String email, @Param("oneHourAgo") LocalDateTime oneHourAgo);

    /**
     * Xóa tất cả EmailVerification đã hết hạn
     *
     * @param now - Thời điểm hiện tại
     * @return int - Số lượng records đã xóa
     */
    @Modifying
    @Query("DELETE FROM EmailVerification e WHERE e.expiresAt < :now")
    int deleteExpiredVerifications(@Param("now") LocalDateTime now);

    /**
     * Xóa tất cả EmailVerification chưa verify của một email
     *
     * @param email - Email cần xóa
     */
    @Modifying
    @Query("DELETE FROM EmailVerification e WHERE e.email = :email AND e.isVerified = false")
    void deleteUnverifiedByEmail(@Param("email") String email);

    /**
     * Tìm EmailVerification theo verification code và type
     *
     * @param code - Mã xác thực
     * @param type - Loại verification (PASSWORD_RESET, EMAIL_VERIFICATION)
     * @return Optional<EmailVerification> - EmailVerification nếu tìm thấy
     */
    Optional<EmailVerification> findByVerificationCodeAndType(String code, String type);

    /**
     * Xóa password reset tokens của một user
     *
     * @param userId - ID của user
     */
    @Modifying
    @Query("DELETE FROM EmailVerification e WHERE e.user.id = :userId AND e.type = 'PASSWORD_RESET'")
    void deletePasswordResetTokensByUserId(@Param("userId") UUID userId);

    /**
     * Xóa EmailVerification theo verification code và type
     *
     * @param code - Mã xác thực
     * @param type - Loại verification
     */
    @Modifying
    @Query("DELETE FROM EmailVerification e WHERE e.verificationCode = :code AND e.type = :type")
    void deleteByVerificationCodeAndType(@Param("code") String code, @Param("type") String type);
}
