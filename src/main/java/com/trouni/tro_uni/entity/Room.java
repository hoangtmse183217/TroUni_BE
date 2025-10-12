package com.trouni.tro_uni.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trouni.tro_uni.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rooms", indexes = {
    @Index(name = "idx_owner_user_id", columnList = "owner_user_id"),
    @Index(name = "idx_city_district", columnList = "city, district")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner; // Must be a user with landlord role
    
    @Nationalized
    @Column(nullable = false, length = 255)
    private String title;

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType = RoomType.PHONG_TRO;
    // Address information
    @Nationalized
    @Column(name = "street_address", length = 255)
    private String streetAddress;
    
    @Nationalized
    @Column(length = 100)
    private String city;

    @Nationalized
    @Column(length = 100)
    private String district;

    @Nationalized
    @Column(length = 100)
    private String ward;
    
    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;
    
    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;
    
    @Column(name = "price_per_month", precision = 12, scale = 0, nullable = false)
    private BigDecimal pricePerMonth;
    
    @Column(name = "area_sqm", precision = 5, scale = 2)
    private BigDecimal areaSqm;
    
    @Column(length = 50)
    private String status = "available"; // available, rented, hidden
    
    // Analytics and premium features
    @Column(name = "view_count")
    private int viewCount = 0;
    
    @Column(name = "boost_expires_at")
    private LocalDateTime boostExpiresAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Relationships
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private List<RoomImage> images = new ArrayList<>();
    
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private List<Review> reviews;
    
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private List<Bookmark> bookmarks;
    
    @ManyToMany
    @JoinTable(
        name = "room_amenities",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private List<MasterAmenity> amenities;
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}