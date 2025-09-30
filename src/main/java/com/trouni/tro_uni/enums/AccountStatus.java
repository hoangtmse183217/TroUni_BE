package com.trouni.tro_uni.enums;

/**
 * AccountStatus - Enum cho trạng thái tài khoản
 * <p>
 * Chức năng chính:
 * - Quản lý trạng thái tài khoản user
 * - Kiểm tra quyền đăng nhập
 * - Quản lý lifecycle của tài khoản
 * 
 * @author TroUni Team
 * @version 1.0
 */
public enum AccountStatus {
    
    /**
     * ACTIVE - Tài khoản hoạt động bình thường
     * - User có thể đăng nhập
     * - User có thể sử dụng tất cả tính năng
     * - Trạng thái mặc định khi tạo tài khoản
     */
    ACTIVE,
    
    /**
     * LOCKED - Tài khoản bị khóa
     * - User không thể đăng nhập
     * - Thường do vi phạm quy định hoặc bảo mật
     * - Cần admin unlock để hoạt động lại
     */
    LOCKED,
    
    /**
     * SUSPENDED - Tài khoản bị tạm ngưng
     * - User không thể đăng nhập
     * - Thường do vi phạm nhẹ hoặc tạm thời
     * - Có thể tự động kích hoạt lại sau thời gian
     */
    SUSPENDED,
    
    /**
     * DELETED - Tài khoản đã bị xóa (soft delete)
     * - User không thể đăng nhập
     * - Dữ liệu vẫn được giữ lại để audit
     * - Không thể khôi phục trực tiếp
     */
    DELETED
}



