package com.trouni.tro_uni.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * UpdateRoommatePostRequest - DTO cho yêu cầu cập nhật bài đăng tìm roommate
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoommatePostRequest {
    
    private String title;
    
    private String description;
    
    private String desiredLocationText;
    
    @Positive(message = "Budget min must be positive")
    private BigDecimal budgetMin;
    
    @Positive(message = "Budget max must be positive")
    private BigDecimal budgetMax;
    
    private String status; // open, closed
}
