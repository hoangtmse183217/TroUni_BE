package com.trouni.tro_uni.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private User user; // A landlord has one active subscription at a time
    
    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private Package packageEntity;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(nullable = false, length = 20)
    private String status; // e.g., active, expired, cancelled
    
    // Relationships
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private List<Payment> payments;
}