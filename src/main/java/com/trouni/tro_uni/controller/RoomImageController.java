package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.request.room.RoomImageRequest;
import com.trouni.tro_uni.dto.response.room.RoomImageResponse;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.service.RoomImageService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * @return ResponseEntity<?>
     */
    @PostMapping("/{roomId}")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN')")
    public ResponseEntity<?> createRoomImages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID roomId,
            @Valid @RequestBody RoomImageRequest request
    ) {
        try {
            List<RoomImageResponse> images = roomImageService.createRoomImages(currentUser, roomId, request);
            return ResponseEntity.ok(ApiResponse.success("Room images added successfully", images));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("CREATE_ROOM_IMAGES_ERROR", "Failed to add room images: " + e.getMessage()));
        }
    }


    /**
     * Get all images for a specific room
     *
     * @param roomId - The ID of the room
     * @return ResponseEntity<?>
     */
    @GetMapping("/{roomId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getRoomImages(
            @PathVariable UUID roomId
    ) {
        try {
            List<RoomImageResponse> images = roomImageService.getRoomImages(roomId);
            return ResponseEntity.ok(ApiResponse.success("Room images retrieved successfully", images));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ROOM_IMAGES_ERROR", "Failed to get room images: " + e.getMessage()));
        }
    }



    /**
     * Delete an image from a room
     *
     * @param currentUser - Authenticated user (must be room owner)
     * @param imageId     - The ID of the image to be deleted
     * @return ResponseEntity<?>
     */
    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteRoomImage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID imageId
    ) {
        try {
            roomImageService.deleteRoomImage(currentUser, imageId);
            return ResponseEntity.ok(ApiResponse.success("Room image deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("DELETE_ROOM_IMAGE_ERROR", "Failed to delete room image: " + e.getMessage()));
        }
    }
}
