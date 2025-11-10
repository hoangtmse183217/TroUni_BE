package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.request.chat.ChatMessageRequest;
import com.trouni.tro_uni.dto.request.chat.CreateChatRoomRequest;
import com.trouni.tro_uni.dto.response.chat.ChatMessageResponse;
import com.trouni.tro_uni.dto.response.chat.ChatRoomResponse;
import com.trouni.tro_uni.entity.ChatRoom;
import com.trouni.tro_uni.entity.Message;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.AuthenticationErrorCode;
import com.trouni.tro_uni.exception.errorcode.GeneralErrorCode;
import com.trouni.tro_uni.repository.ChatRoomRepository;
import com.trouni.tro_uni.repository.MessageRepository;
import com.trouni.tro_uni.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate; // Used to send messages to clients

    /**
     * Creates a new chat room between the current user and a recipient.
     *
     * @param sender  The user initiating the chat room creation.
     * @param request The request containing the recipient's ID.
     * @return ChatRoomResponse - Details of the created or existing chat room.
     */
    @Transactional
    public ChatRoomResponse createChatRoom(User sender, CreateChatRoomRequest request) {
        log.info("Attempting to create/retrieve chat room between senderId={} and recipientId={}", sender.getId(), request.getRecipientId());

        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new AppException(AuthenticationErrorCode.PROFILE_NOT_FOUND));

        ChatRoom chatRoom = getOrCreateChatRoom(sender, recipient);

        log.info("Chat room created/retrieved with ID: {}", chatRoom.getId());
        return ChatRoomResponse.fromChatRoom(chatRoom);
    }

    /**
     * Processes an incoming chat message, saves it, and sends it to the recipient.
     *
     * @param sender  The user sending the message.
     * @param request The message payload containing chatRoomId and content.
     */
    @Transactional
    public void processMessage(User sender, ChatMessageRequest request) {
        log.info("📨 Processing message from senderId={} to chatRoomId={}", sender.getId(), request.getChatRoomId());

        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new AppException(GeneralErrorCode.RESOURCE_NOT_FOUND, "Chat room not found"));
        log.info("✅ ChatRoom found: {} (ID: {})", chatRoom.getId(), chatRoom.getId());

        User recipient = chatRoom.getParticipants().stream()
                .filter(p -> !p.getId().equals(sender.getId()))
                .findFirst()
                .orElseThrow(() -> new AppException(AuthenticationErrorCode.PROFILE_NOT_FOUND));
        log.info("✅ Recipient found: {} (ID: {})", recipient.getUsername(), recipient.getId());

        // Create and save the message
        Message message = new Message(null, chatRoom, sender, request.getContent(), false, LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);
        log.info("Message from {} to {} saved in chat room {}", sender.getUsername(), recipient.getUsername(), chatRoom.getId());

        // Convert to DTO to send to the client
        ChatMessageResponse response = ChatMessageResponse.fromMessage(savedMessage);

        // Send the message to the recipient's private topic
        // The recipient must be subscribed to /topic/user/{userId}
        try {
            messagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoom.getId().toString(), response);
            log.info("📨 Sent to chatRoom {} on /topic/chatRoom/{}", chatRoom.getId(), chatRoom.getId());
        } catch (Exception e) {
            log.error("❌ Failed to send message via WebSocket: {}", e.getMessage(), e);
        }


    }



    /**
     * Retrieves or creates a chat room for two users.
     *
     * @param sender    The first user.
     * @param recipient The second user.
     * @return The existing or newly created ChatRoom.
     */
    private ChatRoom getOrCreateChatRoom(User sender, User recipient) {
        return chatRoomRepository.findChatRoomByParticipants(sender, recipient)
                .orElseGet(() -> {
                    ChatRoom newChatRoom = ChatRoom.builder()
                            .participants(Arrays.asList(sender, recipient))
                            .build();
                    log.info("Creating new chat room for users {} and {}", sender.getUsername(), recipient.getUsername());
                    return chatRoomRepository.save(newChatRoom);
                });
    }

    /**
     * Retrieves the message history for a given chat room.
     *
     * @param roomId The ID of the chat room.
     * @return A list of chat messages.
     */
    public List<ChatMessageResponse> getChatHistory(UUID roomId) {
        List<Message> messages = messageRepository.findByChatRoomIdOrderBySentAtAsc(roomId);
        return messages.stream()
                .map(ChatMessageResponse::fromMessage)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a chat room by its ID.
     *
     * @param chatRoomId The ID of the chat room.
     * @return The ChatRoom entity.
     * @throws AppException if the chat room is not found.
     */
    public ChatRoom getChatRoomById(UUID chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new AppException(GeneralErrorCode.RESOURCE_NOT_FOUND, "Chat room not found"));
    }

    /**
     * Retrieves all chat rooms that a specific user is a participant of.
     *
     * @param userId The ID of the user.
     * @return A list of ChatRoomResponse for the user.
     */
    public List<ChatRoomResponse> getChatRoomsByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(AuthenticationErrorCode.USER_NOT_FOUND));

        List<ChatRoom> chatRooms = chatRoomRepository.findByParticipantsContains(user);

        return chatRooms.stream()
                .map(ChatRoomResponse::fromChatRoom)
                .collect(Collectors.toList());
    }
}
