package com.trouni.tro_uni.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "room_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    
    @Column(name = "description")
    private String description;

    @Column(name = "is_primary")
    private boolean primary = false; // To set a cover image for the room
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}