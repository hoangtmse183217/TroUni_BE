package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.entity.UserVerification;
import com.trouni.tro_uni.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, UUID> {
    
    Optional<UserVerification> findByUser(User user);
    
    Optional<UserVerification> findByUserId(UUID userId);
    
    List<UserVerification> findByStatus(VerificationStatus status);
    
    List<UserVerification> findByReviewer(User reviewer);
}