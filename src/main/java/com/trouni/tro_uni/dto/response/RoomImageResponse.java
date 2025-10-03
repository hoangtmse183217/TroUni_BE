package com.trouni.tro_uni.dto.response;

import com.trouni.tro_uni.entity.RoomImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RoomImageResponse - DTO cho hình ảnh phòng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomImageResponse {
    
    private UUID id;
    private String imageUrl;
    private String description;
    private boolean isPrimary;
    private LocalDateTime createdAt;
    
    public static RoomImageResponse fromRoomImage(RoomImage roomImage) {
        return RoomImageResponse.builder()
                .id(roomImage.getId())
                .imageUrl(roomImage.getImageUrl())
                .description(roomImage.getDescription())
                .isPrimary(roomImage.isPrimary())
                .createdAt(roomImage.getCreatedAt())
                .build();
    }
}
