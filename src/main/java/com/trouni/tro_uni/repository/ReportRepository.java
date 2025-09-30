package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.Report;
import com.trouni.tro_uni.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    
    List<Report> findByReporter(User reporter);
    
    List<Report> findByStatus(String status);
    
    Page<Report> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    
    List<Report> findByReportedContentTypeAndReportedContentId(String contentType, UUID contentId);
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.reportedContentType = :contentType AND r.reportedContentId = :contentId")
    long countByReportedContent(@Param("contentType") String contentType, @Param("contentId") UUID contentId);
}