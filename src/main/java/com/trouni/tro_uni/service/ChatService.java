package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.request.chat.ChatMessageRequest;
import com.trouni.tro_uni.dto.response.chat.ChatMessageResponse;
import com.trouni.tro_uni.entity.ChatRoom;
import com.trouni.tro_uni.entity.Message;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.UserErrorCode;
import com.trouni.tro_uni.repository.ChatRoomRepository;
import com.trouni.tro_uni.repository.MessageRepository;
import com.trouni.tro_uni.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Processes an incoming chat message, saves it, and sends it to the recipient.
     *
     * @param sender  The user sending the message.
     * @param request The message payload containing recipient ID and content.
     */
    @Transactional
    public void processMessage(User sender, ChatMessageRequest request) {
        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new AppException(UserErrorCode.PROFILE_NOT_FOUND));

        // Get or create a chat room between the sender and recipient
        ChatRoom chatRoom = getOrCreateChatRoom(sender, recipient);

        // Create and save the message
        Message message = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(request.getContent())
                .build();
        Message savedMessage = messageRepository.save(message);

        log.info("Message from {} to {} saved in chat room {}", sender.getUsername(), recipient.getUsername(), chatRoom.getId());

        // Convert to DTO to send to the client
        ChatMessageResponse response = ChatMessageResponse.fromMessage(savedMessage);

        // Send the message to the recipient's private topic
        // The recipient must be subscribed to /topic/user/{userId}
        messagingTemplate.convertAndSendToUser(recipient.getId().toString(), "/topic/messages", response);
    }

    /**
     * Retrieves or creates a chat room for two users.
     *
     * @param user1 The first user.
     * @param user2 The second user.
     * @return The existing or newly created ChatRoom.
     */
    private ChatRoom getOrCreateChatRoom(User user1, User user2) {
        return chatRoomRepository.findChatRoomByParticipants(user1, user2)
                .orElseGet(() -> {
                    ChatRoom newChatRoom = ChatRoom.builder()
                            .participants(Arrays.asList(user1, user2))
                            .build();
                    log.info("Creating new chat room for users {} and {}", user1.getUsername(), user2.getUsername());
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
}
