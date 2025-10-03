package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.response.RoomAnalyticsResponse;
import com.trouni.tro_uni.dto.response.RoomDetailResponse;
import com.trouni.tro_uni.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * RoomController - Controller xử lý các API liên quan đến phòng
 * 
 * Chức năng chính:
 * - Xem chi tiết phòng (dành cho Student users)
 * - Tìm kiếm và lọc phòng
 * - Xem thống kê phòng (dành cho Landlord)
 * - Quản lý hình ảnh phòng
 */
@Slf4j
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoomController {
    
    private final RoomService roomService;
    
    /**
     * API lấy thông tin chi tiết phòng
     * Endpoint: GET /api/rooms/{roomId}
     * 
     * @param roomId - ID của phòng
     * @return ResponseEntity - Thông tin chi tiết phòng
     */
    @GetMapping("/{roomId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('LANDLORD') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getRoomDetail(@PathVariable UUID roomId) {
        try {
            RoomDetailResponse roomDetail = roomService.getRoomDetail(roomId);
            return ResponseEntity.ok(ApiResponse.success("Room details retrieved successfully", roomDetail));
        } catch (Exception e) {
            log.error("Error getting room detail: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ROOM_DETAILS_ERROR", "Failed to get room details: " + e.getMessage()));
        }
    }
    
    /**
     * API lấy danh sách phòng có sẵn
     * Endpoint: GET /api/rooms
     * 
     * @param page - Số trang (default: 0)
     * @param size - Kích thước trang (default: 10)
     * @param sortBy - Sắp xếp theo (default: createdAt)
     * @param sortDir - Hướng sắp xếp (default: desc)
     * @return ResponseEntity - Danh sách phòng có phân trang
     */
    @GetMapping
    public ResponseEntity<?> getAvailableRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        if (size > 100) {
            size = 100; // Max 100 items per page
        }
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<RoomDetailResponse> rooms = roomService.getAvailableRooms(pageable);
            return ResponseEntity.ok(ApiResponse.success("Available rooms retrieved successfully", rooms));
        } catch (Exception e) {
            log.error("Error getting available rooms: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_AVAILABLE_ROOMS_ERROR", "Failed to get available rooms: " + e.getMessage()));
        }
    }
    
    /**
     * API tìm kiếm phòng theo nhiều tiêu chí
     * Endpoint: GET /api/rooms/search
     * 
     * @param city - Thành phố
     * @param district - Quận/huyện
     * @param minPrice - Giá tối thiểu
     * @param maxPrice - Giá tối đa
     * @param page - Số trang
     * @param size - Kích thước trang
     * @return ResponseEntity - Kết quả tìm kiếm
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchRooms(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // Set default values if not provided
            if (minPrice == null) minPrice = BigDecimal.ZERO;
            if (maxPrice == null) maxPrice = new BigDecimal("999999999");
            if (city == null) city = "";
            if (district == null) district = "";
            
            Pageable pageable = PageRequest.of(page, size, 
                    Sort.by("createdAt").descending());
            
            Page<RoomDetailResponse> rooms = roomService.searchRooms(
                    city, district, minPrice, maxPrice, pageable);
            
            return ResponseEntity.ok(ApiResponse.success("Room search completed successfully", rooms));
        } catch (Exception e) {
            log.error("Error searching rooms: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("SEARCH_ROOMS_ERROR", "Failed to search rooms: " + e.getMessage()));
        }
    }
    
    /**
     * API lấy thống kê phòng (chỉ dành cho landlord của phòng đó hoặc admin)
     * Endpoint: GET /api/rooms/{roomId}/analytics
     * 
     * @param roomId - ID của phòng
     * @return ResponseEntity - Thống kê phòng
     */
    @GetMapping("/{roomId}/analytics")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN')")
    public ResponseEntity<?> getRoomAnalytics(@PathVariable UUID roomId) {
        try {
            // Add authorization check to ensure landlord owns the room
            RoomDetailResponse roomDetail = roomService.getRoomDetail(roomId);
            RoomAnalyticsResponse analytics = roomDetail.getAnalytics();
            
            if (analytics == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("UNAUTHORIZED_ANALYTICS", "You are not authorized to view analytics for this room"));
            }
            
            return ResponseEntity.ok(ApiResponse.success("Room analytics retrieved successfully", analytics));
        } catch (Exception e) {
            log.error("Error getting room analytics: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ROOM_ANALYTICS_ERROR", "Failed to get room analytics: " + e.getMessage()));
        }
    }
}
