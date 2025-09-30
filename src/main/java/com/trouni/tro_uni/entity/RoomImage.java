package com.trouni.tro_uni.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "room_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private Room room;
    
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    
    @Column(name = "is_primary")
    private boolean primary = false; // To set a cover image for the room
}