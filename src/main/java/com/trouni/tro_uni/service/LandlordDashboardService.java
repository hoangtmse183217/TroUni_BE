package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.request.landlord.UpdateRoomStatusRequest;
import com.trouni.tro_uni.dto.response.landlord.DashboardStatsResponse;
import com.trouni.tro_uni.dto.response.landlord.RoomPerformanceResponse;
import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.RoomStatus;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.RoomErrorCode;
import com.trouni.tro_uni.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LandlordDashboardService {

    private final RoomRepository roomRepository;
    private final AuthService authService; // Giả định service này để lấy user hiện tại

    @Transactional(readOnly = true)
    public DashboardStatsResponse getOverviewStatistics() {
        User currentUser = authService.getCurrentUser();

        long totalRooms = roomRepository.countByOwner(currentUser);
        // Chuyển Enum thành String khi gọi repository
        long activeRooms = roomRepository.countByOwnerAndStatus(currentUser, RoomStatus.AVAILABLE.getValue());
        long totalBookmarks = roomRepository.countTotalBookmarksByOwner(currentUser.getId());
        double averageRating = roomRepository.findAverageRatingByOwner(currentUser.getId());

        return DashboardStatsResponse.builder()
                .totalRooms(totalRooms)
                .activeRooms(activeRooms)
                .totalBookmarks(totalBookmarks)
                .averageRating(averageRating)
                .build();
    }

    @Transactional(readOnly = true)
    public List<RoomPerformanceResponse> getRoomPerformanceMetrics() {
        User currentUser = authService.getCurrentUser();

        // Gọi phương thức repository mới trả về List
        List<Object[]> projections = roomRepository.findRoomPerformanceProjectionByOwnerAsList(currentUser.getId());

        // Map từ List<Object[]> sang List<RoomPerformanceResponse>
        return projections.stream()
                .map(this::mapProjectionToRoomPerformanceResponse)
                .collect(Collectors.toList());
    }

    private RoomPerformanceResponse mapProjectionToRoomPerformanceResponse(Object[] projection) {
        // Phương thức helper này không cần thay đổi
        return RoomPerformanceResponse.builder()
                .roomId((UUID) projection[0])
                .title((String) projection[1])
                .status(RoomStatus.fromValue((String) projection[2]))
                .viewCount(projection[3] != null ? ((Number) projection[3]).longValue() : 0L)
                .bookmarkCount(projection[4] != null ? ((Number) projection[4]).longValue() : 0L)
                .averageRating(projection[5] != null ? ((Number) projection[5]).doubleValue() : 0.0)
                .build();
    }

    public long getViewCount(UUID roomId) {
        User currentUser = authService.getCurrentUser();
        Room room = roomRepository.findByIdAndOwner(roomId, currentUser)
                .orElseThrow(() -> new AppException(RoomErrorCode.NOT_ROOM_OWNER));
        return room.getViewCount();
    }

    @Transactional
    public void updateRoomStatus(UUID roomId, UpdateRoomStatusRequest request) {
        User currentUser = authService.getCurrentUser();
        Room room = roomRepository.findByIdAndOwner(roomId, currentUser)
                .orElseThrow(() -> new AppException(RoomErrorCode.NOT_ROOM_OWNER));

        // Lấy giá trị chuỗi của enum để lưu vào entity
        room.setStatus(request.getStatus().getValue());
        roomRepository.save(room);
    }
}