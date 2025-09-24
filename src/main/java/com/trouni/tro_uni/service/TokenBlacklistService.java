package com.trouni.tro_uni.service;

import com.trouni.tro_uni.entity.BlacklistedToken;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.TokenErrorCode;
import com.trouni.tro_uni.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * TokenBlacklistService - Service quản lý JWT token blacklist
 * 
 * Chức năng chính:
 * - Thêm token vào blacklist khi logout
 * - Kiểm tra token có bị blacklist không
 * - Tự động cleanup token đã hết hạn
 * - Tạo hash an toàn cho token
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    
    /**
     * Thêm token vào blacklist
     * 
     * @param token - JWT token cần blacklist
     * @param expiresAt - Thời gian token hết hạn
     * @param userId - ID của user (optional)
     * @throws AppException - Khi không thể blacklist token
     */
    @Transactional
    public void blacklistToken(String token, LocalDateTime expiresAt, UUID userId) {
        try {
            // Validate input
            if (token == null || token.trim().isEmpty()) {
                throw new AppException(TokenErrorCode.LOGOUT_TOKEN_MISSING);
            }
            
            if (expiresAt == null) {
                throw new AppException(TokenErrorCode.TOKEN_EXPIRED);
            }
            
            String tokenHash = generateTokenHash(token);
            
            // Kiểm tra token đã bị blacklist chưa
            if (blacklistedTokenRepository.existsByTokenHash(tokenHash)) {
                log.warn("Token already blacklisted for user: {}", userId);
                throw new AppException(TokenErrorCode.TOKEN_ALREADY_BLACKLISTED);
            }
            
            BlacklistedToken blacklistedToken = new BlacklistedToken(tokenHash, expiresAt, userId);
            blacklistedTokenRepository.save(blacklistedToken);
            
            log.info("Token blacklisted successfully for user: {}", userId);
        } catch (AppException e) {
            // Re-throw AppException
            throw e;
        } catch (Exception e) {
            log.error("Error blacklisting token for user {}: {}", userId, e.getMessage());
            throw new AppException(TokenErrorCode.BLACKLIST_FAILED);
        }
    }
    
    /**
     * Kiểm tra token có bị blacklist không
     * 
     * @param token - JWT token cần kiểm tra
     * @return boolean - true nếu token bị blacklist
     * @throws AppException - Khi có lỗi kiểm tra blacklist
     */
    public boolean isTokenBlacklisted(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new AppException(TokenErrorCode.TOKEN_EMPTY);
            }
            
            String tokenHash = generateTokenHash(token);
            return blacklistedTokenRepository.existsByTokenHash(tokenHash);
        } catch (AppException e) {
            // Re-throw AppException
            throw e;
        } catch (Exception e) {
            log.error("Error checking token blacklist: {}", e.getMessage());
            throw new AppException(TokenErrorCode.BLACKLIST_CHECK_FAILED);
        }
    }
    
    /**
     * Lấy thông tin blacklisted token
     * 
     * @param token - JWT token
     * @return Optional<BlacklistedToken> - Thông tin token nếu tìm thấy
     */
    public Optional<BlacklistedToken> getBlacklistedToken(String token) {
        try {
            String tokenHash = generateTokenHash(token);
            return blacklistedTokenRepository.findByTokenHash(tokenHash);
        } catch (Exception e) {
            log.error("Error getting blacklisted token: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Xóa token khỏi blacklist (nếu cần)
     * 
     * @param token - JWT token cần xóa
     */
    @Transactional
    public void removeFromBlacklist(String token) {
        try {
            String tokenHash = generateTokenHash(token);
            blacklistedTokenRepository.findByTokenHash(tokenHash)
                    .ifPresent(blacklistedTokenRepository::delete);
            log.info("Token removed from blacklist");
        } catch (Exception e) {
            log.error("Error removing token from blacklist: {}", e.getMessage());
        }
    }
    
    /**
     * Tạo hash SHA-256 cho token để bảo mật
     * 
     * @param token - JWT token gốc
     * @return String - Hash của token
     */
    private String generateTokenHash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("Hash generation failed", e);
        }
    }
    
    /**
     * Tự động cleanup các token đã hết hạn
     * Chạy mỗi 30 phút
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            LocalDateTime now = LocalDateTime.now();
            long expiredCount = blacklistedTokenRepository.countExpiredTokens(now);
            
            if (expiredCount > 0) {
                int deletedCount = blacklistedTokenRepository.deleteExpiredTokens(now);
                log.info("Cleaned up {} expired blacklisted tokens", deletedCount);
            }
        } catch (Exception e) {
            log.error("Error during token cleanup: {}", e.getMessage());
            // Không throw exception trong scheduled method để tránh crash app
        }
    }
    
    /**
     * Lấy thống kê blacklist
     * 
     * @return Map<String, Object> - Thống kê blacklist
     */
    public long getBlacklistCount() {
        return blacklistedTokenRepository.count();
    }
    
    /**
     * Lấy số lượng token đã hết hạn
     * 
     * @return long - Số lượng token đã hết hạn
     */
    public long getExpiredTokenCount() {
        return blacklistedTokenRepository.countExpiredTokens(LocalDateTime.now());
    }
}
