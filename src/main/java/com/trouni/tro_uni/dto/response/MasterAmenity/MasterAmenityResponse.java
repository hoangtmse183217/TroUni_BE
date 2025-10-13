package com.trouni.tro_uni.dto.response.MasterAmenity;

import com.trouni.tro_uni.entity.MasterAmenity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterAmenityResponse {
    private UUID id;
    private String name;
    private String icon;
    private boolean active;
    /**
     * Convert MasterAmenity entity to MasterAmenityResponse DTO
     * @param amenity - MasterAmenity entity to convert
     * @return MasterAmenityResponse
     */
    public static MasterAmenityResponse fromMasterAmenity(MasterAmenity amenity) {
        if (amenity == null) return null;

        return MasterAmenityResponse.builder()
                .id(amenity.getId())
                .name(amenity.getName())
                .icon(amenity.getIconUrl())
                .active(amenity.getActive())
                .build();
    }
}
