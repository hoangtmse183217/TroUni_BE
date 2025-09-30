package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.Profile;
import com.trouni.tro_uni.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    
    Optional<Profile> findByUser(User user);
    
    Optional<Profile> findByUserId(UUID userId);
    
    Optional<Profile> findByPhoneNumber(String phoneNumber);
    
    boolean existsByPhoneNumber(String phoneNumber);
}