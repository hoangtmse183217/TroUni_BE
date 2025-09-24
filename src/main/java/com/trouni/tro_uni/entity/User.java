package com.trouni.tro_uni.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * User Entity - Entity đại diện cho người dùng trong hệ thống
 * 
 * Chức năng chính:
 * - Lưu trữ thông tin cơ bản của user (username, email, password)
 * - Implement UserDetails để tích hợp với Spring Security
 * - Quản lý role và trạng thái verification
 * - Liên kết với Profile entity
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    // ===============================
    // Primary Key và Basic Fields
    // ===============================
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_name", nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    // ===============================
    // Role và Status Fields
    // ===============================
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.STUDENT;

    @Column(name = "is_phone_verified")
    private boolean phoneVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "id_verification_status")
    private VerificationStatus idVerificationStatus = VerificationStatus.NOT_VERIFIED;

    // ===============================
    // Relationship Fields
    // ===============================
    
    /**
     * One-to-one relationship với Profile
     * - mappedBy: Profile entity sẽ có field "user" để reference
     * - cascade: Khi xóa User thì cũng xóa Profile
     * - fetch: Lazy loading để tối ưu performance
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private Profile profile;

    // ===============================
    // Audit Fields
    // ===============================
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ===============================
    // UserDetails Implementation
    // ===============================
    
    /**
     * Trả về danh sách quyền của user
     * 
     * Format: "ROLE_" + role.name()
     * Ví dụ: "ROLE_STUDENT", "ROLE_ADMIN"
     * 
     * @return Collection<GrantedAuthority> - Danh sách quyền
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Trả về password đã được mã hóa
     * 
     * @return String - Password hash
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Trả về username (không phải email)
     * 
     * Quan trọng: Phải return username, không phải email
     * để phân biệt được login bằng username vs email
     * 
     * @return String - Username
     */
    @Override
    public String getUsername() {
        return username; // Return actual username, not email
    }

    /**
     * Kiểm tra tài khoản có hết hạn không
     * 
     * @return boolean - true nếu tài khoản chưa hết hạn
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // Nếu có logic expire thì xử lý ở đây
    }

    /**
     * Kiểm tra tài khoản có bị khóa không
     * 
     * @return boolean - true nếu tài khoản chưa bị khóa
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // Nếu có logic locked thì xử lý ở đây
    }

    /**
     * Kiểm tra credentials (password) có hết hạn không
     * 
     * @return boolean - true nếu credentials chưa hết hạn
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Nếu có logic hết hạn password thì xử lý ở đây
    }

    /**
     * Kiểm tra tài khoản có được kích hoạt không
     * 
     * @return boolean - true nếu tài khoản đã được kích hoạt
     */
    @Override
    public boolean isEnabled() {
        return true; // Always enabled for now
    }

    // ===============================
    // Lifecycle Callbacks
    // ===============================
    
    /**
     * Callback trước khi update entity
     * Tự động cập nhật updatedAt khi có thay đổi
     */
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
