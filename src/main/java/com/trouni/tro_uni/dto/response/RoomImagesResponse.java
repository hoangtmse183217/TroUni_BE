package com.trouni.tro_uni.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomImagesResponse {
    private List<RoomImageResponse> images;
}

