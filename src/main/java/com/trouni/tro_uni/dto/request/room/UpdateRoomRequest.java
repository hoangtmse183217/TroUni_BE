package com.trouni.tro_uni.dto.request.room;

import com.trouni.tro_uni.enums.RoomType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRoomRequest {
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
    private List<UUID> amenityIds;
}
