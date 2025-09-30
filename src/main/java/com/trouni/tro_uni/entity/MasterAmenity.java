package com.trouni.tro_uni.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.UUID;

import java.util.List;

@Entity
@Table(name = "master_amenities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MasterAmenity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Nationalized
    @Column(nullable = false, unique = true, length = 100)
    private String name; // e.g., Wi-Fi, Air Conditioner, Parking
    
    @Column(name = "icon_url")
    private String iconUrl;
    
    // Many-to-many relationship with Room
    @ManyToMany(mappedBy = "amenities")
    private List<Room> rooms;
}