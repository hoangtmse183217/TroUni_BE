package com.trouni.tro_uni.dto.response.review;

import com.trouni.tro_uni.dto.response.UserResponse;
import com.trouni.tro_uni.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    // Basic information
    private UUID id;
    private String comment;
    private Integer score;


    // User who wrote the review
    private UserResponse user;

    //Room which got reviewed
    private UUID roomId;

    // Timestamps
    private LocalDateTime createdAt;
    // Timestamps
    private LocalDateTime updatedAt;

    /**
     * Convert Review entity to ReviewResponse DTO
     *
     * @param review - Review entity to convert
     * @return ReviewResponse
     */
    public static ReviewResponse fromReview(Review review) {
        if (review == null) return null;

        return ReviewResponse.builder()
                .id(review.getId())
                .score(review.getScore())
                .comment(review.getComment())
                .roomId(review.getRoom().getId())
                .user(UserResponse.fromUser(review.getUser()))
                .createdAt(review.getCreatedAt())
                .build();
    }
}
