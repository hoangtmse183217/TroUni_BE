package com.trouni.tro_uni.dto.request.masteramenity;

import com.trouni.tro_uni.dto.response.room.RoomResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MasterAmenityRequest {
    String name;
    String icon;
    RoomResponse room;
}
