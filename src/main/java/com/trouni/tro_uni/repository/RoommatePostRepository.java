package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.RoommatePost;
import com.trouni.tro_uni.entity.User;
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
public interface RoommatePostRepository extends JpaRepository<RoommatePost, UUID> {
    
    List<RoommatePost> findByAuthor(User author);
    
    List<RoommatePost> findByAuthorId(UUID authorId);
    
    Page<RoommatePost> findByStatus(String status, Pageable pageable);
    
    @Query("SELECT rp FROM RoommatePost rp WHERE rp.budgetMin <= :maxBudget AND rp.budgetMax >= :minBudget")
    Page<RoommatePost> findByBudgetRange(@Param("minBudget") BigDecimal minBudget, 
                                        @Param("maxBudget") BigDecimal maxBudget, 
                                        Pageable pageable);
    
    @Query("SELECT rp FROM RoommatePost rp WHERE LOWER(rp.desiredLocationText) LIKE LOWER(CONCAT('%', :location, '%'))")
    Page<RoommatePost> findByDesiredLocationContaining(@Param("location") String location, Pageable pageable);
    
    Page<RoommatePost> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
}