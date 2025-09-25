package com.trouni.tro_uni.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "full_name", length = 150, columnDefinition = "nvarchar(150)")
    private String fullName;
    
    @Column(length = 20)
    private String gender; // male, female, other
    
    @Column(name = "phone_number", length = 15, unique = true)
    private String phoneNumber; // Used for Zalo contact and verification
    
    @Column(name = "avatar_url")
    private String avatarUrl;
    
    @Column
    private String badge; // e.g., "Tin uy tín", "Top chủ trọ"
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}