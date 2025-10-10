package com.trouni.tro_uni.dto.response.packages;

import com.trouni.tro_uni.entity.Package;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageResponse {
    private UUID id;
    private String name;
    private BigDecimal pricePerMonth;
    private Integer maxListings;
    private Integer maxImagesPerListing;
    private Boolean canViewStats;
    private Integer boostDays;
    private String featuresJson;

    /**
     * Convert Package entity to PackageResponse DTO
     * @param packageEntity - Package entity to convert
     * @return PackageResponse
     */
    public static PackageResponse fromPackage(Package packageEntity) {
        if (packageEntity == null) return null;

        return PackageResponse.builder()
                .id(packageEntity.getId())
                .name(packageEntity.getName())
                .pricePerMonth(packageEntity.getPricePerMonth())
                .maxListings(packageEntity.getMaxListings())
                .maxImagesPerListing(packageEntity.getMaxImagesPerListing())
                .canViewStats(packageEntity.isCanViewStats())
                .boostDays(packageEntity.getBoostDays())
                .featuresJson(packageEntity.getFeaturesJson())
                .build();
    }
}
