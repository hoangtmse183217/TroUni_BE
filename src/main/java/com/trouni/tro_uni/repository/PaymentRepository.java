package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.Payment;
import com.trouni.tro_uni.entity.Subscription;
import com.trouni.tro_uni.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    List<Payment> findByUser(User user);
    
    List<Payment> findByUserId(UUID userId);
    
    List<Payment> findBySubscription(Subscription subscription);
    
    List<Payment> findByStatus(String status);
    
    Page<Payment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    Optional<Payment> findByTransactionCode(String transactionCode);
    
    boolean existsByTransactionCode(String transactionCode);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    BigDecimal sumAmountByStatus(String status);

    long countByStatus(String status);
}
