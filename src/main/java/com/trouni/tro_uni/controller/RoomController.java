package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.request.RoomSearchRequest;
import com.trouni.tro_uni.dto.request.room.RoomRequest;
import com.trouni.tro_uni.dto.request.room.UpdateRoomRequest;
import com.trouni.tro_uni.dto.response.RoomListItemResponse;
import com.trouni.tro_uni.dto.response.RoomSummaryResponse;
import com.trouni.tro_uni.dto.response.RoomImagesResponse;
import com.trouni.tro_uni.dto.response.room.RoomResponse;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.service.RoomService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {
    @Autowired
    private RoomService roomService;

    // ================== ORIGINAL SEARCH AND FILTER APIs (from main branch) ==================
    
    // Tìm phòng cơ bản + filter
    @PreAuthorize("permitAll()")
    @GetMapping("/search")
    public ResponseEntity<?> searchRooms(RoomSearchRequest request) {
        try {
            List<RoomListItemResponse> rooms = roomService.searchRooms(request);
            return ResponseEntity.ok(ApiResponse.success("Rooms search completed successfully", rooms));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("SEARCH_ROOMS_ERROR", "Failed to search rooms: " + e.getMessage()));
        }
    }

    // Danh sách phòng công khai
    @GetMapping
    public ResponseEntity<?> getPublicRooms() {
        try {
            List<RoomListItemResponse> rooms = roomService.getPublicRooms();
            return ResponseEntity.ok(ApiResponse.success("Public rooms retrieved successfully", rooms));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_PUBLIC_ROOMS_ERROR", "Failed to get public rooms: " + e.getMessage()));
        }
    }

    // Tóm tắt thông tin 1 phòng
    @GetMapping("/{roomId}/summary")
    public ResponseEntity<?> getRoomSummary(@PathVariable UUID roomId) {
        try {
            RoomSummaryResponse summary = roomService.getRoomSummary(roomId);
            if (summary == null) {
                return ResponseEntity.status(404)
                        .body(ApiResponse.error("ROOM_NOT_FOUND", "Room not found with ID: " + roomId));
            }
            return ResponseEntity.ok(ApiResponse.success("Room summary retrieved successfully", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ROOM_SUMMARY_ERROR", "Failed to get room summary: " + e.getMessage()));
        }
    }

    // Lấy ảnh phòng
    @GetMapping("/{roomId}/images")
    public ResponseEntity<?> getRoomImages(@PathVariable UUID roomId) {
        try {
            RoomImagesResponse images = roomService.getRoomImages(roomId);
            return ResponseEntity.ok(ApiResponse.success("Room images retrieved successfully", images));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ROOM_IMAGES_ERROR", "Failed to get room images: " + e.getMessage()));
        }
    }

    // ================== NEW CRUD APIs (from nguyenvuong-dev branch) ==================

    /**
     * Create a new room
     *
     * @param currentUser - Authenticated user (landlord)
     * @param request     - Room creation details
     * @return ResponseEntity<?>
     */
    @PostMapping("/room")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<?> createRoom(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody RoomRequest request
    ) {
        try {
            RoomResponse room = roomService.createRoom(currentUser, request);
            return ResponseEntity.ok(ApiResponse.success("Room created successfully", room));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("CREATE_ROOM_ERROR", "Failed to create room: " + e.getMessage()));
        }
    }

    /**
     * Get room by ID (UUID version)
     *
     * @param roomId - Room identifier
     * @return ResponseEntity<?>
     */
    @GetMapping("/{roomId}/details")
    public ResponseEntity<?> getRoomById(
            @PathVariable UUID roomId
    ) {
        try {
            RoomResponse room = roomService.getRoomById(roomId);
            return ResponseEntity.ok(ApiResponse.success("Room details retrieved successfully", room));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ROOM_ERROR", "Failed to get room details: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRoomByUserId(
            @PathVariable UUID userId
    ) {
        try {
            List<RoomResponse> rooms = roomService.getRoomByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("Room details retrieved successfully", rooms));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ROOM_ERROR", "Failed to get room details: " + e.getMessage()));
        }
    }

    /**
     * Update room details
     *
     * @param currentUser - Authenticated user (must be room owner)
     * @param roomId      - Room identifier
     * @param request     - Updated room details
     * @return ResponseEntity<?>
     */
    @PutMapping("/{roomId}")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN')")
    public ResponseEntity<?> updateRoom(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID roomId,
            @Valid @RequestBody UpdateRoomRequest request
    ) {
        try {
            RoomResponse room = roomService.updateRoom(currentUser, roomId, request);
            return ResponseEntity.ok(ApiResponse.success("Room updated successfully", room));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("UPDATE_ROOM_ERROR", "Failed to update room: " + e.getMessage()));
        }
    }

    /**
     * Delete a room
     *
     * @param currentUser - Authenticated user (must be room owner)
     * @param roomId      - Room identifier
     * @return ResponseEntity<?>
     */
    @DeleteMapping("/{roomId}")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteRoom(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID roomId
    ) {
        try {
            roomService.deleteRoom(currentUser, roomId);
            return ResponseEntity.ok(ApiResponse.success("Room deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("DELETE_ROOM_ERROR", "Failed to delete room: " + e.getMessage()));
        }
    }

    /**
     * Get all available rooms with pagination
     *
     * @param pageable - Pagination information
     * @return ResponseEntity<?>
     */
    @GetMapping("/paginated")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> getAllRoomsPaginated(Pageable pageable) {
        try {
            Page<RoomResponse> rooms = roomService.getAllRooms(pageable);
            return ResponseEntity.ok(ApiResponse.success("Rooms retrieved successfully with pagination", rooms));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ROOMS_PAGINATED_ERROR", "Failed to get rooms: " + e.getMessage()));
        }
    }

    /**
     * Get all available rooms (UUID version)
     *
     * @return ResponseEntity<?>
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> getAllRooms() {
        try {
            List<RoomResponse> rooms = roomService.getAllRooms();
            return ResponseEntity.ok(ApiResponse.success("All rooms retrieved successfully", rooms));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ALL_ROOMS_ERROR", "Failed to get all rooms: " + e.getMessage()));
        }
    }
}
