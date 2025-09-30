package com.trouni.tro_uni.exception.errorcode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/**
 * TokenErrorCode - Error codes cho JWT token và blacklist
 * 
 * Chức năng chính:
 * - Định nghĩa các lỗi liên quan đến JWT token
 * - Lỗi blacklist và token validation
 * - Lỗi token expiration và security
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum TokenErrorCode {
    
    // ===============================
    // JWT Token Errors
    // ===============================
    
    TOKEN_INVALID("TOKEN_001", "Invalid JWT token", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("TOKEN_002", "JWT token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_MALFORMED("TOKEN_003", "Malformed JWT token", HttpStatus.UNAUTHORIZED),
    TOKEN_UNSUPPORTED("TOKEN_004", "Unsupported JWT token", HttpStatus.UNAUTHORIZED),
    TOKEN_EMPTY("TOKEN_005", "JWT token is empty", HttpStatus.UNAUTHORIZED),
    TOKEN_SIGNATURE_INVALID("TOKEN_006", "Invalid JWT token signature", HttpStatus.UNAUTHORIZED),
    
    // ===============================
    // Blacklist Errors
    // ===============================
    
    TOKEN_BLACKLISTED("TOKEN_007", "JWT token has been blacklisted", HttpStatus.UNAUTHORIZED),
    TOKEN_ALREADY_BLACKLISTED("TOKEN_008", "JWT token is already blacklisted", HttpStatus.CONFLICT),
    BLACKLIST_FAILED("TOKEN_009", "Failed to blacklist JWT token", HttpStatus.INTERNAL_SERVER_ERROR),
    BLACKLIST_CLEANUP_FAILED("TOKEN_010", "Failed to cleanup expired blacklisted tokens", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // ===============================
    // Token Generation Errors
    // ===============================
    
    TOKEN_GENERATION_FAILED("TOKEN_011", "Failed to generate JWT token", HttpStatus.INTERNAL_SERVER_ERROR),
    TOKEN_CLAIMS_INVALID("TOKEN_012", "Invalid JWT token claims", HttpStatus.BAD_REQUEST),
    TOKEN_SECRET_INVALID("TOKEN_013", "Invalid JWT secret key", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // ===============================
    // Token Validation Errors
    // ===============================
    
    TOKEN_VALIDATION_FAILED("TOKEN_014", "JWT token validation failed", HttpStatus.UNAUTHORIZED),
    TOKEN_USER_NOT_FOUND("TOKEN_015", "User not found for JWT token", HttpStatus.UNAUTHORIZED),
    TOKEN_AUTHORITY_INVALID("TOKEN_016", "Invalid authority in JWT token", HttpStatus.FORBIDDEN),
    
    // ===============================
    // Logout Errors
    // ===============================
    
    LOGOUT_FAILED("TOKEN_017", "Logout failed", HttpStatus.INTERNAL_SERVER_ERROR),
    LOGOUT_TOKEN_MISSING("TOKEN_018", "JWT token is required for logout", HttpStatus.BAD_REQUEST),
    LOGOUT_USER_NOT_AUTHENTICATED("TOKEN_019", "User not authenticated for logout", HttpStatus.UNAUTHORIZED),
    
    // ===============================
    // Blacklist Management Errors
    // ===============================
    
    BLACKLIST_NOT_FOUND("TOKEN_020", "Blacklisted token not found", HttpStatus.NOT_FOUND),
    BLACKLIST_STATS_FAILED("TOKEN_021", "Failed to get blacklist statistics", HttpStatus.INTERNAL_SERVER_ERROR),
    BLACKLIST_CHECK_FAILED("TOKEN_022", "Failed to check token blacklist status", HttpStatus.INTERNAL_SERVER_ERROR);
    
    String code;
    String message;
    HttpStatusCode statusCode;
}
