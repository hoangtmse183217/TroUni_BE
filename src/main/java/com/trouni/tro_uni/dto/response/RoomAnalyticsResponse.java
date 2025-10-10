package com.trouni.tro_uni.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RoomAnalyticsResponse - DTO cho thống kê phòng
 * Chỉ hiển thị cho premium users hoặc landlords
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAnalyticsResponse {
    
    private int totalViews;
    private int viewsThisWeek;
    private int viewsThisMonth;
    private int bookmarkCount;
    private LocalDateTime lastViewedAt;
    private boolean isBoosted;
    private LocalDateTime boostExpiresAt;
}
