package com.trouni.tro_uni.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.ArrayList;
import java.util.UUID;

import java.util.List;

@Entity
@Table(name = "master_amenities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterAmenity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Nationalized
    @Column(nullable = false, length = 100, unique = true)
    private String name; // e.g., Wi-Fi, Air Conditioner, Parking
    
    @Nationalized
    @Column(name = "description")
    private String description;
    
    @Column(name = "icon_url")
    private String iconUrl;
    
    @Column(name = "is_active")
    private Boolean active = true;
    
    // Many-to-many relationship with Room
    @ManyToMany(mappedBy = "amenities")
    private List<Room> rooms = new ArrayList<>();
}