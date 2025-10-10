package com.trouni.tro_uni.dto.request;

import com.trouni.tro_uni.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomSearchRequest {
    private String city;
    private String district;
    private String ward;
    private Integer minPrice;
    private Integer maxPrice;
    private Double minArea;
    private Double maxArea;
    private RoomType roomType;
}

