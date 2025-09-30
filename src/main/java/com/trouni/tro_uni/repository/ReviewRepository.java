package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.Review;
import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    
    List<Review> findByRoom(Room room);
    
    List<Review> findByRoomId(UUID roomId);
    
    List<Review> findByUser(User user);
    
    Optional<Review> findByUserAndRoom(User user, Room room);
    
    Page<Review> findByRoomOrderByCreatedAtDesc(Room room, Pageable pageable);
    
    @Query("SELECT AVG(r.score) FROM Review r WHERE r.room = :room")
    Double getAverageScoreByRoom(@Param("room") Room room);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.room = :room")
    long countByRoom(@Param("room") Room room);
    
    boolean existsByUserAndRoom(User user, Room room);
}