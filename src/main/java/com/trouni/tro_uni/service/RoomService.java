package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.request.RoomSearchRequest;
import com.trouni.tro_uni.dto.response.RoomListItemResponse;
import com.trouni.tro_uni.dto.response.RoomSummaryResponse;
import com.trouni.tro_uni.dto.response.RoomImagesResponse;
import com.trouni.tro_uni.dto.response.RoomImageResponse;
import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.entity.RoomImage;
import com.trouni.tro_uni.repository.RoomRepository;
import com.trouni.tro_uni.repository.RoomImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomImageRepository roomImageRepository;

    /**
     * Search rooms with multiple filters: location, price range, roomType
     */
    public List<RoomListItemResponse> searchRooms(RoomSearchRequest request) {
        List<Room> rooms = roomRepository.findAll();

        // filter by location (city, district)
        if (request.getLocation() != null) {
            String[] loc = request.getLocation().split(",");
            String city = loc.length > 0 ? loc[0].trim() : null;
            String district = loc.length > 1 ? loc[1].trim() : null;

            rooms = rooms.stream()
                    .filter(r -> (city == null || city.equalsIgnoreCase(r.getCity())))
                    .filter(r -> (district == null || district.equalsIgnoreCase(r.getDistrict())))
                    .collect(Collectors.toList());
        }

        // filter by price range
        if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            BigDecimal min = BigDecimal.valueOf(request.getMinPrice());
            BigDecimal max = BigDecimal.valueOf(request.getMaxPrice());
            rooms = rooms.stream()
                    .filter(r -> r.getPricePerMonth() != null
                            && r.getPricePerMonth().compareTo(min) >= 0
                            && r.getPricePerMonth().compareTo(max) <= 0)
                    .collect(Collectors.toList());
        }

        // filter by room type
        if (request.getRoomType() != null) {
            rooms = rooms.stream()
                    .filter(r -> request.getRoomType().equals(r.getRoomType()))
                    .collect(Collectors.toList());
        }

        return rooms.stream()
                .map(this::toRoomListItemResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all public rooms
     */
    public List<RoomListItemResponse> getPublicRooms() {
        return roomRepository.findAll().stream()
                .map(this::toRoomListItemResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tóm tắt thông tin của một phòng theo roomId (dạng mostSignificantBits của UUID).
     * Nếu không tìm thấy phòng sẽ trả về null.
     * Có xử lý ngoại lệ khi không tìm thấy phòng hoặc lỗi hệ thống.
     */
    public RoomSummaryResponse getRoomSummary(Long roomId) {
        try {
            Optional<Room> roomOpt = roomRepository.findAll().stream()
                    .filter(r -> r.getId().getMostSignificantBits() == roomId)
                    .findFirst();
            Room room = roomOpt.orElseThrow(() -> new RuntimeException("Room not found"));
            return toRoomSummaryResponse(room);
        } catch (RuntimeException e) {
            // Lỗi không tìm thấy phòng
            return null;
        } catch (Exception e) {
            // Lỗi hệ thống khác
            return null;
        }
    }

    /**
     * Lấy danh sách ảnh của một phòng theo roomId (dạng mostSignificantBits của UUID).
     * Nếu không tìm thấy phòng sẽ trả về danh sách ảnh rỗng.
     * Có xử lý ngoại lệ khi không tìm thấy phòng hoặc lỗi hệ thống.
     */
    public RoomImagesResponse getRoomImages(Long roomId) {
        try {
            Optional<Room> roomOpt = roomRepository.findAll().stream()
                    .filter(r -> r.getId().getMostSignificantBits() == roomId)
                    .findFirst();
            UUID roomUuid = roomOpt.orElseThrow(() -> new RuntimeException("Room not found")).getId();
            List<RoomImage> images = roomImageRepository.findByRoomId(roomUuid);
            List<RoomImageResponse> imageResponses = images.stream()
                    .map(img -> new RoomImageResponse(
                            img.getId() != null ? img.getId().getMostSignificantBits() : null,
                            img.getImageUrl(),
                            img.isPrimary()
                    ))
                    .collect(Collectors.toList());
            return new RoomImagesResponse(imageResponses);
        } catch (RuntimeException e) {
            // Lỗi không tìm thấy phòng
            return new RoomImagesResponse(List.of());
        } catch (Exception e) {
            // Lỗi hệ thống khác
            return new RoomImagesResponse(List.of());
        }
    }

    // ================== Mapping methods ==================

    private RoomListItemResponse toRoomListItemResponse(Room room) {
        return new RoomListItemResponse(
                room.getId() != null ? room.getId().getMostSignificantBits() : null,  // UUID -> Long
                room.getTitle(),
                room.getStreetAddress(),
                room.getRoomType(),
                room.getAreaSqm() != null ? room.getAreaSqm().doubleValue() : null,
                room.getPricePerMonth() != null ? room.getPricePerMonth().intValue() : null,
                getThumbnailUrl(room)
        );
    }

    private RoomSummaryResponse toRoomSummaryResponse(Room room) {
        return new RoomSummaryResponse(
                room.getId() != null ? room.getId().getMostSignificantBits() : null,
                room.getTitle(),
                room.getStreetAddress(),
                room.getRoomType(),
                room.getAreaSqm() != null ? room.getAreaSqm().doubleValue() : null,
                room.getPricePerMonth() != null ? room.getPricePerMonth().intValue() : null,
                room.getDescription(),
                room.getOwner() != null ? room.getOwner().getProfile().getFullName() : null,
                room.getOwner() != null ? room.getOwner().getProfile().getPhoneNumber() : null,
                getThumbnailUrl(room)
        );
    }

    private String getThumbnailUrl(Room room) {
        Optional<RoomImage> thumbnail = roomImageRepository.findByRoomIdAndPrimaryTrue(room.getId());
        return thumbnail.map(RoomImage::getImageUrl).orElse(null);
    }
}
