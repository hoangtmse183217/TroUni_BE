package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

    // ================== ORIGINAL SEARCH METHODS (from main branch) ==================

    Page<Room> findByStatus(String status, Pageable pageable);

    Page<Room> findByRoomType(RoomType roomType, Pageable pageable);

    Page<Room> findByCityAndDistrict(String city, String district, Pageable pageable);

    Page<Room> findByCity(String city, Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.pricePerMonth >= :minPrice AND r.pricePerMonth <= :maxPrice")
    Page<Room> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice,
                                Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.status = :status AND r.city = :city AND r.district = :district " +
            "AND r.pricePerMonth >= :minPrice AND r.pricePerMonth <= :maxPrice")
    Page<Room> findByMultipleCriteria(@Param("status") String status,
                                      @Param("city") String city,
                                      @Param("district") String district,
                                      @Param("minPrice") BigDecimal minPrice,
                                      @Param("maxPrice") BigDecimal maxPrice,
                                      Pageable pageable);

    @Query("SELECT COUNT(r) FROM Room r WHERE r.owner = :owner AND r.status = :status")
    long countByOwnerAndStatus(@Param("owner") User owner, @Param("status") String status);

    List<Room> findTop10ByOrderByViewCountDesc();

    @Query("SELECT r FROM Room r WHERE " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:city IS NULL OR r.city = :city) AND " +
            "(:district IS NULL OR r.district = :district) AND " +
            "(:ward IS NULL OR r.ward = :ward) AND " +
            "(:minPrice IS NULL OR r.pricePerMonth >= :minPrice) AND " +
            "(:maxPrice IS NULL OR r.pricePerMonth <= :maxPrice) AND " +
            "(:minArea IS NULL OR r.areaSqm >= :minArea) AND " +
            "(:maxArea IS NULL OR r.areaSqm <= :maxArea) AND " +
            "(:roomType IS NULL OR r.roomType = :roomType)")
    List<Room> searchAndFilter(
            @Param("status") String status,
            @Param("city") String city,
            @Param("district") String district,
            @Param("ward") String ward,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minArea") BigDecimal minArea,
            @Param("maxArea") BigDecimal maxArea,
            @Param("roomType") RoomType roomType);

    List<Room> findByStatus(String status);

    // ================== NEW METHODS (from nguyenvuong-dev branch) ==================

    Page<Room> findByOwnerId(UUID ownerId, Pageable pageable);

    List<Room> findByOwner(User owner);

    List<Room> findByOwnerId(UUID ownerId);

    @Query("SELECT COUNT(r) FROM Room r WHERE r.owner.id = :ownerId")
    long countByOwnerId(@Param("ownerId") UUID ownerId);
}
