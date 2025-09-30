package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.ChatRoom;
import com.trouni.tro_uni.entity.Message;
import com.trouni.tro_uni.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    
    List<Message> findByChatRoom(ChatRoom chatRoom);
    
    List<Message> findByChatRoomId(UUID chatRoomId);
    
    Page<Message> findByChatRoomOrderBySentAtDesc(ChatRoom chatRoom, Pageable pageable);
    
    List<Message> findBySender(User sender);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.chatRoom = :chatRoom AND m.read = false AND m.sender != :currentUser")
    long countUnreadByChatRoomAndNotSender(@Param("chatRoom") ChatRoom chatRoom, @Param("currentUser") User currentUser);
    
    List<Message> findByChatRoomAndReadFalse(ChatRoom chatRoom);
}