package com.trouni.tro_uni.dto.request;

import com.trouni.tro_uni.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomSearchRequest {
    private String location; // district/ward/city
    private Integer minPrice;
    private Integer maxPrice;
    private Double minArea;
    private Double maxArea;
    private RoomType roomType;
    private Integer page;
    private Integer size;
}

