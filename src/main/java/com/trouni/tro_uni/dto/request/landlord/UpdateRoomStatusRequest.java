package com.trouni.tro_uni.dto.request.landlord;

import com.trouni.tro_uni.enums.RoomStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoomStatusRequest {
    @NotNull(message = "Status cannot be null")
    private RoomStatus status;
}
