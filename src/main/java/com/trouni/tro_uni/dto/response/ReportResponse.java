package com.trouni.tro_uni.dto.response;

import com.trouni.tro_uni.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ReportResponse - DTO cho thông tin báo cáo vi phạm
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    
    private UUID id;
    private String reportedContentType;
    private UUID reportedContentId;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
    
    // Reporter information (limited for privacy)
    private ReporterResponse reporter;
    
    // Content information
    private ReportedContentResponse reportedContent;
    
    public static ReportResponse fromReport(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .reportedContentType(report.getReportedContentType())
                .reportedContentId(report.getReportedContentId())
                .reason(report.getReason())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .reporter(ReporterResponse.fromUser(report.getReporter()))
                .build();
    }
}
