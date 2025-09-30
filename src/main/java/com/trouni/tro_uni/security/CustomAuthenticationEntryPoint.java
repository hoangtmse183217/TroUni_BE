package com.trouni.tro_uni.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trouni.tro_uni.dto.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CustomAuthenticationEntryPoint - Xử lý authentication errors
 * <p>
 * Chức năng chính:
 * - Trả về JSON response thay vì HTML khi authentication fails
 * - Xử lý các trường hợp: token rỗng, token sai, token hết hạn
 * - Đảm bảo response format nhất quán với API
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, 
                        AuthenticationException authException) throws IOException {
        
        log.warn("Authentication failed for request: {} - {}", request.getRequestURI(), authException.getMessage());
        
        // Xác định error code dựa trên loại exception
        String errorCode = determineErrorCode(authException);
        String errorMessage = determineErrorMessage(authException);
        
        // Tạo ApiResponse
        ApiResponse<Object> apiResponse = ApiResponse.error(errorCode, errorMessage);
        
        // Set response headers
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        // Write JSON response
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.getWriter().flush();
    }
    
    /**
     * Xác định error code dựa trên loại exception
     * <p>
     * @param authException - Authentication exception
     * @return String - Error code
     */
    private String determineErrorCode(AuthenticationException authException) {
        String message = authException.getMessage();
        
        if (message != null) {
            if (message.contains("JWT") || message.contains("token")) {
                return "TOKEN_INVALID";
            }
            if (message.contains("expired")) {
                return "TOKEN_EXPIRED";
            }
        }
        
        return "UNAUTHENTICATED";
    }
    
    /**
     * Xác định error message dựa trên loại exception
     * <p>
     * @param authException - Authentication exception
     * @return String - Error message
     */
    private String determineErrorMessage(AuthenticationException authException) {
        String message = authException.getMessage();
        
        if (message != null) {
            if (message.contains("JWT") || message.contains("token")) {
                return "Invalid or malformed token";
            }
            if (message.contains("expired")) {
                return "Token has expired";
            }
        }
        
        return "Authentication required";
    }
}


