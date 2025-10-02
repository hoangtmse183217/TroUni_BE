package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.request.room.RoomImageRequest;
import com.trouni.tro_uni.dto.response.room.RoomImageResponse;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.service.RoomImageService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/room-images")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomImageController {

    RoomImageService roomImageService;

    /**
     * Add a new image to a room
     *
     * @param currentUser - Authenticated user (must be room owner)
     * @param roomId      - The ID of the room to add the image to
     * @param request     - The image information to be added
     * @return ResponseEntity<RoomImageResponse>
     */
    @PostMapping("/{roomId}")
    public ResponseEntity<List<RoomImageResponse>> createRoomImages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID roomId,
            @Valid @RequestBody RoomImageRequest request
    ) {
        return ResponseEntity.ok(roomImageService.createRoomImages(currentUser, roomId, request));
    }


    /**
     * Get all images for a specific room
     *
     * @param roomId - The ID of the room
     * @return ResponseEntity<List<RoomImageResponse>>
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<List<RoomImageResponse>> getRoomImages(
            @PathVariable UUID roomId
    ) {
        return ResponseEntity.ok(roomImageService.getRoomImages(roomId));
    }

    /**
     * Delete an image from a room
     *
     * @param currentUser - Authenticated user (must be room owner)
     * @param imageId     - The ID of the image to be deleted
     * @return ResponseEntity<Void>
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteRoomImage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID imageId
    ) {
        roomImageService.deleteRoomImage(currentUser, imageId);
        return ResponseEntity.noContent().build();
    }
}
