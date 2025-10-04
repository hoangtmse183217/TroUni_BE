package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.request.CreateRoommatePostRequest;
import com.trouni.tro_uni.dto.request.UpdateRoommatePostRequest;
import com.trouni.tro_uni.dto.response.RoommatePostResponse;
import com.trouni.tro_uni.service.RoommatePostService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * RoommatePostController - Controller xử lý các API bài đăng tìm roommate
 * 
 * Chức năng chính:
 * - CRUD operations cho bài đăng tìm roommate
 * - Tìm kiếm và lọc bài đăng
 * - Quản lý bài đăng của user
 */
@Slf4j
@RestController
@RequestMapping("/roommate-posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoommatePostController {
    
    private final RoommatePostService roommatePostService;
    
    /**
     * API tạo bài đăng tìm roommate mới
     * Endpoint: POST /api/roommate-posts
     * 
     * @param request - Thông tin bài đăng
     * @return ResponseEntity - Bài đăng đã tạo
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> createRoommatePost(@Valid @RequestBody CreateRoommatePostRequest request) {
        try {
            RoommatePostResponse post = roommatePostService.createRoommatePost(request);
            return ResponseEntity.ok(ApiResponse.success("Roommate post created successfully", post));
        } catch (Exception e) {
            log.error("Error creating roommate post: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("CREATE_ROOMMATE_POST_ERROR", "Failed to create roommate post: " + e.getMessage()));
        }
    }
    
    /**
     * API cập nhật bài đăng tìm roommate
     * Endpoint: PUT /api/roommate-posts/{postId}
     * 
     * @param postId - ID của bài đăng
     * @param request - Thông tin cập nhật
     * @return ResponseEntity - Bài đăng đã cập nhật
     */
    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> updateRoommatePost(@PathVariable UUID postId, 
                                               @Valid @RequestBody UpdateRoommatePostRequest request) {
        try {
            RoommatePostResponse post = roommatePostService.updateRoommatePost(postId, request);
            return ResponseEntity.ok(ApiResponse.success("Roommate post updated successfully", post));
        } catch (Exception e) {
            log.error("Error updating roommate post: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("UPDATE_ROOMMATE_POST_ERROR", "Failed to update roommate post: " + e.getMessage()));
        }
    }
    
    /**
     * API xóa bài đăng tìm roommate
     * Endpoint: DELETE /api/roommate-posts/{postId}
     * 
     * @param postId - ID của bài đăng
     * @return ResponseEntity - Kết quả xóa
     */
    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> deleteRoommatePost(@PathVariable UUID postId) {
        try {
            roommatePostService.deleteRoommatePost(postId);
            return ResponseEntity.ok(ApiResponse.success("Roommate post deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting roommate post: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("DELETE_ROOMMATE_POST_ERROR", "Failed to delete roommate post: " + e.getMessage()));
        }
    }
    
    /**
     * API lấy thông tin chi tiết bài đăng
     * Endpoint: GET /api/roommate-posts/{postId}
     * 
     * @param postId - ID của bài đăng
     * @return ResponseEntity - Thông tin chi tiết bài đăng
     */
    @GetMapping("/{postId}")
    public ResponseEntity<?> getRoommatePost(@PathVariable UUID postId) {
        try {
            RoommatePostResponse post = roommatePostService.getRoommatePost(postId);
            return ResponseEntity.ok(ApiResponse.success("Roommate post retrieved successfully", post));
        } catch (Exception e) {
            log.error("Error getting roommate post: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ROOMMATE_POST_ERROR", "Failed to get roommate post: " + e.getMessage()));
        }
    }
    
    /**
     * API lấy danh sách bài đăng tìm roommate
     * Endpoint: GET /api/roommate-posts
     * 
     * @param status - Trạng thái bài đăng (open, closed)
     * @param page - Số trang
     * @param size - Kích thước trang
     * @return ResponseEntity - Danh sách bài đăng có phân trang
     */
    @GetMapping
    public ResponseEntity<?> getRoommatePosts(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, 
                    Sort.by("createdAt").descending());
            
            Page<RoommatePostResponse> posts = roommatePostService.getRoommatePosts(status, pageable);
            return ResponseEntity.ok(ApiResponse.success("Roommate posts retrieved successfully", posts));
        } catch (Exception e) {
            log.error("Error getting roommate posts: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ROOMMATE_POSTS_ERROR", "Failed to get roommate posts: " + e.getMessage()));
        }
    }
    
    /**
     * API lấy danh sách bài đăng của user hiện tại
     * Endpoint: GET /api/roommate-posts/my-posts
     * 
     * @return ResponseEntity - Danh sách bài đăng của user
     */
    @GetMapping("/my-posts")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getCurrentUserPosts() {
        try {
            List<RoommatePostResponse> posts = roommatePostService.getCurrentUserPosts();
            return ResponseEntity.ok(ApiResponse.success("User posts retrieved successfully", posts));
        } catch (Exception e) {
            log.error("Error getting user posts: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_USER_POSTS_ERROR", "Failed to get user posts: " + e.getMessage()));
        }
    }
    
    /**
     * API tìm kiếm bài đăng theo ngân sách
     * Endpoint: GET /api/roommate-posts/search/budget
     * 
     * @param minBudget - Ngân sách tối thiểu
     * @param maxBudget - Ngân sách tối đa
     * @param page - Số trang
     * @param size - Kích thước trang
     * @return ResponseEntity - Kết quả tìm kiếm
     */
    @GetMapping("/search/budget")
    public ResponseEntity<?> searchByBudget(
            @RequestParam BigDecimal minBudget,
            @RequestParam BigDecimal maxBudget,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, 
                    Sort.by("createdAt").descending());
            
            Page<RoommatePostResponse> posts = roommatePostService.searchByBudget(minBudget, maxBudget, pageable);
            return ResponseEntity.ok(ApiResponse.success("Budget search completed successfully", posts));
        } catch (Exception e) {
            log.error("Error searching by budget: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("SEARCH_BY_BUDGET_ERROR", "Failed to search by budget: " + e.getMessage()));
        }
    }
    
    /**
     * API tìm kiếm bài đăng theo địa điểm
     * Endpoint: GET /api/roommate-posts/search/location
     * 
     * @param location - Địa điểm mong muốn
     * @param page - Số trang
     * @param size - Kích thước trang
     * @return ResponseEntity - Kết quả tìm kiếm
     */
    @GetMapping("/search/location")
    public ResponseEntity<?> searchByLocation(
            @RequestParam String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, 
                    Sort.by("createdAt").descending());
            
            Page<RoommatePostResponse> posts = roommatePostService.searchByLocation(location, pageable);
            return ResponseEntity.ok(ApiResponse.success("Location search completed successfully", posts));
        } catch (Exception e) {
            log.error("Error searching by location: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("SEARCH_BY_LOCATION_ERROR", "Failed to search by location: " + e.getMessage()));
        }
    }
}
