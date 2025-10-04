package com.trouni.tro_uni.dto.response;

import com.trouni.tro_uni.entity.RoommatePost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RoommatePostResponse - DTO cho thông tin bài đăng tìm roommate
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoommatePostResponse {
    
    private UUID id;
    private String title;
    private String description;
    private String desiredLocationText;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Author information
    private AuthorResponse author;
    
    public static RoommatePostResponse fromRoommatePost(RoommatePost post) {
        return RoommatePostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .desiredLocationText(post.getDesiredLocationText())
                .budgetMin(post.getBudgetMin())
                .budgetMax(post.getBudgetMax())
                .status(post.getStatus())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .author(AuthorResponse.fromUser(post.getAuthor()))
                .build();
    }
}
