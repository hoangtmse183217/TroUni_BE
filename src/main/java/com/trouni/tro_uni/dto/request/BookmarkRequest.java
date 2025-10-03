package com.trouni.tro_uni.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * BookmarkRequest - DTO cho yêu cầu bookmark/unbookmark phòng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkRequest {
    
    @NotNull(message = "Room ID is required")
    private UUID roomId;
}
