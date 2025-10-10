package com.trouni.tro_uni.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "roommate_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoommatePost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "author_user_id", nullable = false)
    private User author; // Must be a user with student role
    
    @Nationalized
    @Column(nullable = false, length = 255)
    private String title;
    
    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;
    
    @Nationalized
    @Column(name = "desired_location_text")
    private String desiredLocationText; // User-input text like "Near University of Science"
    
    @Column(name = "budget_min", precision = 12, scale = 0)
    private BigDecimal budgetMin;
    
    @Column(name = "budget_max", precision = 12, scale = 0)
    private BigDecimal budgetMax;
    
    @Column(length = 20)
    private String status = "open"; // open, closed
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}