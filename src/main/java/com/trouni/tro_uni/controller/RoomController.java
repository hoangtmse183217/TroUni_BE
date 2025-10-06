package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.request.room.RoomRequest;
import com.trouni.tro_uni.dto.request.room.UpdateRoomRequest;
import com.trouni.tro_uni.dto.response.room.RoomResponse;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.service.RoomService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {
    RoomService roomService;

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
     * Get room by ID
     *
     * @param roomId - Room identifier
     * @return ResponseEntity<RoomResponse>
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoomById(
            @PathVariable UUID roomId
    ) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoomResponse>> getRoomByUserId(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(roomService.getRoomsByUserId(userId));
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
    @GetMapping
    public ResponseEntity<Page<RoomResponse>> getAllRooms(Pageable pageable) {
        return ResponseEntity.ok(roomService.getAllRooms(pageable));
    }

    /**
     * Get all available rooms
     *
     * @return ResponseEntity<Page<RoomResponse>>
     */
    @GetMapping("/all")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        List<RoomResponse> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    /**
     * Search rooms with filters
     * @param city - City filter
     * @param district - District filter
     * @param minPrice - Minimum price
     * @param maxPrice - Maximum price
     * @param minArea - Minimum area
     * @param maxArea - Maximum area
     * @param pageable - Pagination information
     * @return ResponseEntity<Page<RoomResponse>>
     */
//    @GetMapping("/search")
//    public ResponseEntity<Page<RoomResponse>> searchRooms(
//            @RequestParam(required = false) String city,
//            @RequestParam(required = false) String district,
//            @RequestParam(required = false) Double minPrice,
//            @RequestParam(required = false) Double maxPrice,
//            @RequestParam(required = false) Double minArea,
//            @RequestParam(required = false) Double maxArea,
//            Pageable pageable
//    ) {
//        return ResponseEntity.ok(roomService.searchRooms(city, district, minPrice, maxPrice, minArea, maxArea, pageable));
//    }

    /**
     * Get rooms owned by current user
     * @param currentUser - Authenticated user
     * @param pageable - Pagination information
     * @return ResponseEntity<Page<RoomResponse>>
     */
//    @GetMapping("/my-rooms")
//    public ResponseEntity<Page<RoomResponse>> getMyRooms(
//            @AuthenticationPrincipal User currentUser,
//            Pageable pageable
//    ) {
//        return ResponseEntity.ok(roomService.getUserRooms(currentUser, pageable));
//    }
}
