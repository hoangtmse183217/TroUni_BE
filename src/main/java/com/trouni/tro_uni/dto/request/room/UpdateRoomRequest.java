package com.trouni.tro_uni.dto.request.room;

import com.trouni.tro_uni.dto.request.masteramenity.MasterAmenityRequest;
import com.trouni.tro_uni.enums.RoomType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRoomRequest {
    String title;

    String description;

    RoomType roomType;

    // Address information
    String streetAddress;
    String city;
    String district;
    String ward;
    BigDecimal latitude;
    BigDecimal longitude;

    // Core room details
    BigDecimal pricePerMonth;
    BigDecimal areaSqm;

    // Optional fields
    String status = "available";

    //Related data
    List<String> images;
    List<MasterAmenityRequest> amenities;

}