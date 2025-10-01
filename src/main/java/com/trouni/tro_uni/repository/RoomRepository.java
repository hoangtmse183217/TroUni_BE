package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

    Page<Room> findByStatus(String status, Pageable pageable);

    Page<Room> findByOwnerId(UUID ownerId, Pageable pageable);

    @Query("SELECT r FROM Room r WHERE " +
            "(:city IS NULL OR r.city = :city) AND " +
            "(:district IS NULL OR r.district = :district) AND " +
            "(:minPrice IS NULL OR r.pricePerMonth >= :minPrice) AND " +
            "(:maxPrice IS NULL OR r.pricePerMonth <= :maxPrice) AND " +
            "(:minArea IS NULL OR r.areaSqm >= :minArea) AND " +
            "(:maxArea IS NULL OR r.areaSqm <= :maxArea) AND " +
            "r.status = :status")
    Page<Room> findByFilters(
            @Param("city") String city,
            @Param("district") String district,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minArea") BigDecimal minArea,
            @Param("maxArea") BigDecimal maxArea,
            @Param("status") String status,
            Pageable pageable
    );
}
