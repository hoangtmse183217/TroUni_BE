package com.trouni.tro_uni.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomSearchResponse {
    private List<RoomListItemResponse> rooms;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}

