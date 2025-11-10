package com.trouni.tro_uni.dto.response.landlord;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponse {
    private long totalRooms;
    private long activeRooms;
    private long totalBookmarks;
    private double averageRating;
}