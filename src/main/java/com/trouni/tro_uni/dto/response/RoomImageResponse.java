package com.trouni.tro_uni.dto.response;

import com.trouni.tro_uni.entity.RoomImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomImageResponse {
    private UUID id;
    private String imageUrl;
    private boolean primary;

    public static RoomImageResponse fromRoomImage(RoomImage roomImage) {
        return new RoomImageResponse(
            roomImage.getId(),
            roomImage.getImageUrl(),
            roomImage.isPrimary()
        );
    }
}

