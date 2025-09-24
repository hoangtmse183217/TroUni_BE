package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * BlacklistedTokenRepository - Repository cho BlacklistedToken entity
 * 
 * Chức năng chính:
 * - Tìm kiếm token đã bị blacklist
 * - Xóa các token đã hết hạn
 * - Quản lý blacklist tokens
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, UUID> {
    
    /**
     * Kiểm tra token có bị blacklist không
     * 
     * @param tokenHash - Hash của token cần kiểm tra
     * @return boolean - true nếu token bị blacklist
     */
    boolean existsByTokenHash(String tokenHash);
    
    /**
     * Tìm blacklisted token theo hash
     * 
     * @param tokenHash - Hash của token
     * @return Optional<BlacklistedToken> - Token nếu tìm thấy
     */
    Optional<BlacklistedToken> findByTokenHash(String tokenHash);
    
    /**
     * Xóa tất cả token đã hết hạn
     * 
     * @param currentTime - Thời gian hiện tại
     * @return int - Số lượng token đã xóa
     */
    @Modifying
    @Query("DELETE FROM BlacklistedToken bt WHERE bt.expiresAt < :currentTime")
    int deleteExpiredTokens(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Đếm số lượng token đã hết hạn
     * 
     * @param currentTime - Thời gian hiện tại
     * @return long - Số lượng token đã hết hạn
     */
    @Query("SELECT COUNT(bt) FROM BlacklistedToken bt WHERE bt.expiresAt < :currentTime")
    long countExpiredTokens(@Param("currentTime") LocalDateTime currentTime);
}

