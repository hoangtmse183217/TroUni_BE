package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    
    // Additional custom methods can be added here as needed
}