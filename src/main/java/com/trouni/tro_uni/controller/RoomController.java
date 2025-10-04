package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.request.RoomSearchRequest;
import com.trouni.tro_uni.dto.response.RoomListItemResponse;
import com.trouni.tro_uni.dto.response.RoomSummaryResponse;
import com.trouni.tro_uni.dto.response.RoomImagesResponse;
import com.trouni.tro_uni.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

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
}
