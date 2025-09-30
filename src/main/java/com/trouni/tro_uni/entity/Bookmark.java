package com.trouni.tro_uni.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookmarks")
@IdClass(BookmarkId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {
    
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private User user;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "room_id")
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private Room room;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}