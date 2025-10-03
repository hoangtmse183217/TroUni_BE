package com.trouni.tro_uni.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ReviewSummaryResponse - DTO cho tóm tắt đánh giá phòng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryResponse {
    
    private Double averageScore;
    private Long totalReviews;
    private Long fiveStarCount;
    private Long fourStarCount;
    private Long threeStarCount;
    private Long twoStarCount;
    private Long oneStarCount;
}
