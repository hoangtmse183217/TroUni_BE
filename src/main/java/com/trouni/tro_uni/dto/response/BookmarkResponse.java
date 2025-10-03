package com.trouni.tro_uni.dto.response;

import com.trouni.tro_uni.entity.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * BookmarkResponse - DTO cho thông tin bookmark
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponse {
    
    private UUID userId;
    private UUID roomId;
    private String roomTitle;
    private String roomCity;
    private String roomDistrict;
    private java.math.BigDecimal roomPrice;
    private String roomStatus;
    private String primaryImageUrl;
    private LocalDateTime bookmarkedAt;
    
    public static BookmarkResponse fromBookmark(Bookmark bookmark) {
        String primaryImageUrl = null;
        if (bookmark.getRoom().getImages() != null && !bookmark.getRoom().getImages().isEmpty()) {
            primaryImageUrl = bookmark.getRoom().getImages().stream()
                    .filter(img -> img.isPrimary())
                    .findFirst()
                    .map(img -> img.getImageUrl())
                    .orElse(bookmark.getRoom().getImages().get(0).getImageUrl());
        }
        
        return BookmarkResponse.builder()
                .userId(bookmark.getUser().getId())
                .roomId(bookmark.getRoom().getId())
                .roomTitle(bookmark.getRoom().getTitle())
                .roomCity(bookmark.getRoom().getCity())
                .roomDistrict(bookmark.getRoom().getDistrict())
                .roomPrice(bookmark.getRoom().getPricePerMonth())
                .roomStatus(bookmark.getRoom().getStatus())
                .primaryImageUrl(primaryImageUrl)
                .bookmarkedAt(bookmark.getCreatedAt())
                .build();
    }
}
