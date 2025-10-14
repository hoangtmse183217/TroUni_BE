package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.Bookmark;
import com.trouni.tro_uni.entity.BookmarkId;
import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId> {
    
    List<Bookmark> findByUser(User user);
    
    List<Bookmark> findByRoom(Room room);
    
    Page<Bookmark> findByUser(User user, Pageable pageable);
    
    Page<Bookmark> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    boolean existsByUserAndRoom(User user, Room room);
    
    void deleteByUserAndRoom(User user, Room room);
}