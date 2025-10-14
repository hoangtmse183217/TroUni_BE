package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.request.chat.ChatMessageRequest;
import com.trouni.tro_uni.dto.request.chat.CreateChatRoomRequest;
import com.trouni.tro_uni.dto.response.chat.ChatMessageResponse;
import com.trouni.tro_uni.dto.response.chat.ChatRoomResponse;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.AuthenticationErrorCode;
import com.trouni.tro_uni.repository.UserRepository;
import com.trouni.tro_uni.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * REST endpoint to create a chat room between the current user and a recipient.
     * If a chat room already exists, it returns the existing one.
     */
    @PostMapping("/room")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createChatRoom(
            @AuthenticationPrincipal User currentUser,
            @RequestBody CreateChatRoomRequest request) {
        log.info("Received request to create chat room for senderId={} and recipientId={}", currentUser.getId(), request.getRecipientId());
        ChatRoomResponse chatRoom = chatService.createChatRoom(currentUser, request);
        return ResponseEntity.ok(ApiResponse.success("Chat room created/retrieved successfully!", chatRoom));
    }

      /**
     * Handles incoming real-time chat messages sent via WebSocket.
     * The destination for this mapping is "/app/chat.sendMessage".
     *
     * @param principal The authenticated user sending the message.
     * @param request   The chat message payload.
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(
            Principal principal,
            @Payload ChatMessageRequest request) {
            User sender = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new AppException(AuthenticationErrorCode.USER_NOT_FOUND));
        chatService.processMessage(sender, request);
    }



    /**
     * REST endpoint để lấy lịch sử chat theo room
     */
    @GetMapping("/{roomId}/history")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getChatHistory(@PathVariable UUID roomId) {
        List<ChatMessageResponse> history = chatService.getChatHistory(roomId);
        return ResponseEntity.ok(ApiResponse.success("Chat history retrieved successfully!", history));
    }

    /**
     * REST endpoint to get all chat rooms for a specific user.
     */
    @GetMapping("/user/{userId}/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getChatRoomsByUserId(@PathVariable UUID userId) {
        List<ChatRoomResponse> chatRooms = chatService.getChatRoomsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Chat rooms retrieved successfully!", chatRooms));
    }
}
