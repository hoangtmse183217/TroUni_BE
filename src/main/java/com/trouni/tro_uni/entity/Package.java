package com.trouni.tro_uni.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Package {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String name; // e.g., Basic, Pro, Elite
    
    @Column(name = "price_per_month", precision = 12, nullable = false)
    private BigDecimal pricePerMonth;
    
    // Package limits and features
    @Column(name = "max_listings", nullable = false)
    private int maxListings = 1; // Max concurrent active posts
    
    @Column(name = "max_images_per_listing", nullable = false)
    private int maxImagesPerListing = 5;
    
    @Column(name = "can_view_stats", nullable = false)
    private boolean canViewStats = false;
    
    @Column(name = "boost_days", nullable = false)
    private int boostDays = 0; // e.g., 3 for Pro, 7 for Elite

    @Nationalized
    @Column(name = "features")
    private String features; // For other flexible features like badges
    
    // Relationships
    @OneToMany(mappedBy = "packageEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private List<Subscription> subscriptions;
}