package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.request.chat.ChatMessageRequest;
import com.trouni.tro_uni.dto.response.chat.ChatMessageResponse;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

/**
 * ChatController - Handles real-time chat messages and chat history retrieval.
 *
 * @author TroUni Team
 * @version 1.0
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    /**
     * Handles incoming real-time chat messages sent via WebSocket.
     * The destination for this mapping is "/app/chat.sendMessage".
     *
     * @param currentUser The authenticated user sending the message.
     * @param request     The chat message payload.
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(
            @AuthenticationPrincipal User currentUser,
            @Payload ChatMessageRequest request) {
        chatService.processMessage(currentUser, request);
    }

    /**
     * REST endpoint to retrieve the message history for a specific chat room.
     *
     * @param roomId The ID of the chat room.
     * @return A list of past messages in the chat room.
     */
    @GetMapping("/{roomId}/history")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getChatHistory(@PathVariable UUID roomId) {
        List<ChatMessageResponse> history = chatService.getChatHistory(roomId);
        return ResponseEntity.ok(ApiResponse.success("Chat history retrieved successfully!", history));
    }
}
