package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PackageRepository extends JpaRepository<Package, UUID> {
    
    Optional<Package> findByName(String name);
    
    boolean existsByName(String name);
}