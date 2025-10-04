package com.trouni.tro_uni.controller;

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
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {
    @Autowired
    private RoomService roomService;

    // ================== ORIGINAL SEARCH AND FILTER APIs (from main branch) ==================
    
    // Tìm phòng cơ bản + filter
    @PreAuthorize("hasAnyRole('GUEST', 'LANDLORD', 'ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<RoomListItemResponse>> searchRooms(RoomSearchRequest request) {
        List<RoomListItemResponse> rooms = roomService.searchRooms(request);
        return ResponseEntity.ok(rooms);
    }

    // Danh sách phòng công khai
    @PreAuthorize("hasAnyRole('GUEST', 'LANDLORD', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<RoomListItemResponse>> getPublicRooms() {
        List<RoomListItemResponse> rooms = roomService.getPublicRooms();
        return ResponseEntity.ok(rooms);
    }

    // Tóm tắt thông tin 1 phòng
    @PreAuthorize("hasAnyRole('GUEST', 'LANDLORD', 'ADMIN')")
    @GetMapping("/{roomId}/summary")
    public ResponseEntity<RoomSummaryResponse> getRoomSummary(@PathVariable Long roomId) {
        RoomSummaryResponse summary = roomService.getRoomSummary(roomId);
        if (summary == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(summary);
    }

    // Lấy ảnh phòng
    @PreAuthorize("hasAnyRole('GUEST', 'LANDLORD', 'ADMIN')")
    @GetMapping("/{roomId}/images")
    public ResponseEntity<RoomImagesResponse> getRoomImages(@PathVariable Long roomId) {
        RoomImagesResponse images = roomService.getRoomImages(roomId);
        return ResponseEntity.ok(images);
    }

    // ================== NEW CRUD APIs (from nguyenvuong-dev branch) ==================

    /**
     * Create a new room
     *
     * @param currentUser - Authenticated user (landlord)
     * @param request     - Room creation details
     * @return ResponseEntity<RoomResponse>
     */
    @PostMapping("/room")
    public ResponseEntity<RoomResponse> createRoom(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody RoomRequest request
    ) {
        return ResponseEntity.ok(roomService.createRoom(currentUser, request));
    }

    /**
     * Get room by ID (UUID version)
     *
     * @param roomId - Room identifier
     * @return ResponseEntity<RoomResponse>
     */
    @GetMapping("/{roomId}/details")
    public ResponseEntity<RoomResponse> getRoomById(
            @PathVariable UUID roomId
    ) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    /**
     * Update room details
     *
     * @param currentUser - Authenticated user (must be room owner)
     * @param roomId      - Room identifier
     * @param request     - Updated room details
     * @return ResponseEntity<RoomResponse>
     */
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID roomId,
            @Valid @RequestBody UpdateRoomRequest request
    ) {
        return ResponseEntity.ok(roomService.updateRoom(currentUser, roomId, request));
    }

    /**
     * Delete a room
     *
     * @param currentUser - Authenticated user (must be room owner)
     * @param roomId      - Room identifier
     * @return ResponseEntity<Void>
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID roomId
    ) {
        roomService.deleteRoom(currentUser, roomId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all available rooms with pagination
     *
     * @param pageable - Pagination information
     * @return ResponseEntity<Page<RoomResponse>>
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<RoomResponse>> getAllRoomsPaginated(Pageable pageable) {
        return ResponseEntity.ok(roomService.getAllRooms(pageable));
    }

    /**
     * Get all available rooms (UUID version)
     *
     * @return ResponseEntity<List<RoomResponse>>
     */
    @GetMapping("/all")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        List<RoomResponse> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }
}
