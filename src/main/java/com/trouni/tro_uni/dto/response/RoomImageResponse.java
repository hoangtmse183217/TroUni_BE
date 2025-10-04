package com.trouni.tro_uni.dto.response;

import com.trouni.tro_uni.entity.RoomImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomImageResponse {
    private Long id;
    private String imageUrl;
    private boolean primary;

    public static RoomImageResponse fromRoomImage(RoomImage roomImage) {
        return new RoomImageResponse(
            roomImage.getId() != null ? roomImage.getId().getMostSignificantBits() : null,
            roomImage.getImageUrl(),
            roomImage.isPrimary()
        );
    }
}

