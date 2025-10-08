package com.trouni.tro_uni.controller;


import com.trouni.tro_uni.dto.request.landlord.UpdateRoomStatusRequest;
import com.trouni.tro_uni.dto.response.MessageResponse;
import com.trouni.tro_uni.dto.response.landlord.DashboardStatsResponse;
import com.trouni.tro_uni.dto.response.landlord.RoomPerformanceResponse;
import com.trouni.tro_uni.service.LandlordDashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/landlord")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LANDLORD')")
public class LandlordDashboardController {

    private final LandlordDashboardService landlordDashboardService;

    @GetMapping("/dashboard/statistics")
    public ResponseEntity<DashboardStatsResponse> getDashboardStatistics() {
        return ResponseEntity.ok(landlordDashboardService.getOverviewStatistics());
    }

    @GetMapping("/dashboard/rooms/performance")
    public ResponseEntity<List<RoomPerformanceResponse>> getRoomPerformance() {
        return ResponseEntity.ok(landlordDashboardService.getRoomPerformanceMetrics());
    }

    @GetMapping("/dashboard/rooms/{roomId}/views")
    public ResponseEntity<Long> getViewCountAnalytics(@PathVariable UUID roomId) {
        return ResponseEntity.ok(landlordDashboardService.getViewCount(roomId));
    }

    @PatchMapping("/rooms/{roomId}/status")
    public ResponseEntity<MessageResponse> updateRoomStatus(
            @PathVariable UUID roomId,
            @Valid @RequestBody UpdateRoomStatusRequest request) {
        landlordDashboardService.updateRoomStatus(roomId, request);
        return ResponseEntity.ok(new MessageResponse("Room status updated successfully to " + request.getStatus().getValue()));
    }
}