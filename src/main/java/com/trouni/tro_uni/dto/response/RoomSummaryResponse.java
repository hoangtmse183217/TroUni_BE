package com.trouni.tro_uni.dto.response;

import com.trouni.tro_uni.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomSummaryResponse {
    private UUID id;
    private String title;
    private String address;
    private RoomType roomType;
    private Double area;
    private Integer price;
    private String description;
    private String ownerName;
    private String ownerPhone;
    private String thumbnailUrl;
}

