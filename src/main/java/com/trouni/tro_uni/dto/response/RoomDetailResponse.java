package com.trouni.tro_uni.dto.response;

import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * RoomDetailResponse - DTO cho thông tin chi tiết phòng
 * Sử dụng cho Student users để xem thông tin đầy đủ của phòng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDetailResponse {
    
    private UUID id;
    private String title;
    private String description;
    private RoomType roomType;
    private String streetAddress;
    private String city;
    private String district;
    private String ward;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal pricePerMonth;
    private BigDecimal areaSqm;
    private String status;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Landlord information
    private LandlordContactResponse landlord;
    
    // Images
    private List<RoomImageResponse> images;
    
    // Amenities
    private List<AmenityResponse> amenities;
    
    // Reviews summary
    private ReviewSummaryResponse reviewSummary;
    
    // Analytics for premium features
    private RoomAnalyticsResponse analytics;
    
    public static RoomDetailResponse fromRoom(Room room) {
        return RoomDetailResponse.builder()
                .id(room.getId())
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
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .landlord(LandlordContactResponse.fromUser(room.getOwner()))
                .images(room.getImages() != null ? 
                    room.getImages().stream()
                        .map(RoomImageResponse::fromRoomImage)
                        .collect(Collectors.toList()) : List.of())
                .amenities(room.getAmenities() != null ?
                    room.getAmenities().stream()
                        .map(AmenityResponse::fromMasterAmenity)
                        .collect(Collectors.toList()) : List.of())
                .build();
    }
}
