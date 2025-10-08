package com.trouni.tro_uni.dto.response.landlord;

import com.trouni.tro_uni.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomPerformanceResponse {
    private UUID roomId;
    private String title;
    private RoomStatus status;
    private long viewCount;
    private long bookmarkCount;
    private double averageRating;
}
