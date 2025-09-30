package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.Subscription;
import com.trouni.tro_uni.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    
    Optional<Subscription> findByUser(User user);
    
    Optional<Subscription> findByUserId(UUID userId);
    
    List<Subscription> findByStatus(String status);
    
    @Query("SELECT s FROM Subscription s WHERE s.status = 'active' AND s.endDate < :currentDate")
    List<Subscription> findExpiredActiveSubscriptions(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT s FROM Subscription s WHERE s.user = :user AND s.status = 'active'")
    Optional<Subscription> findActiveSubscriptionByUser(@Param("user") User user);
}