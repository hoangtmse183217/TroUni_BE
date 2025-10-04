package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.request.RoomSearchRequest;
import com.trouni.tro_uni.dto.request.masteramenity.MasterAmenityRequest;
import com.trouni.tro_uni.dto.request.room.RoomRequest;
import com.trouni.tro_uni.dto.request.room.UpdateRoomRequest;
import com.trouni.tro_uni.dto.response.RoomListItemResponse;
import com.trouni.tro_uni.dto.response.RoomSummaryResponse;
import com.trouni.tro_uni.dto.response.RoomImagesResponse;
import com.trouni.tro_uni.dto.response.RoomImageResponse;
import com.trouni.tro_uni.dto.response.room.RoomResponse;
import com.trouni.tro_uni.entity.MasterAmenity;
import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.entity.RoomImage;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.RoomErrorCode;
import com.trouni.tro_uni.repository.MasterAmenityRepository;
import com.trouni.tro_uni.repository.RoomRepository;
import com.trouni.tro_uni.repository.RoomImageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomImageRepository roomImageRepository;
    
    MasterAmenityRepository masterAmenityRepository;

    // ================== ORIGINAL SEARCH AND FILTER METHODS (from main branch) ==================

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

    // ================== NEW CRUD METHODS (from nguyenvuong-dev branch) ==================

    /**
     * Create a new room listing
     *
     * @param currentUser - The landlord user creating the room
     * @param request     - Room creation details including title, price, location, etc
     * @return RoomResponse - Details of the created room
     * @throws AppException - If user is not authorized as landlord
     */
    public RoomResponse createRoom(User currentUser, RoomRequest request) {
        if (currentUser.getRole() != UserRole.LANDLORD) {
            throw new AppException(RoomErrorCode.NOT_LANDLORD);
        }

        // Step 1: Create Room and save first to get ID
        Room room = Room.builder()
                .owner(currentUser)
                .title(request.getTitle())
                .description(request.getDescription())
                .roomType(request.getRoomType())
                .streetAddress(request.getStreetAddress())
                .city(request.getCity())
                .district(request.getDistrict())
                .ward(request.getWard())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .pricePerMonth(request.getPricePerMonth())
                .areaSqm(request.getAreaSqm())
                .status("available")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Room savedRoom = roomRepository.save(room); // Save first to get ID

        // Step 2: Process List<String> to RoomImage
        List<RoomImage> savedImages = request.getImages().stream()
                .map(url -> {
                    RoomImage image = new RoomImage();
                    image.setImageUrl(url);
                    image.setPrimary(false);
                    image.setRoom(savedRoom); // Room now has ID
                    return roomImageRepository.save(image);
                })
                .collect(Collectors.toList());
        savedRoom.setImages(savedImages);

        // Step 3: Process amenities from AmenityRequest to MasterAmenity
        List<MasterAmenity> savedAmenities = request.getAmenities().stream()
                .map(dto -> {
                    MasterAmenity amenity = new MasterAmenity();
                    amenity.setName(dto.getName());
                    amenity.setIconUrl(dto.getIcon());
                    return masterAmenityRepository.save(amenity);
                })
                .collect(Collectors.toList());
        savedRoom.setAmenities(savedAmenities);

        // Step 4: Save Room again with images and amenities
        Room finalRoom = roomRepository.save(savedRoom);
        log.info("Created new room with ID: {} by user: {}", finalRoom.getId(), currentUser.getUsername());
        return RoomResponse.fromRoom(finalRoom);
    }

    /**
     * Get room details by ID (UUID version)
     *
     * @param roomId - Unique identifier of the room
     * @return RoomResponse - Room details including images and amenities
     * @throws AppException - When room is not found
     */
    public RoomResponse getRoomById(UUID roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(RoomErrorCode.ROOM_NOT_FOUND));

        room.setViewCount(room.getViewCount() + 1);
        roomRepository.save(room);

        log.info("Retrieved room details for ID: {}", roomId);
        return RoomResponse.fromRoom(room);
    }

    /**
     * Update room information
     *
     * @param currentUser - The landlord user updating the room
     * @param roomId      - ID of the room to update
     * @param request     - Updated room details
     * @return RoomResponse - Updated room information
     * @throws AppException - When room is not found or user is not the owner
     */
    public RoomResponse updateRoom(User currentUser, UUID roomId, UpdateRoomRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(RoomErrorCode.ROOM_NOT_FOUND));

        if (!room.getOwner().getId().equals(currentUser.getId())) {
            throw new AppException(RoomErrorCode.NOT_ROOM_OWNER);
        }

        // Update basic information
        room.setTitle(request.getTitle());
        room.setDescription(request.getDescription());
        room.setRoomType(request.getRoomType());
        room.setStreetAddress(request.getStreetAddress());
        room.setCity(request.getCity());
        room.setDistrict(request.getDistrict());
        room.setWard(request.getWard());
        room.setLatitude(request.getLatitude());
        room.setLongitude(request.getLongitude());
        room.setPricePerMonth(request.getPricePerMonth());
        room.setAreaSqm(request.getAreaSqm());
        room.setStatus(request.getStatus());
        room.setUpdatedAt(LocalDateTime.now());

        // Process List<String> to RoomImage
        List<RoomImage> savedImages = request.getImages().stream()
                .map(url -> {
                    RoomImage image = new RoomImage();
                    image.setImageUrl(url);
                    image.setPrimary(false);
                    image.setRoom(room);
                    return roomImageRepository.save(image);
                })
                .collect(Collectors.toList());
        room.setImages(savedImages);

        // Process amenities from AmenityRequest to Amenity
        List<MasterAmenity> savedAmenities = new ArrayList<>();
        for (MasterAmenityRequest amenityRequest : request.getAmenities()) {
            MasterAmenity amenity = new MasterAmenity();
            amenity.setName(amenityRequest.getName());
            amenity.setIconUrl(amenityRequest.getIcon());
            savedAmenities.add(masterAmenityRepository.save(amenity));
        }
        room.setAmenities(savedAmenities);

        Room updatedRoom = roomRepository.save(room);
        log.info("Updated room with ID: {} by user: {}", roomId, currentUser.getUsername());
        return RoomResponse.fromRoom(updatedRoom);
    }

    /**
     * Delete a room listing
     *
     * @param currentUser - The landlord user deleting the room
     * @param roomId      - ID of the room to delete
     * @throws AppException - When room is not found or user is not the owner
     */
    public void deleteRoom(User currentUser, UUID roomId) {
        Room room = roomRepository.findById(roomId).
                orElseThrow(() -> new AppException(RoomErrorCode.ROOM_NOT_FOUND));

        if (!room.getOwner().getId().equals(currentUser.getId())) {
            throw new AppException(RoomErrorCode.NOT_ROOM_OWNER);
        }

        roomRepository.delete(room);
        log.info("Deleted room with ID: {} by user: {}", roomId, currentUser.getUsername());
    }

    /**
     * Get paginated list of available rooms
     *
     * @param pageable - Pagination information
     * @return Page<RoomResponse> - Paginated list of rooms
     */
    public Page<RoomResponse> getAllRooms(Pageable pageable) {
        return roomRepository.findByStatus("available", pageable)
                .map(RoomResponse::fromRoom);
    }

    /**
     * Get all rooms without pagination (UUID version)
     *
     * @return List of RoomResponse containing all rooms
     */
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(RoomResponse::fromRoom)
                .collect(Collectors.toList());
    }

    // ================== MAPPING METHODS ==================

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