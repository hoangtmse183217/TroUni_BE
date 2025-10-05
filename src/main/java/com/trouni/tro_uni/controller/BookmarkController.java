package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.request.BookmarkRequest;
import com.trouni.tro_uni.dto.response.BookmarkResponse;
import com.trouni.tro_uni.service.BookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * BookmarkController - Controller xử lý các API bookmark
 * 
 * Chức năng chính:
 * - Bookmark/unbookmark phòng (dành cho tất cả user roles)
 * - Lấy danh sách phòng đã bookmark
 * - Kiểm tra trạng thái bookmark
 */
@Slf4j
@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookmarkController {
    
    private final BookmarkService bookmarkService;
    
    /**
     * API bookmark một phòng
     * Endpoint: POST /api/bookmarks
     * 
     * @param request - Thông tin bookmark request
     * @return ResponseEntity - Kết quả bookmark
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<?> bookmarkRoom(@Valid @RequestBody BookmarkRequest request) {
        try {
            BookmarkResponse bookmark = bookmarkService.bookmarkRoom(request.getRoomId());
            return ResponseEntity.ok(ApiResponse.success("Room bookmarked successfully", bookmark));
        } catch (Exception e) {
            log.error("Error bookmarking room: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("BOOKMARK_ERROR", "Failed to bookmark room: " + e.getMessage()));
        }
    }
    
    /**
     * API unbookmark một phòng
     * Endpoint: DELETE /api/bookmarks/{roomId}
     * 
     * @param roomId - ID của phòng cần unbookmark
     * @return ResponseEntity - Kết quả unbookmark
     */
    @DeleteMapping("/{roomId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<?> unbookmarkRoom(@PathVariable UUID roomId) {
        try {
            bookmarkService.unbookmarkRoom(roomId);
            return ResponseEntity.ok(ApiResponse.success("Room unbookmarked successfully", null));
        } catch (Exception e) {
            log.error("Error unbookmarking room: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("UNBOOKMARK_ERROR", "Failed to unbookmark room: " + e.getMessage()));
        }
    }
    
    /**
     * API toggle bookmark status
     * Endpoint: POST /api/bookmarks/{roomId}/toggle
     * 
     * @param roomId - ID của phòng
     * @return ResponseEntity - Kết quả toggle
     */
    @PostMapping("/{roomId}/toggle")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<?> toggleBookmark(@PathVariable UUID roomId) {
        try {
            BookmarkResponse result = bookmarkService.toggleBookmark(roomId);
            if (result != null) {
                return ResponseEntity.ok(ApiResponse.success("Room bookmarked successfully", result));
            } else {
                return ResponseEntity.ok(ApiResponse.success("Room unbookmarked successfully", null));
            }
        } catch (Exception e) {
            log.error("Error toggling bookmark: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("TOGGLE_BOOKMARK_ERROR", "Failed to toggle bookmark: " + e.getMessage()));
        }
    }
    
    /**
     * API lấy danh sách phòng đã bookmark (có phân trang)
     * Endpoint: GET /api/bookmarks
     * 
     * @param page - Số trang (default: 0)
     * @param size - Kích thước trang (default: 10)
     * @return ResponseEntity - Danh sách bookmark có phân trang
     */
    @GetMapping
    public ResponseEntity<?> getUserBookmarks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, 
                    Sort.by("createdAt").descending());
            
            Page<BookmarkResponse> bookmarks = bookmarkService.getUserBookmarks(pageable);
            return ResponseEntity.ok(ApiResponse.success("Bookmarks retrieved successfully", bookmarks));
        } catch (Exception e) {
            log.error("Error getting user bookmarks: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_BOOKMARKS_ERROR", "Failed to get bookmarks: " + e.getMessage()));
        }
    }
    
    /**
     * API lấy tất cả bookmark của user (không phân trang)
     * Endpoint: GET /api/bookmarks/all
     * 
     * @return ResponseEntity - Danh sách tất cả bookmark
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllUserBookmarks() {
        try {
            List<BookmarkResponse> bookmarks = bookmarkService.getAllUserBookmarks();
            return ResponseEntity.ok(ApiResponse.success("All bookmarks retrieved successfully", bookmarks));
        } catch (Exception e) {
            log.error("Error getting all user bookmarks: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ALL_BOOKMARKS_ERROR", "Failed to get all bookmarks: " + e.getMessage()));
        }
    }
    
    /**
     * API kiểm tra trạng thái bookmark của một phòng
     * Endpoint: GET /api/bookmarks/{roomId}/status
     * 
     * @param roomId - ID của phòng
     * @return ResponseEntity - Trạng thái bookmark
     */
    @GetMapping("/{roomId}/status")
    public ResponseEntity<?> checkBookmarkStatus(@PathVariable UUID roomId) {
        try {
            boolean isBookmarked = bookmarkService.isRoomBookmarked(roomId);
            return ResponseEntity.ok(ApiResponse.success("Bookmark status retrieved successfully", 
                    java.util.Map.of("isBookmarked", isBookmarked, "roomId", roomId)));
        } catch (Exception e) {
            log.error("Error checking bookmark status: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("CHECK_BOOKMARK_STATUS_ERROR", "Failed to check bookmark status: " + e.getMessage()));
        }
    }
}
