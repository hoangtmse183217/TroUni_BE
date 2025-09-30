package com.trouni.tro_uni.security;

import com.trouni.tro_uni.service.CustomUserDetailsService;
import com.trouni.tro_uni.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter - Filter xử lý JWT authentication

 * Chức năng chính:
 * - Intercept tất cả HTTP requests
 * - Extract JWT token từ Authorization header
 * - Validate JWT token
 * - Set authentication vào SecurityContext
 * - Cho phép request tiếp tục nếu token hợp lệ
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Dependencies injection
    private final JwtUtil jwtUtil;                     // Utility xử lý JWT
    private final CustomUserDetailsService userDetailsService; // Service load user details



    /**
     * Xử lý filter cho mỗi request

     * Quy trình:
     * 1. Extract JWT token từ Authorization header
     * 2. Validate token nếu có
     * 3. Load user details từ database
     * 4. Set authentication vào SecurityContext
     * 5. Cho phép request tiếp tục
     * 
     * @param request - HttpServletRequest
     * @param response - HttpServletResponse
     * @param filterChain - FilterChain để tiếp tục
     * @throws ServletException - Khi có lỗi servlet
     * @throws IOException - Khi có lỗi I/O
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Bước 1: Extract JWT token từ request
            String jwt = parseJwt(request);
            
            // Bước 2: Validate token nếu có
            if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
                // Bước 3: Extract username từ token
                String username = jwtUtil.extractUsername(jwt);

                // Bước 4: Load user details từ database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Bước 5: Tạo authentication object
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null, // credentials = null vì đã xác thực qua JWT
                                userDetails.getAuthorities());
                
                // Bước 6: Set additional details
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Bước 7: Set authentication vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Log lỗi nhưng không block request
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        // Bước 8: Cho phép request tiếp tục
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token từ Authorization header

     * Format header: "Authorization: Bearer <JWT_TOKEN>"
     * 
     * @param request - HttpServletRequest
     * @return String - JWT token hoặc null nếu không tìm thấy
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        // Kiểm tra header có tồn tại và bắt đầu bằng "Bearer "
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            // Trả về token (bỏ qua "Bearer " prefix)
            return headerAuth.substring(7);
        }

        return null;
    }
}
