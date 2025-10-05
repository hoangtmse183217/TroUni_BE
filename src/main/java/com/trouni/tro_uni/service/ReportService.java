package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.request.CreateReportRequest;
import com.trouni.tro_uni.dto.response.ReportResponse;
import com.trouni.tro_uni.dto.response.ReportedContentResponse;
import com.trouni.tro_uni.entity.Report;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.mapper.ReportMapper;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.GeneralErrorCode;
import com.trouni.tro_uni.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ReportService - Service xử lý các thao tác báo cáo vi phạm
 *
 * Chức năng chính:
 * - Tạo báo cáo vi phạm
 * - Lấy danh sách báo cáo
 * - Quản lý trạng thái báo cáo
 * - Xử lý báo cáo (dành cho Manager/Admin)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoommatePostRepository roommatePostRepository;
    private final ReviewRepository reviewRepository;
    private final ReportMapper reportMapper;
    
    private static final List<String> VALID_CONTENT_TYPES = Arrays.asList(
            "room", "user", "roommate_post", "review"
    );
    
    /**
     * Tạo báo cáo vi phạm mới
     */
    @Transactional
    public ReportResponse createReport(CreateReportRequest request) {
        User currentUser = getCurrentUser();
        
        // Validate content type
        if (!VALID_CONTENT_TYPES.contains(request.getReportedContentType())) {
            throw new AppException(GeneralErrorCode.INVALID_INPUT,
                    "Invalid content type. Valid types: " + String.join(", ", VALID_CONTENT_TYPES));
        }
        
        // Validate content exists
        if (!contentExists(request.getReportedContentType(), request.getReportedContentId())) {
            throw new AppException(GeneralErrorCode.RESOURCE_NOT_FOUND, "Reported content not found");
        }
        
        // Check if user already reported this content
        List<Report> existingReports = reportRepository.findByReportedContentTypeAndReportedContentId(
                request.getReportedContentType(), request.getReportedContentId());
        
        boolean alreadyReported = existingReports != null && existingReports.stream()
                .filter(Objects::nonNull)
                .anyMatch(report -> report.getReporter() != null && 
                        report.getReporter().getId().equals(currentUser.getId()));
        
        if (alreadyReported) {
            throw new AppException(GeneralErrorCode.RESOURCE_ALREADY_EXISTS,
                    "You have already reported this content");
        }
        
        Report report = reportMapper.toEntity(request, currentUser);
        report.setStatus("pending");
        
        report = reportRepository.save(report);
        
        log.info("User {} reported {} with ID {}", 
                currentUser.getUsername(), request.getReportedContentType(), request.getReportedContentId());
        
        ReportResponse response = ReportResponse.fromReport(report);
        response.setReportedContent(getReportedContentInfo(
                request.getReportedContentType(), request.getReportedContentId()));
        
        return response;
    }
    
    /**
     * Lấy danh sách báo cáo của user hiện tại
     */
    public List<ReportResponse> getCurrentUserReports() {
        User currentUser = getCurrentUser();
        List<Report> reports = reportRepository.findByReporter(currentUser);
        
        return reports != null ? reports.stream()
                .filter(Objects::nonNull)
                .map(report -> {
                    ReportResponse response = ReportResponse.fromReport(report);
                    response.setReportedContent(getReportedContentInfo(
                            report.getReportedContentType(), report.getReportedContentId()));
                    return response;
                })
                .collect(Collectors.toList()) : new ArrayList<>();
    }
    
    /**
     * Lấy danh sách tất cả báo cáo (dành cho Manager/Admin)
     */
    public Page<ReportResponse> getAllReports(String status, Pageable pageable) {
        User currentUser = getCurrentUser();
        
        // Chỉ Manager và Admin mới có quyền xem tất cả báo cáo
        if (!currentUser.getRole().equals(UserRole.MANAGER) && 
            !currentUser.getRole().equals(UserRole.ADMIN)) {
            throw new AppException(GeneralErrorCode.ACCESS_DENIED,
                    "Only managers and admins can view all reports");
        }
        
        Page<Report> reports;
        if (status != null && !status.isEmpty()) {
            reports = reportRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else {
            reports = reportRepository.findAll(pageable);
        }
        
        return reports.map(report -> {
            ReportResponse response = ReportResponse.fromReport(report);
            response.setReportedContent(getReportedContentInfo(
                    report.getReportedContentType(), report.getReportedContentId()));
            return response;
        });
    }
    
    /**
     * Cập nhật trạng thái báo cáo (dành cho Manager/Admin)
     */
    @Transactional
    public ReportResponse updateReportStatus(UUID reportId, String newStatus) {
        User currentUser = getCurrentUser();
        
        // Chỉ Manager và Admin mới có quyền cập nhật trạng thái
        if (!currentUser.getRole().equals(UserRole.MANAGER) && 
            !currentUser.getRole().equals(UserRole.ADMIN)) {
            throw new AppException(GeneralErrorCode.ACCESS_DENIED,
                    "Only managers and admins can update report status");
        }
        
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(GeneralErrorCode.RESOURCE_NOT_FOUND));
        
        // Validate status
        List<String> validStatuses = Arrays.asList("pending", "reviewed", "resolved", "dismissed");
        if (!validStatuses.contains(newStatus)) {
            throw new AppException(GeneralErrorCode.INVALID_INPUT,
                    "Invalid status. Valid statuses: " + String.join(", ", validStatuses));
        }
        
        reportMapper.updateStatus(newStatus, report);
        report = reportRepository.save(report);
        
        log.info("User {} updated report {} status to {}", 
                currentUser.getUsername(), reportId, newStatus);
        
        ReportResponse response = ReportResponse.fromReport(report);
        response.setReportedContent(getReportedContentInfo(
                report.getReportedContentType(), report.getReportedContentId()));
        
        return response;
    }
    
    /**
     * Lấy thống kê báo cáo theo loại nội dung
     */
    public long getReportCountByContent(String contentType, UUID contentId) {
        return reportRepository.countByReportedContent(contentType, contentId);
    }
    
    /**
     * Kiểm tra nội dung có tồn tại không
     */
    private boolean contentExists(String contentType, UUID contentId) {
        switch (contentType) {
            case "room":
                return roomRepository.existsById(contentId);
            case "user":
                return userRepository.existsById(contentId);
            case "roommate_post":
                return roommatePostRepository.existsById(contentId);
            case "review":
                return reviewRepository.existsById(contentId);
            default:
                return false;
        }
    }
    
    /**
     * Lấy thông tin nội dung bị báo cáo
     */
    private ReportedContentResponse getReportedContentInfo(String contentType, UUID contentId) {
        switch (contentType) {
            case "room":
                return roomRepository.findById(contentId)
                        .map(room -> ReportedContentResponse.builder()
                                .contentId(contentId)
                                .contentType(contentType)
                                .title(room.getTitle())
                                .summary("Room in " + room.getCity() + ", " + room.getDistrict())
                                .exists(true)
                                .build())
                        .orElse(createNotFoundContent(contentType, contentId));

            case "user":
                return userRepository.findById(contentId)
                        .map(user -> ReportedContentResponse.builder()
                                .contentId(contentId)
                                .contentType(contentType)
                                .title(user.getUsername())
                                .summary("User account")
                                .exists(true)
                                .build())
                        .orElse(createNotFoundContent(contentType, contentId));

            case "roommate_post":
                return roommatePostRepository.findById(contentId)
                        .map(post -> ReportedContentResponse.builder()
                                .contentId(contentId)
                                .contentType(contentType)
                                .title(post.getTitle())
                                .summary("Roommate seeking post")
                                .exists(true)
                                .build())
                        .orElse(createNotFoundContent(contentType, contentId));

            case "review":
                return reviewRepository.findById(contentId)
                        .map(review -> ReportedContentResponse.builder()
                                .contentId(contentId)
                                .contentType(contentType)
                                .title("Review")
                                .summary("Room review with score: " + review.getScore())
                                .exists(true)
                                .build())
                        .orElse(createNotFoundContent(contentType, contentId));

            default:
                return createNotFoundContent(contentType, contentId);
        }
    }
    
    /**
     * Tạo response cho nội dung không tồn tại
     */
    private ReportedContentResponse createNotFoundContent(String contentType, UUID contentId) {
        return ReportedContentResponse.builder()
                .contentId(contentId)
                .contentType(contentType)
                .title("Content not found")
                .summary("The reported content no longer exists")
                .exists(false)
                .build();
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
