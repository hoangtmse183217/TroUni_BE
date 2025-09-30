package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByRole(UserRole role);
    
    // Hard delete methods for admin
    @Modifying
    @Transactional
    @Query("DELETE FROM Bookmark b WHERE b.user.id = :userId")
    int deleteUserBookmarks(@Param("userId") UUID userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.user.id = :userId")
    int deleteUserReviews(@Param("userId") UUID userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Report r WHERE r.reporter.id = :userId")
    int deleteUserReports(@Param("userId") UUID userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.user.id = :userId")
    int deleteUserNotifications(@Param("userId") UUID userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM UserVerification uv WHERE uv.user.id = :userId")
    int deleteUserVerifications(@Param("userId") UUID userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Room r WHERE r.owner.id = :userId")
    int deleteUserRoomsAndRelated(@Param("userId") UUID userId);
}