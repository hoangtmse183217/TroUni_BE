package com.trouni.tro_uni.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * CreateRoommatePostRequest - DTO cho yêu cầu tạo bài đăng tìm roommate
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoommatePostRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    private String desiredLocationText;
    
    @Positive(message = "Budget min must be positive")
    private BigDecimal budgetMin;
    
    @Positive(message = "Budget max must be positive")
    private BigDecimal budgetMax;
}
