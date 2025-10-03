package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.request.CreateReportRequest;
import com.trouni.tro_uni.dto.response.ReportResponse;
import com.trouni.tro_uni.service.ReportService;
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
import java.util.Map;
import java.util.UUID;

/**
 * ReportController - Controller xử lý các API báo cáo vi phạm
 * 
 * Chức năng chính:
 * - Tạo báo cáo vi phạm
 * - Lấy danh sách báo cáo
 * - Quản lý trạng thái báo cáo (Manager/Admin)
 * - Thống kê báo cáo
 */
@Slf4j
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportController {
    
    private final ReportService reportService;
    
    /**
     * API tạo báo cáo vi phạm
     * Endpoint: POST /api/reports
     * 
     * @param request - Thông tin báo cáo
     * @return ResponseEntity - Báo cáo đã tạo
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('LANDLORD')")
    public ResponseEntity<?> createReport(@Valid @RequestBody CreateReportRequest request) {
        try {
            ReportResponse report = reportService.createReport(request);
            return ResponseEntity.ok(ApiResponse.success("Report created successfully", report));
        } catch (Exception e) {
            log.error("Error creating report: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("CREATE_REPORT_ERROR", "Failed to create report: " + e.getMessage()));
        }
    }
    
    /**
     * API lấy danh sách báo cáo của user hiện tại
     * Endpoint: GET /api/reports/my-reports
     * 
     * @return ResponseEntity - Danh sách báo cáo của user
     */
    @GetMapping("/my-reports")
    @PreAuthorize("hasRole('STUDENT') or hasRole('LANDLORD')")
    public ResponseEntity<?> getCurrentUserReports() {
        try {
            List<ReportResponse> reports = reportService.getCurrentUserReports();
            return ResponseEntity.ok(ApiResponse.success("User reports retrieved successfully", reports));
        } catch (Exception e) {
            log.error("Error getting user reports: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_USER_REPORTS_ERROR", "Failed to get user reports: " + e.getMessage()));
        }
    }
    
    /**
     * API lấy danh sách tất cả báo cáo (dành cho Manager/Admin)
     * Endpoint: GET /api/reports
     * 
     * @param status - Trạng thái báo cáo (pending, reviewed, resolved, dismissed)
     * @param page - Số trang
     * @param size - Kích thước trang
     * @return ResponseEntity - Danh sách báo cáo có phân trang
     */
    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllReports(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, 
                    Sort.by("createdAt").descending());
            
            Page<ReportResponse> reports = reportService.getAllReports(status, pageable);
            return ResponseEntity.ok(ApiResponse.success("Reports retrieved successfully", reports));
        } catch (Exception e) {
            log.error("Error getting all reports: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_REPORTS_ERROR", "Failed to get reports: " + e.getMessage()));
        }
    }
    
    /**
     * API cập nhật trạng thái báo cáo (dành cho Manager/Admin)
     * Endpoint: PUT /api/reports/{reportId}/status
     * 
     * @param reportId - ID của báo cáo
     * @param request - Trạng thái mới
     * @return ResponseEntity - Báo cáo đã cập nhật
     */
    @PutMapping("/{reportId}/status")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateReportStatus(@PathVariable UUID reportId, 
                                               @RequestBody Map<String, String> request) {
        try {
            String newStatus = request.get("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("MISSING_STATUS", "Status is required"));
            }
            
            ReportResponse report = reportService.updateReportStatus(reportId, newStatus);
            return ResponseEntity.ok(ApiResponse.success("Report status updated successfully", report));
        } catch (Exception e) {
            log.error("Error updating report status: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("UPDATE_REPORT_STATUS_ERROR", "Failed to update report status: " + e.getMessage()));
        }
    }
    
    /**
     * API lấy thống kê báo cáo theo nội dung
     * Endpoint: GET /api/reports/stats/{contentType}/{contentId}
     * 
     * @param contentType - Loại nội dung
     * @param contentId - ID nội dung
     * @return ResponseEntity - Số lượng báo cáo
     */
    @GetMapping("/stats/{contentType}/{contentId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getReportStats(@PathVariable String contentType, 
                                           @PathVariable UUID contentId) {
        try {
            long reportCount = reportService.getReportCountByContent(contentType, contentId);
            
            Map<String, Object> stats = Map.of(
                    "contentType", contentType,
                    "contentId", contentId,
                    "reportCount", reportCount
            );
            
            return ResponseEntity.ok(ApiResponse.success("Report statistics retrieved successfully", stats));
        } catch (Exception e) {
            log.error("Error getting report stats: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_REPORT_STATS_ERROR", "Failed to get report statistics: " + e.getMessage()));
        }
    }
    
    /**
     * API lấy danh sách loại báo cáo hợp lệ
     * Endpoint: GET /api/reports/categories
     * 
     * @return ResponseEntity - Danh sách loại báo cáo
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getReportCategories() {
        try {
            List<Map<String, String>> categories = List.of(
                    Map.of("type", "room", "description", "Room listing"),
                    Map.of("type", "user", "description", "User account"),
                    Map.of("type", "roommate_post", "description", "Roommate seeking post"),
                    Map.of("type", "review", "description", "Room review")
            );
            
            return ResponseEntity.ok(ApiResponse.success("Report categories retrieved successfully", categories));
        } catch (Exception e) {
            log.error("Error getting report categories: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_REPORT_CATEGORIES_ERROR", "Failed to get report categories: " + e.getMessage()));
        }
    }
    
    /**
     * API lấy danh sách trạng thái báo cáo hợp lệ
     * Endpoint: GET /api/reports/statuses
     * 
     * @return ResponseEntity - Danh sách trạng thái
     */
    @GetMapping("/statuses")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getReportStatuses() {
        try {
            List<Map<String, String>> statuses = List.of(
                    Map.of("status", "pending", "description", "Waiting for review"),
                    Map.of("status", "reviewed", "description", "Under investigation"),
                    Map.of("status", "resolved", "description", "Issue resolved"),
                    Map.of("status", "dismissed", "description", "Report dismissed")
            );
            
            return ResponseEntity.ok(ApiResponse.success("Report statuses retrieved successfully", statuses));
        } catch (Exception e) {
            log.error("Error getting report statuses: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_REPORT_STATUSES_ERROR", "Failed to get report statuses: " + e.getMessage()));
        }
    }
}
