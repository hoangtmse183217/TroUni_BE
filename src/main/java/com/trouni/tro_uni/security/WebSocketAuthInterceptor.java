package com.trouni.tro_uni.security;

import com.trouni.tro_uni.service.CustomUserDetailsService;
import com.trouni.tro_uni.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Interceptor xác thực JWT token cho kết nối WebSocket (STOMP).
 * Khi client gửi token qua header Authorization (Bearer token),
 * interceptor này sẽ validate và gắn thông tin user vào session.
 *
 * @author TroUni Team
 * @version 2.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        // Chỉ xử lý khi client gửi lệnh CONNECT
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = extractTokenFromHeader(accessor);

            if (!StringUtils.hasText(token)) {
                log.warn("❌ WebSocket CONNECT failed: No token found in headers.");
                // Ném exception thay vì trả về null để client nhận được thông báo lỗi rõ ràng hơn
                throw new IllegalArgumentException("Authentication token is required.");
            }

            try {
                if (jwtUtil.validateJwtToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    accessor.setUser(authentication);
                    // Không set SecurityContextHolder ở đây vì nó là thread-local và không an toàn
                    // cho các message xử lý bất đồng bộ. accessor.setUser là đủ cho Spring Security WebSocket.
                    log.debug("✅ WebSocket authenticated for user: {}", username);
                } else {
                    // validateJwtToken đã ném exception, nhưng để chắc chắn
                    log.warn("⚠️ WebSocket CONNECT failed: Invalid token for user.");
                    throw new IllegalArgumentException("Invalid token.");
                }
            } catch (Exception e) {
                log.error("❌ WebSocket authentication failed: {}", e.getMessage());
                // Ném lại exception để từ chối kết nối
                throw new IllegalArgumentException("Authentication failed: " + e.getMessage(), e);
            }
        }
        return message;
    }

    /**
     * Lấy JWT token từ STOMP header.
     * Phương thức này tìm token trong cả "native" headers (của HTTP handshake)
     * và trong headers của STOMP CONNECT frame.
     */
    private String extractTokenFromHeader(StompHeaderAccessor accessor) {
        final String authHeaderName = "Authorization";

        // 1. Ưu tiên tìm trong native headers (gửi qua HTTP handshake)
        String nativeAuthHeader = accessor.getFirstNativeHeader(authHeaderName);
        if (StringUtils.hasText(nativeAuthHeader) && nativeAuthHeader.startsWith("Bearer ")) {
            return nativeAuthHeader.substring(7);
        }

        // 2. Nếu không có, tìm trong STOMP headers (gửi trong CONNECT frame)
        // stomp.js client gửi header trong connect() frame tại đây
        Object stompAuthHeader = accessor.getHeader(authHeaderName);
        if (stompAuthHeader instanceof String) {
            String headerValue = (String) stompAuthHeader;
            if (headerValue.startsWith("Bearer ")) {
                return headerValue.substring(7);
            }
            return headerValue;
        }

        return null;
    }
}