package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * EmailVerificationRepository - Repository cho EmailVerification entity
 *
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
     * Tìm EmailVerification theo email và chưa verify
     *
     * @param email - Email cần tìm
     * @return Optional<EmailVerification> - EmailVerification nếu tìm thấy
     */
    Optional<EmailVerification> findByEmailAndIsVerifiedFalse(String email);

    /**
     * Tìm EmailVerification theo email và user
     *
     * @param email - Email cần tìm
     * @param userId - ID của user
     * @return Optional<EmailVerification> - EmailVerification nếu tìm thấy
     */
    Optional<EmailVerification> findByEmailAndUserId(String email, UUID userId);

    /**
     * Tìm EmailVerification theo email, user và chưa verify
     *
     * @param email - Email cần tìm
     * @param userId - ID của user
     * @return Optional<EmailVerification> - EmailVerification nếu tìm thấy
     */
    Optional<EmailVerification> findByEmailAndUserIdAndIsVerifiedFalse(String email, UUID userId);

    /**
     * Tìm tất cả EmailVerification chưa verify của một user
     *
     * @param userId - ID của user
     * @return List<EmailVerification> - Danh sách EmailVerification
     */
    List<EmailVerification> findByUserIdAndIsVerifiedFalse(UUID userId);

    /**
     * Kiểm tra email đã được verify chưa
     *
     * @param email - Email cần kiểm tra
     * @return boolean - true nếu đã verify
     */
    @Query("SELECT COUNT(e) > 0 FROM EmailVerification e WHERE e.email = :email AND e.isVerified = true")
    boolean existsByEmailAndIsVerifiedTrue(@Param("email") String email);

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
     * Xóa tất cả EmailVerification chưa verify của một user
     *
     * @param userId - ID của user
     * @return int - Số lượng records đã xóa
     */
    @Modifying
    @Query("DELETE FROM EmailVerification e WHERE e.user.id = :userId AND e.isVerified = false")
    int deleteUnverifiedByUserId(@Param("userId") UUID userId);

    /**
     * Xóa tất cả EmailVerification chưa verify của một email
     *
     * @param email - Email cần xóa
     * @return int - Số lượng records đã xóa
     */
    @Modifying
    @Query("DELETE FROM EmailVerification e WHERE e.email = :email AND e.isVerified = false")
    int deleteUnverifiedByEmail(@Param("email") String email);

    /**
     * Tìm EmailVerification theo verification code
     *
     * @param code - Mã xác thực
     * @return Optional<EmailVerification> - EmailVerification nếu tìm thấy
     */
    Optional<EmailVerification> findByVerificationCode(String code);

    /**
     * Tìm EmailVerification theo verification code và chưa verify
     *
     * @param code - Mã xác thực
     * @return Optional<EmailVerification> - EmailVerification nếu tìm thấy
     */
    Optional<EmailVerification> findByVerificationCodeAndIsVerifiedFalse(String code);
}
