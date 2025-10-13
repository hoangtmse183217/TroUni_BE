package com.trouni.tro_uni.dto.response.chat;

import com.trouni.tro_uni.entity.Message;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageResponse {

    UUID messageId;
    UUID chatRoomId;
    UUID senderId;
    String senderName;
    UUID recipientId; // Added recipientId
    String content;
    LocalDateTime timestamp;

    public static ChatMessageResponse fromMessage(Message message) {
        // Assuming ChatRoom has a way to get the other participant if there are only two
        UUID recipientId = null;
        if (message.getChatRoom() != null && message.getChatRoom().getParticipants() != null) {
            recipientId = message.getChatRoom().getParticipants().stream()
                    .filter(p -> !p.getId().equals(message.getSender().getId()))
                    .map(p -> p.getId())
                    .findFirst()
                    .orElse(null);
        }

        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .chatRoomId(message.getChatRoom().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getUsername()) // Assuming User has getUsername()
                .recipientId(recipientId)
                .content(message.getContent())
                .timestamp(message.getSentAt())
                .build();
    }
}
