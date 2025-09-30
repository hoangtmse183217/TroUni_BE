package com.trouni.tro_uni.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * BlacklistedToken Entity - Lưu trữ các JWT token đã bị blacklist
 * 
 * Chức năng chính:
 * - Lưu trữ token đã logout để ngăn chặn sử dụng lại
 * - Tự động xóa token hết hạn để tiết kiệm không gian
 * - Hỗ trợ logout an toàn với JWT stateless
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Entity
@Table(name = "blacklisted_tokens", indexes = {
    @Index(name = "idx_token_hash", columnList = "token_hash"),
    @Index(name = "idx_expires_at", columnList = "expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Hash của JWT token để bảo mật
     * Không lưu token gốc để tránh rò rỉ thông tin
     */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;
    
    /**
     * Thời gian token hết hạn
     * Dùng để tự động cleanup các token đã hết hạn
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    /**
     * Thời gian token bị blacklist
     */
    @Column(name = "blacklisted_at", nullable = false)
    private LocalDateTime blacklistedAt = LocalDateTime.now();
    
    /**
     * Lý do blacklist (logout, security, etc.)
     */
    @Column(name = "reason", length = 100)
    private String reason = "logout";
    
    /**
     * User ID của token bị blacklist (optional)
     */
    @Column(name = "user_id")
    private UUID userId;
    
    /**
     * Constructor để tạo blacklisted token
     */
    public BlacklistedToken(String tokenHash, LocalDateTime expiresAt, UUID userId) {
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.userId = userId;
        this.blacklistedAt = LocalDateTime.now();
        this.reason = "logout";
    }
}

