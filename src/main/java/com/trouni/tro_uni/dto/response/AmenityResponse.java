package com.trouni.tro_uni.dto.response;

import com.trouni.tro_uni.entity.MasterAmenity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * AmenityResponse - DTO cho tiện ích phòng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmenityResponse {
    
    private UUID id;
    private String name;
    private String description;
    private String iconUrl;
    
    public static AmenityResponse fromMasterAmenity(MasterAmenity amenity) {
        return AmenityResponse.builder()
                .id(amenity.getId())
                .name(amenity.getName())
                .description(amenity.getDescription())
                .iconUrl(amenity.getIconUrl())
                .build();
    }
}
