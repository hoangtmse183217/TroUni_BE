package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.request.CreateRoommatePostRequest;
import com.trouni.tro_uni.dto.request.UpdateRoommatePostRequest;
import com.trouni.tro_uni.dto.response.RoommatePostResponse;
import com.trouni.tro_uni.entity.RoommatePost;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.GeneralErrorCode;
import com.trouni.tro_uni.repository.RoommatePostRepository;
import com.trouni.tro_uni.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * RoommatePostService - Service xử lý các thao tác liên quan đến bài đăng tìm roommate
 * 
 * Chức năng chính:
 * - Tạo, sửa, xóa bài đăng tìm roommate
 * - Lấy danh sách bài đăng
 * - Tìm kiếm bài đăng theo tiêu chí
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoommatePostService {
    
    private final RoommatePostRepository roommatePostRepository;
    private final UserRepository userRepository;
    
    /**
     * Tạo bài đăng tìm roommate mới
     */
    @Transactional
    public RoommatePostResponse createRoommatePost(CreateRoommatePostRequest request) {
        User currentUser = getCurrentUser();
        
        // Kiểm tra user có role STUDENT không
        if (!currentUser.getRole().equals(UserRole.STUDENT)) {
            throw new AppException(GeneralErrorCode.ACCESS_DENIED, "Only students can create roommate posts");
        }
        
        // Validate budget range
        if (request.getBudgetMin() != null && request.getBudgetMax() != null && 
            request.getBudgetMin().compareTo(request.getBudgetMax()) > 0) {
            throw new AppException(GeneralErrorCode.INVALID_INPUT, "Budget min cannot be greater than budget max");
        }
        
        RoommatePost post = new RoommatePost();
        post.setAuthor(currentUser);
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setDesiredLocationText(request.getDesiredLocationText());
        post.setBudgetMin(request.getBudgetMin());
        post.setBudgetMax(request.getBudgetMax());
        post.setStatus("open");
        
        post = roommatePostRepository.save(post);
        
        log.info("User {} created roommate post {}", currentUser.getUsername(), post.getId());
        return RoommatePostResponse.fromRoommatePost(post);
    }
    
    /**
     * Cập nhật bài đăng tìm roommate
     */
    @Transactional
    public RoommatePostResponse updateRoommatePost(UUID postId, UpdateRoommatePostRequest request) {
        User currentUser = getCurrentUser();
        RoommatePost post = roommatePostRepository.findById(postId)
                .orElseThrow(() -> new AppException(GeneralErrorCode.RESOURCE_NOT_FOUND));
        
        // Kiểm tra quyền sở hữu
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new AppException(GeneralErrorCode.ACCESS_DENIED, "You can only update your own posts");
        }
        
        // Validate budget range if both are provided
        if (request.getBudgetMin() != null && request.getBudgetMax() != null && 
            request.getBudgetMin().compareTo(request.getBudgetMax()) > 0) {
            throw new AppException(GeneralErrorCode.INVALID_INPUT, "Budget min cannot be greater than budget max");
        }
        
        // Update fields if provided
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            post.setDescription(request.getDescription());
        }
        if (request.getDesiredLocationText() != null) {
            post.setDesiredLocationText(request.getDesiredLocationText());
        }
        if (request.getBudgetMin() != null) {
            post.setBudgetMin(request.getBudgetMin());
        }
        if (request.getBudgetMax() != null) {
            post.setBudgetMax(request.getBudgetMax());
        }
        if (request.getStatus() != null) {
            post.setStatus(request.getStatus());
        }
        
        post = roommatePostRepository.save(post);
        
        log.info("User {} updated roommate post {}", currentUser.getUsername(), postId);
        return RoommatePostResponse.fromRoommatePost(post);
    }
    
    /**
     * Xóa bài đăng tìm roommate
     */
    @Transactional
    public void deleteRoommatePost(UUID postId) {
        User currentUser = getCurrentUser();
        RoommatePost post = roommatePostRepository.findById(postId)
                .orElseThrow(() -> new AppException(GeneralErrorCode.RESOURCE_NOT_FOUND));
        
        // Kiểm tra quyền sở hữu hoặc admin/manager
        if (!post.getAuthor().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(UserRole.ADMIN) &&
            !currentUser.getRole().equals(UserRole.MANAGER)) {
            throw new AppException(GeneralErrorCode.ACCESS_DENIED, "You can only delete your own posts");
        }
        
        roommatePostRepository.delete(post);
        
        log.info("User {} deleted roommate post {}", currentUser.getUsername(), postId);
    }
    
    /**
     * Lấy thông tin chi tiết bài đăng
     */
    public RoommatePostResponse getRoommatePost(UUID postId) {
        RoommatePost post = roommatePostRepository.findById(postId)
                .orElseThrow(() -> new AppException(GeneralErrorCode.RESOURCE_NOT_FOUND));
        
        return RoommatePostResponse.fromRoommatePost(post);
    }
    
    /**
     * Lấy danh sách bài đăng tìm roommate (có phân trang)
     */
    public Page<RoommatePostResponse> getRoommatePosts(String status, Pageable pageable) {
        Page<RoommatePost> posts;
        
        if (status != null && !status.isEmpty()) {
            posts = roommatePostRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else {
            posts = roommatePostRepository.findAll(pageable);
        }
        
        return posts.map(RoommatePostResponse::fromRoommatePost);
    }
    
    /**
     * Lấy danh sách bài đăng của user hiện tại
     */
    public List<RoommatePostResponse> getCurrentUserPosts() {
        User currentUser = getCurrentUser();
        List<RoommatePost> posts = roommatePostRepository.findByAuthor(currentUser);
        
        return posts.stream()
                .map(RoommatePostResponse::fromRoommatePost)
                .collect(Collectors.toList());
    }
    
    /**
     * Tìm kiếm bài đăng theo ngân sách
     */
    public Page<RoommatePostResponse> searchByBudget(BigDecimal minBudget, BigDecimal maxBudget, Pageable pageable) {
        Page<RoommatePost> posts = roommatePostRepository.findByBudgetRange(minBudget, maxBudget, pageable);
        return posts.map(RoommatePostResponse::fromRoommatePost);
    }
    
    /**
     * Tìm kiếm bài đăng theo địa điểm mong muốn
     */
    public Page<RoommatePostResponse> searchByLocation(String location, Pageable pageable) {
        Page<RoommatePost> posts = roommatePostRepository.findByDesiredLocationContaining(location, pageable);
        return posts.map(RoommatePostResponse::fromRoommatePost);
    }
    
    /**
     * Lấy thông tin user hiện tại từ SecurityContext
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(GeneralErrorCode.RESOURCE_NOT_FOUND, "User not found"));
    }
}
