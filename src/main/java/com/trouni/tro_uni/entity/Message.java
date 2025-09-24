package com.trouni.tro_uni.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private ChatRoom chatRoom;
    
    @ManyToOne
    @JoinColumn(name = "sender_user_id", nullable = false)
    @JsonIgnore // Tránh circular reference khi serialize JSON
    private User sender;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "is_read")
    private boolean read = false;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt = LocalDateTime.now();
}