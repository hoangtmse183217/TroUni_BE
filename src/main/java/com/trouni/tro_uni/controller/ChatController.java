package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.request.chat.ChatMessageRequest;
import com.trouni.tro_uni.dto.response.chat.ChatMessageResponse;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.repository.UserRepository;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.AuthenticationErrorCode;
import com.trouni.tro_uni.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(
            Principal principal,
            @Payload ChatMessageRequest request) {
        User sender = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new AppException(AuthenticationErrorCode.USER_NOT_FOUND));
        chatService.processMessage(sender, request);
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
