package com.trouni.tro_uni.dto.response.room;

import com.trouni.tro_uni.entity.RoomImage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomImageResponse {
    UUID id;
    String imageUrl;
    boolean isPrimary;

    /**
     * Convert RoomImage entity to RoomImageResponse DTO
     * @param image - RoomImage entity to convert
     * @return RoomImageResponse
     */
    public static RoomImageResponse fromRoomImage(RoomImage image) {
        if (image == null) return null;

        return RoomImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .isPrimary(image.isPrimary())
                .build();
    }
}
