package com.trouni.tro_uni.dto.response.chat;

import com.trouni.tro_uni.entity.ChatRoom;
import com.trouni.tro_uni.dto.response.UserResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatRoomResponse {
    UUID id;
    LocalDateTime createdAt;
    List<UserResponse> participants;

    public static ChatRoomResponse fromChatRoom(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .createdAt(chatRoom.getCreatedAt())
                .participants(chatRoom.getParticipants().stream()
                        .map(UserResponse::fromUser)
                        .collect(Collectors.toList()))
                .build();
    }
}
