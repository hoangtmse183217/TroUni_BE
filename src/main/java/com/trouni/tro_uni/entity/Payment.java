package com.trouni.tro_uni.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "subscription_id") // Can be null if it's a one-time purchase like manual boost
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private Subscription subscription;
    
    @Column(precision = 12, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Column(name = "transaction_code", unique = true)
    private String transactionCode;
    
    @Column(nullable = false, length = 20)
    private String status; // e.g., pending, completed, failed
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}