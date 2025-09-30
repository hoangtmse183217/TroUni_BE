package com.trouni.tro_uni.util;

import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.TokenErrorCode;
import com.trouni.tro_uni.service.TokenBlacklistService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * JwtUtil - Utility class xử lý JWT token
 * 
 * Chức năng chính:
 * - Tạo JWT token từ UserDetails
 * - Validate JWT token
 * - Extract thông tin từ token (username, expiration, claims)
 * - Kiểm tra token có hết hạn không
 * - Parse và decode JWT token
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    // Dependencies injection
    private final TokenBlacklistService tokenBlacklistService;
    
    // JWT configuration từ application.properties
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs;
    
    /**
     * Extract username từ JWT token
     * 
     * Username được lưu trong "sub" (subject) claim của JWT
     * 
     * @param token - JWT token
     * @return String - Username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extract expiration date từ JWT token
     * 
     * @param token - JWT token
     * @return Date - Expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extract claim bất kỳ từ JWT token
     * 
     * Generic method để extract bất kỳ claim nào từ token
     * 
     * @param token - JWT token
     * @param claimsResolver - Function để extract claim
     * @return T - Claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extract tất cả claims từ JWT token
     * 
     * @param token - JWT token
     * @return Claims - Tất cả claims trong token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Kiểm tra token có hết hạn không
     * 
     * @param token - JWT token
     * @return Boolean - true nếu token đã hết hạn
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Tạo JWT token từ UserDetails
     * 
     * @param userDetails - UserDetails object
     * @return String - JWT token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    
    /**
     * Tạo JWT token với claims và subject
     * 
     * @param claims - Claims để thêm vào token
     * @param subject - Subject (thường là username)
     * @return String - JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Validate token với UserDetails
     * 
     * Kiểm tra:
     * - Username trong token có khớp với UserDetails không
     * - Token có hết hạn không
     * 
     * @param token - JWT token
     * @param userDetails - UserDetails để so sánh
     * @return Boolean - true nếu token hợp lệ
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    /**
     * Validate JWT token (chỉ kiểm tra signature và expiration)
     * 
     * Không cần UserDetails, chỉ kiểm tra:
     * - Token có đúng format không
     * - Signature có hợp lệ không
     * - Token có hết hạn không
     * - Token có bị blacklist không
     * 
     * @param authToken - JWT token
     * @return boolean - true nếu token hợp lệ
     * @throws AppException - Khi token không hợp lệ hoặc bị blacklist
     */
    public boolean validateJwtToken(String authToken) {
        try {
            // Kiểm tra token có bị blacklist không
            if (tokenBlacklistService.isTokenBlacklisted(authToken)) {
                logger.warn("Token is blacklisted");
                throw new AppException(TokenErrorCode.TOKEN_BLACKLISTED);
            }
            
            Jwts.parserBuilder().setSigningKey(getSignKey()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new AppException(TokenErrorCode.TOKEN_MALFORMED);
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
            throw new AppException(TokenErrorCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            throw new AppException(TokenErrorCode.TOKEN_UNSUPPORTED);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            throw new AppException(TokenErrorCode.TOKEN_EMPTY);
        } catch (SecurityException e) {
            logger.error("Invalid JWT token signature: {}", e.getMessage());
            throw new AppException(TokenErrorCode.TOKEN_SIGNATURE_INVALID);
        } catch (AppException e) {
            // Re-throw AppException từ blacklist check
            throw e;
        } catch (Exception e) {
            logger.error("JWT token validation failed: {}", e.getMessage());
            throw new AppException(TokenErrorCode.TOKEN_VALIDATION_FAILED);
        }
    }
    
    /**
     * Blacklist JWT token
     * 
     * Thêm token vào blacklist để ngăn chặn sử dụng sau khi logout
     * 
     * @param token - JWT token cần blacklist
     * @param userId - ID của user (optional)
     * @throws AppException - Khi không thể blacklist token
     */
    public void blacklistToken(String token, UUID userId) {
        try {
            // Kiểm tra token có hợp lệ không trước khi blacklist
            if (token == null || token.trim().isEmpty()) {
                throw new AppException(TokenErrorCode.LOGOUT_TOKEN_MISSING);
            }
            
            Date expiration = extractExpiration(token);
            LocalDateTime expiresAt = expiration.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
            
            tokenBlacklistService.blacklistToken(token, expiresAt, userId);
            logger.info("Token blacklisted for user: {}", userId);
        } catch (AppException e) {
            // Re-throw AppException
            throw e;
        } catch (Exception e) {
            logger.error("Error blacklisting token for user {}: {}", userId, e.getMessage());
            throw new AppException(TokenErrorCode.BLACKLIST_FAILED);
        }
    }
    
    /**
     * Tạo signing key từ JWT secret
     * 
     * Convert base64 encoded secret thành Key object
     * để sử dụng cho signing và verification
     * 
     * @return Key - Signing key
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}