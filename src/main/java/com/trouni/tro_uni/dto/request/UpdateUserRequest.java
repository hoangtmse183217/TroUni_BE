package com.trouni.tro_uni.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UpdateUserRequest - DTO cho update user của bản thân
 * <p>
 * Chức năng chính:
 * - Cập nhật thông tin user của bản thân
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
public class UpdateUserRequest {
    
    /**
     * Username mới
     * - Tối đa 100 ký tự
     * - Hỗ trợ Unicode (nvarchar)
     * - Phải unique
     */
    @Size(max = 100, message = "Username must not exceed 100 characters")
    private String username;
    
    /**
     * Email mới
     * - Format email hợp lệ
     * - Tối đa 100 ký tự
     * - Phải unique
     */
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
}
