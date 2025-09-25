package com.trouni.tro_uni.dto.request;

import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.enums.AccountStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AdminUpdateUserRequest - DTO cho admin/manager update user
 * <p>
 * Chức năng chính:
 * - Admin/Manager cập nhật thông tin user khác
 * - Có thể thay đổi role và status
 * - Validation cho các trường có thể cập nhật
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateUserRequest {
    
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
    
    /**
     * Role mới
     * - STUDENT, LANDLORD, MANAGER, ADMIN
     * - Chỉ admin mới có thể thay đổi role
     */
    private UserRole role;
    
    /**
     * Status mới
     * - ACTIVE, LOCKED, SUSPENDED, DELETED
     * - Chỉ admin/manager mới có thể thay đổi status
     */
    private AccountStatus status;
}
