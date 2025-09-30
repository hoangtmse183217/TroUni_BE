package com.trouni.tro_uni.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trouni.tro_uni.dto.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CustomAccessDeniedHandler - Xử lý authorization errors
 * <p>
 * Chức năng chính:
 * - Trả về JSON response thay vì HTML khi access denied
 * - Xử lý các trường hợp: không có quyền truy cập endpoint
 * - Đảm bảo response format nhất quán với API
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                      AccessDeniedException accessDeniedException) throws IOException {
        
        log.warn("Access denied for request: {} - {}", request.getRequestURI(), accessDeniedException.getMessage());
        
        // Tạo ApiResponse
        ApiResponse<Object> apiResponse = ApiResponse.error("ACCESS_DENIED", "Access denied");
        
        // Set response headers
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        // Write JSON response
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.getWriter().flush();
    }
}


