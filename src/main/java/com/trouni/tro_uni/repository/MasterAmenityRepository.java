package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.MasterAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MasterAmenityRepository extends JpaRepository<MasterAmenity, UUID> {
    

    Optional<MasterAmenity> findByName(String name);

    @Query("SELECT ma FROM MasterAmenity ma JOIN ma.rooms r WHERE r.id = :roomId")
    List<MasterAmenity> findByRoomId(@Param("roomId") UUID roomId);

    boolean existsByName(String name);

}