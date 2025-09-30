package com.trouni.tro_uni.entity;

import com.trouni.tro_uni.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_verifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserVerification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "id_front_image_url")
    private String idFrontImageUrl;
    
    @Column(name = "id_back_image_url")
    private String idBackImageUrl;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt = LocalDateTime.now();
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @ManyToOne
    @JoinColumn(name = "reviewer_id") // Manager or Admin who reviewed this
    private User reviewer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status;

    @Nationalized
    private String notes; // Reason for rejection, etc.
}