package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.response.RoomAnalyticsResponse;
import com.trouni.tro_uni.dto.response.RoomDetailResponse;
import com.trouni.tro_uni.dto.response.ReviewSummaryResponse;
import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.GeneralErrorCode;
import com.trouni.tro_uni.repository.BookmarkRepository;
import com.trouni.tro_uni.repository.ReviewRepository;
import com.trouni.tro_uni.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * RoomService - Service xử lý các thao tác liên quan đến phòng
 * 
 * Chức năng chính:
 * - Xem chi tiết phòng với thông tin đầy đủ
 * - Tăng view count khi xem phòng
 * - Lấy thống kê phòng cho landlord
 * - Quản lý hình ảnh phòng
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {
    
    private final RoomRepository roomRepository;
    private final ReviewRepository reviewRepository;
    private final BookmarkRepository bookmarkRepository;
    
    /**
     * Lấy thông tin chi tiết phòng cho Student users
     * Tự động tăng view count khi xem
     */
    @Transactional
    public RoomDetailResponse getRoomDetail(UUID roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(GeneralErrorCode.RESOURCE_NOT_FOUND));
        
        // Tăng view count
        room.setViewCount(room.getViewCount() + 1);
        roomRepository.save(room);
        
        RoomDetailResponse response = RoomDetailResponse.fromRoom(room);
        
        // Thêm review summary
        response.setReviewSummary(getReviewSummary(room));
        
        // Thêm analytics nếu user có quyền xem
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            // Chỉ hiển thị analytics cho landlord của phòng hoặc admin
            if (isUserAuthorizedForAnalytics(room, username)) {
                response.setAnalytics(getRoomAnalytics(room));
            }
        }
        
        return response;
    }
    
    /**
     * Lấy danh sách phòng có sẵn với phân trang
     */
    public Page<RoomDetailResponse> getAvailableRooms(Pageable pageable) {
        Page<Room> rooms = roomRepository.findByStatus("available", pageable);
        return rooms.map(room -> {
            RoomDetailResponse response = RoomDetailResponse.fromRoom(room);
            response.setReviewSummary(getReviewSummary(room));
            return response;
        });
    }
    
    /**
     * Tìm kiếm phòng theo nhiều tiêu chí
     */
    public Page<RoomDetailResponse> searchRooms(String city, String district, 
                                               java.math.BigDecimal minPrice, 
                                               java.math.BigDecimal maxPrice, 
                                               Pageable pageable) {
        Page<Room> rooms = roomRepository.findByMultipleCriteria(
            "available", city, district, minPrice, maxPrice, pageable);
        
        return rooms.map(room -> {
            RoomDetailResponse response = RoomDetailResponse.fromRoom(room);
            response.setReviewSummary(getReviewSummary(room));
            return response;
        });
    }
    
    /**
     * Lấy thống kê phòng cho landlord
     */
    public RoomAnalyticsResponse getRoomAnalytics(Room room) {
        long bookmarkCount = bookmarkRepository.findByRoom(room).size();
        
        return RoomAnalyticsResponse.builder()
                .totalViews(room.getViewCount())
                .viewsThisWeek(0) //Implement weekly view tracking
                .viewsThisMonth(0) //Implement monthly view tracking
                .bookmarkCount((int) bookmarkCount)
                .lastViewedAt(LocalDateTime.now()) //Track last viewed time
                .isBoosted(room.getBoostExpiresAt() != null && 
                          room.getBoostExpiresAt().isAfter(LocalDateTime.now()))
                .boostExpiresAt(room.getBoostExpiresAt())
                .build();
    }
    
    /**
     * Lấy tóm tắt đánh giá phòng
     */
    private ReviewSummaryResponse getReviewSummary(Room room) {
        Double averageScore = reviewRepository.getAverageScoreByRoom(room);
        long totalReviews = reviewRepository.countByRoom(room);
        
        // Implement star count breakdown
        return ReviewSummaryResponse.builder()
                .averageScore(averageScore != null ? averageScore : 0.0)
                .totalReviews(totalReviews)
                .fiveStarCount(0L)
                .fourStarCount(0L)
                .threeStarCount(0L)
                .twoStarCount(0L)
                .oneStarCount(0L)
                .build();
    }
    
    /**
     * Kiểm tra user có quyền xem analytics không
     */
    private boolean isUserAuthorizedForAnalytics(Room room, String username) {
        // Implement proper authorization check
        return room.getOwner().getUsername().equals(username) ||
               hasAdminRole();
    }
    
    /**
     * Kiểm tra user có role admin không
     */
    private boolean hasAdminRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
