package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.request.masteramenity.MasterAmenityRequest;
import com.trouni.tro_uni.dto.request.room.RoomRequest;
import com.trouni.tro_uni.dto.request.room.UpdateRoomRequest;
import com.trouni.tro_uni.dto.response.room.RoomResponse;
import com.trouni.tro_uni.entity.MasterAmenity;
import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.entity.RoomImage;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.RoomErrorCode;
import com.trouni.tro_uni.exception.errorcode.UserErrorCode;
import com.trouni.tro_uni.repository.MasterAmenityRepository;
import com.trouni.tro_uni.repository.RoomImageRepository;
import com.trouni.tro_uni.repository.RoomRepository;
import com.trouni.tro_uni.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {
    MasterAmenityRepository masterAmenityRepository;
    RoomImageRepository roomImageRepository;
    RoomRepository roomRepository;
    UserRepository userRepository;

    /**
     * Create a new room listing
     * <p>
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
                .status(request.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Create image
        List<String> imageUrls = Optional.ofNullable(request.getImages()).orElse(Collections.emptyList());
        List<RoomImage> images = imageUrls.stream()
                .map(url -> RoomImage.builder()
                        .imageUrl(url)
                        .primary(false)
                        .room(room)
                        .build())
                .collect(Collectors.toList());
        room.setImages(images);

        // Create Amenity
//        List<MasterAmenityRequest> amenityDtos = Optional.ofNullable(request.getAmenities()).orElse(Collections.emptyList());
//        List<MasterAmenity> amenities = amenityDtos.stream()
//                .map(dto -> masterAmenityRepository
//                        .findByName(dto.getName())
//                        .orElseGet(() -> {
//                            MasterAmenity newAmenity = new MasterAmenity();
//                            newAmenity.setName(dto.getName());
//                            newAmenity.setIconUrl(dto.getIcon());
//                            return masterAmenityRepository.save(newAmenity);
//                        })
//                )
//                .collect(Collectors.toList());
//        room.setAmenities(amenities);

        Room savedRoom = roomRepository.save(room);
        log.info("Created new room with ID: {} by user: {}", savedRoom.getId(), currentUser.getUsername());
        return RoomResponse.fromRoom(savedRoom);
    }


    /**
     * Get room details by ID
     * <p>
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
     * Get all rooms owned by a specific user (without pagination)
     *
     * @param userId - UUID of the user (landlord)
     * @return List<RoomResponse> - All rooms of the user
     * @throws AppException - When user is not found
     */
    public List<RoomResponse> getRoomsByUserId(UUID userId) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(UserErrorCode.PROFILE_NOT_FOUND));

        // Get all rooms by owner
        List<Room> rooms = roomRepository.findAll().stream()
                .filter(room -> room.getOwner().getId().equals(userId))
                .toList();

        log.info("Retrieved {} rooms for user: {}", rooms.size(), user.getUsername());
        return rooms.stream()
                .map(RoomResponse::fromRoom)
                .collect(Collectors.toList());
    }

    /**
     * Update room information
     * <p>
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

        // Cập nhật thông tin cơ bản
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

        // Xử lý ảnh từ List<String> → RoomImage
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

        // ✅ Xử lý tiện ích từ AmenityRequest → Amenity
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
     * <p>
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
     * <p>
     *
     * @param pageable - Pagination information
     * @return Page<RoomResponse> - Paginated list of rooms
     */
    public Page<RoomResponse> getAllRooms(Pageable pageable) {
        return roomRepository.findByStatus("available", pageable)
                .map(RoomResponse::fromRoom);
    }

    /**
     * Get all rooms without pagination
     *
     * @return List of RoomResponse containing all rooms
     */
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(RoomResponse::fromRoom)
                .collect(Collectors.toList());
    }

    /**
     * Search rooms with filters
     * @param city - City filter
     * @param district - District filter
     * @param minPrice - Minimum price
     * @param maxPrice - Maximum price
     * @param minArea - Minimum area
     * @param maxArea - Maximum area
     * @param pageable - Pagination information
     * @return Page<RoomResponse> - Filtered and paginated rooms
     */
//    public Page<RoomResponse> searchRooms(
//            String city,
//            String district,
//            Double minPrice,
//            Double maxPrice,
//            Double minArea,
//            Double maxArea,
//            Pageable pageable
//    ) {
//        // Convert Double to BigDecimal for price comparison
//        BigDecimal minPriceDecimal = minPrice != null ? BigDecimal.valueOf(minPrice) : null;
//        BigDecimal maxPriceDecimal = maxPrice != null ? BigDecimal.valueOf(maxPrice) : null;
//        BigDecimal minAreaDecimal = minArea != null ? BigDecimal.valueOf(minArea) : null;
//        BigDecimal maxAreaDecimal = maxArea != null ? BigDecimal.valueOf(maxArea) : null;
//
//        return roomRepository.findByFilters(
//                city,
//                district,
//                minPriceDecimal,
//                maxPriceDecimal,
//                minAreaDecimal,
//                maxAreaDecimal,
//                "available",
//                pageable
//        ).map(RoomResponse::fromRoom);
//    }

    /**
     * Get rooms owned by a specific user
     * <p>
     * @param currentUser - The user whose rooms to retrieve
     * @param pageable - Pagination information
     * @return Page<RoomResponse> - Paginated list of user's rooms
     */
//    public Page<RoomResponse> getUserRooms(User currentUser, Pageable pageable) {
//        return roomRepository.findByOwnerId(currentUser.getId(), pageable)
//                .map(RoomResponse::fromRoom);
//    }
}
