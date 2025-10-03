package com.trouni.tro_uni.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * ReportedContentResponse - DTO cho thông tin nội dung bị báo cáo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportedContentResponse {
    
    private UUID contentId;
    private String contentType;
    private String title;
    private String summary;
    private boolean exists; // Kiểm tra nội dung có còn tồn tại không
}
