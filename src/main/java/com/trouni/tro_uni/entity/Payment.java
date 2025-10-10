package com.trouni.tro_uni.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trouni.tro_uni.enums.PaymentMethod;
import com.trouni.tro_uni.enums.PaymentStatus;
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
    
    @Column(precision = 12, scale = 0, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // Stored as String for database compatibility
    
    @Column(name = "transaction_code", unique = true)
    private String transactionCode;
    
    @Column(nullable = false, length = 20)
    private String status; // Stored as String for database compatibility
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Helper methods to work with enums
    public PaymentMethod getPaymentMethodEnum() {
        return paymentMethod != null ? PaymentMethod.valueOf(paymentMethod) : null;
    }
    
    public void setPaymentMethodEnum(PaymentMethod method) {
        this.paymentMethod = method != null ? method.name() : null;
    }
    
    public PaymentStatus getStatusEnum() {
        return status != null ? PaymentStatus.valueOf(status) : null;
    }
    
    public void setStatusEnum(PaymentStatus paymentStatus) {
        this.status = paymentStatus != null ? paymentStatus.name() : null;
    }
}