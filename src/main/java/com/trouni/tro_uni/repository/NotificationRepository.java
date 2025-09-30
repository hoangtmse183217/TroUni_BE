package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.Notification;
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
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    List<Notification> findByUser(User user);
    
    List<Notification> findByUserId(UUID userId);
    
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    List<Notification> findByUserAndReadFalse(User user);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.read = false")
    long countUnreadByUser(@Param("user") User user);
    
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}