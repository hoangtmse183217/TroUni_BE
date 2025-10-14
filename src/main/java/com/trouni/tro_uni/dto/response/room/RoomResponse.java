package com.trouni.tro_uni.dto.response.room;

import com.trouni.tro_uni.dto.response.MasterAmenity.MasterAmenityResponse;
import com.trouni.tro_uni.dto.response.UserResponse;
import com.trouni.tro_uni.dto.response.review.ReviewResponse;
import com.trouni.tro_uni.entity.Review;
import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private UUID id;
    private UserResponse owner;
    private String title;
    private String description;
    private RoomType roomType;

    // Address information
    private String streetAddress;
    private String city;
    private String district;
    private String ward;
    private BigDecimal latitude;
    private BigDecimal longitude;

    // Room details
    private BigDecimal pricePerMonth;
    private BigDecimal areaSqm;
    private String status;

    // Statistics
    private int viewCount;
    private Double averageRating;
    private Integer totalReviews;

    // Premium features
    private LocalDateTime boostExpiresAt;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Related entities
    private List<RoomImageResponse> images;
    private List<MasterAmenityResponse> amenities;
    private List<ReviewResponse> recentReviews;

    /**
     * Convert Room entity to RoomResponse DTO
     * @param room - Room entity to convert
     * @return RoomResponse
     */
    public static RoomResponse fromRoom(Room room) {
        if (room == null) return null;

        return RoomResponse.builder()
                .id(room.getId())
                .owner(UserResponse.fromUser(room.getOwner()))
                .title(room.getTitle())
                .description(room.getDescription())
                .roomType(room.getRoomType())
                .streetAddress(room.getStreetAddress())
                .city(room.getCity())
                .district(room.getDistrict())
                .ward(room.getWard())
                .latitude(room.getLatitude())
                .longitude(room.getLongitude())
                .pricePerMonth(room.getPricePerMonth())
                .areaSqm(room.getAreaSqm())
                .status(room.getStatus())
                .viewCount(room.getViewCount())
                .boostExpiresAt(room.getBoostExpiresAt())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .images(room.getImages() != null ? room.getImages().stream()
                        .map(RoomImageResponse::fromRoomImage)
                        .collect(Collectors.toList()) : null)
                .amenities(room.getAmenities() != null ? room.getAmenities().stream()
                        .map(MasterAmenityResponse::fromMasterAmenity)
                        .collect(Collectors.toList()) : null)
                .averageRating(calculateAverageRating(room))
                .totalReviews(room.getReviews() != null ? room.getReviews().size() : 0)
                .recentReviews(room.getReviews() != null ? room.getReviews().stream()
                        .sorted(Comparator.comparing(
                                Review::getCreatedAt,
                                Comparator.nullsLast(Comparator.reverseOrder())
                        ))
                        .limit(3)
                        .map(ReviewResponse::fromReview)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    /**
     * Calculate average rating from room reviews
     * @param room - Room entity
     * @return average rating or 0.0 if no reviews
     */
    private static Double calculateAverageRating(Room room) {
        if (room.getReviews() == null || room.getReviews().isEmpty()) {
            return 0.0;
        }
        return room.getReviews().stream()
                .mapToDouble(review -> review.getScore())
                .average()
                .orElse(0.0);
    }
}
