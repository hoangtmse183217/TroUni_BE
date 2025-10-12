package com.trouni.tro_uni.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocketConfig - Configuration for WebSocket message broker.
 *
 * @author TroUni Team
 * @version 1.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Registers STOMP endpoints, allowing clients to connect to the WebSocket server.
     * <p>
     * The endpoint "/ws" is configured for WebSocket connections.
     * SockJS is enabled as a fallback for browsers that don't support WebSocket.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint for clients to connect to
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
//                .withSockJS();
    }

    /**
     * Configures the message broker.
     * <p>
     * - "/app" is the prefix for messages bound for methods annotated with @MessageMapping.
     * - "/topic" is the prefix for topics that clients can subscribe to.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix for application-bound messages (e.g., from client to server)
        registry.setApplicationDestinationPrefixes("/app");
        // Prefix for topics that clients subscribe to (e.g., from server to client)
        registry.enableSimpleBroker("/topic");
        // Use a specific prefix for user-specific destinations
        registry.setUserDestinationPrefix("/user");
    }
}
