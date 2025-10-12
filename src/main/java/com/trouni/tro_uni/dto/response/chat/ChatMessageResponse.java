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
    String content;
    LocalDateTime timestamp;

    public static ChatMessageResponse fromMessage(Message message) {
        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .chatRoomId(message.getChatRoom().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getUsername()) // Assuming User has getUsername()
                .content(message.getContent())
                .timestamp(message.getSentAt())
                .build();
    }
}
