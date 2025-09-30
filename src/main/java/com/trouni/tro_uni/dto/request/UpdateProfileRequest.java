package com.trouni.tro_uni.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UpdateProfileRequest - DTO cho update profile của bản thân
 * <p>
 * Chức năng chính:
 * - Cập nhật thông tin profile của user hiện tại
 * - Validation cho các trường có thể cập nhật
 * - Hỗ trợ partial update (chỉ cập nhật trường có giá trị)
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    
    /**
     * Họ và tên đầy đủ
     * - Tối đa 150 ký tự
     * - Hỗ trợ Unicode (nvarchar)
     */
    @Size(max = 150, message = "Full name must not exceed 150 characters")
    private String fullName;
    
    /**
     * Giới tính
     * - Các giá trị: male, female, other
     * - Tối đa 20 ký tự
     */
    @Size(max = 20, message = "Gender must not exceed 20 characters")
    private String gender;
    
    /**
     * Số điện thoại
     * - Tối đa 15 ký tự
     * - Format: số điện thoại
     */
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    private String phoneNumber;
    
    /**
     * URL ảnh đại diện
     * - URL hợp lệ
     * - Có thể là link từ Google hoặc upload
     */
    private String avatarUrl;
    
    /**
     * Badge/Chứng chỉ
     * - Ví dụ: "Tin uy tín", "Top chủ trọ"
     * - Tối đa 100 ký tự
     */
    @Size(max = 100, message = "Badge must not exceed 100 characters")
    private String badge;
}
