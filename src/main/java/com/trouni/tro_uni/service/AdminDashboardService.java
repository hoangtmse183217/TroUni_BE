package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.response.AdminDashboardStatsResponse;
import com.trouni.tro_uni.enums.PaymentStatus;
import com.trouni.tro_uni.repository.PaymentRepository;
import com.trouni.tro_uni.repository.ReviewRepository;
import com.trouni.tro_uni.repository.RoomRepository;
import com.trouni.tro_uni.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;

    public AdminDashboardStatsResponse getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalRooms = roomRepository.count();
        long totalReviews = reviewRepository.count();
        long totalTransactions = paymentRepository.countByStatus(PaymentStatus.COMPLETED.name());
        BigDecimal totalRevenue = paymentRepository.sumAmountByStatus(PaymentStatus.COMPLETED.name());

        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        return AdminDashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalRooms(totalRooms)
                .totalReviews(totalReviews)
                .totalTransactions(totalTransactions)
                .totalRevenue(totalRevenue)
                .build();
    }
}
