package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.MasterAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MasterAmenityRepository extends JpaRepository<MasterAmenity, UUID> {
    
    Optional<MasterAmenity> findByName(String name);
    
    boolean existsByName(String name);
}