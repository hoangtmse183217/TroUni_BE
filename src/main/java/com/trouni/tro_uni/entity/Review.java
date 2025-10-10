package com.trouni.tro_uni.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_user_room_unique", columnList = "user_id, room_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private Room room;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private User user;
    
    @Column
    private Integer score; // Rating from 1 to 5

    @Nationalized
    private String comment;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}