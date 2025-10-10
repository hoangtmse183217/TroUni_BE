package com.trouni.tro_uni.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * CreateReportRequest - DTO cho yêu cầu tạo báo cáo vi phạm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequest {
    
    @NotBlank(message = "Content type is required")
    private String reportedContentType; // room, user, roommate_post, review
    
    @NotNull(message = "Content ID is required")
    private UUID reportedContentId;
    
    @NotBlank(message = "Reason is required")
    private String reason;
}
