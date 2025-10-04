package com.trouni.tro_uni.dto.request.room;

import com.trouni.tro_uni.dto.request.masteramenity.MasterAmenityRequest;
import com.trouni.tro_uni.enums.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomRequest {
     @NotBlank
     String title;

     String description;

    @NotNull
    private RoomType roomType;

    // Address information
     String streetAddress;
     String city;
     String district;
     String ward;
     BigDecimal latitude;
     BigDecimal longitude;

    // Core room details
     @NotNull
     BigDecimal pricePerMonth;
     BigDecimal areaSqm;

    // Optional fields
     String status = "available";

     //Related data
     List<String> images;
     List<MasterAmenityRequest> amenities;

}
