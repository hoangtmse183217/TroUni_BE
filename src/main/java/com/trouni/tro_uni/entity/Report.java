package com.trouni.tro_uni.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "reporter_user_id", nullable = false)
    private User reporter;
    
    // Polymorphic relationship to report different content types
    @Column(name = "reported_content_type", nullable = false)
    private String reportedContentType; // e.g., room, user, roommate_post, review
    
    @Column(name = "reported_content_id", nullable = false)
    private UUID reportedContentId;
    
    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String reason;
    
    @Column(length = 20)
    private String status = "pending"; // e.g., pending, reviewed, resolved
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}