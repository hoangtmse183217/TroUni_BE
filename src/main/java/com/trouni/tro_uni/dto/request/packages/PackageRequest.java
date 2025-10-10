package com.trouni.tro_uni.dto.request.packages;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageRequest {

    @NotBlank(message = "Package name is required")
    private String name;

    @NotNull(message = "Price per month is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private BigDecimal pricePerMonth;

    @NotNull(message = "Max listings is required")
    @Min(value = 1, message = "Max listings must be at least 1")
    private Integer maxListings;

    @NotNull(message = "Max images per listing is required")
    @Min(value = 1, message = "Max images per listing must be at least 1")
    private Integer maxImagesPerListing;

//    @NotNull(message = "Can view stats field is required")
//    private Boolean canViewStats;

    @NotNull(message = "Boost days is required")
    @Min(value = 0, message = "Boost days must be greater than or equal to 0")
    private Integer boostDays;

    private String featuresJson;
}
