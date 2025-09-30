package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage, UUID> {
    
    List<RoomImage> findByRoom(Room room);
    
    List<RoomImage> findByRoomId(UUID roomId);
    
    Optional<RoomImage> findByRoomAndPrimaryTrue(Room room);
    
    Optional<RoomImage> findByRoomIdAndPrimaryTrue(UUID roomId);
    
    List<RoomImage> findByRoomOrderByPrimaryDesc(Room room);
}