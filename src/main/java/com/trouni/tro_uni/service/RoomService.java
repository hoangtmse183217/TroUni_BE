package com.trouni.tro_uni.service;

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
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.RoomErrorCode;
import com.trouni.tro_uni.exception.errorcode.MasterAmenityErrorCode;
import com.trouni.tro_uni.mapper.RoomMapper;
import com.trouni.tro_uni.repository.MasterAmenityRepository;
import com.trouni.tro_uni.repository.RoomRepository;
import com.trouni.tro_uni.repository.RoomImageRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    private final RoomMapper roomMapper;

    @Autowired
    private RoomImageRepository roomImageRepository;

    MasterAmenityRepository masterAmenityRepository;
    RoomImageRepository roomImageRepository;
    RoomRepository roomRepository;

    /**
     * Create a new room listing
     *
     * @param currentUser - The landlord user creating the room
     * @param request     - Room creation details including title, price, location, etc
     * @return RoomResponse - Details of the created room
     * @throws AppException - If user is not authorized as landlord
     */
    public RoomResponse createRoom(User currentUser, RoomRequest request) {

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
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<RoomImage> savedImages = request.getImages().stream()
                    .filter(url -> url != null && !url.trim().isEmpty())
                    .map(url -> {
                        RoomImage image = new RoomImage();
                        image.setImageUrl(url);
                        image.setPrimary(false);
                        image.setRoom(savedRoom); // Room now has ID
                        return roomImageRepository.save(image);
                    })
                    .collect(Collectors.toList());
            savedRoom.setImages(savedImages);
        }

        // Step 3: Process amenities from AmenityRequest to MasterAmenity
        List<MasterAmenity> savedAmenities = new ArrayList<>();
        if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
            savedAmenities = request.getAmenities().stream()
                    .filter(dto -> dto != null && dto.getName() != null && !dto.getName().trim().isEmpty())
                    .map(dto -> getOrCreateMasterAmenity(dto.getName(), dto.getIcon()))
                    .collect(Collectors.toList());
        }
        savedRoom.setAmenities(savedAmenities);

        // Bước 4: Lưu lại Room với ảnh và tiện ích
        Room finalRoom = roomRepository.save(savedRoom);
        log.info("Created new room with ID: {} by user: {}", finalRoom.getId(), currentUser.getUsername());
        return RoomResponse.fromRoom(finalRoom);
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

    // ================== HELPER METHODS ==================

    /**
     * Get or create MasterAmenity by name to avoid duplicates
     */
    private MasterAmenity getOrCreateMasterAmenity(String name, String iconUrl) {
        String amenityName = name.trim();

        // Check if amenity already exists by name
        Optional<MasterAmenity> existingAmenity = masterAmenityRepository.findByName(amenityName);
        if (existingAmenity.isPresent()) {
            log.info("Using existing MasterAmenity: {}", amenityName);
            return existingAmenity.get();
        } else {
            // Create new amenity only if it doesn't exist
            try {
                log.info("Creating new MasterAmenity: {}", amenityName);
                MasterAmenity amenity = new MasterAmenity();
                amenity.setName(amenityName);
                amenity.setIconUrl(iconUrl);
                amenity.setActive(true);
                return masterAmenityRepository.save(amenity);
            } catch (DataIntegrityViolationException e) {
                // Handle case where amenity was created by another thread
                log.warn("MasterAmenity '{}' was created by another process, retrieving existing one", amenityName);
                return masterAmenityRepository.findByName(amenityName)
                        .orElseThrow(() -> new AppException(MasterAmenityErrorCode.MASTER_AMENITY_NOT_FOUND, "Failed to create or find MasterAmenity: " + amenityName));
            }
        }
    }

    // ================== MAPPING METHODS ==================

    private RoomListItemResponse toRoomListItemResponse(Room room) {
        return new RoomListItemResponse(
                room.getId(),
                room.getTitle(),
                String.join(", ", room.getStreetAddress(), room.getWard(), room.getDistrict(), room.getCity()),
                room.getRoomType(),
                room.getAreaSqm() != null ? room.getAreaSqm().doubleValue() : null,
                room.getPricePerMonth() != null ? room.getPricePerMonth().intValue() : null,
                getThumbnailUrl(room)
        );
    }

    private RoomSummaryResponse toRoomSummaryResponse(Room room) {
        return new RoomSummaryResponse(
                room.getId(),
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
        if (room.getImages() != null && !room.getImages().isEmpty()) {
            return room.getImages().stream()
                    .filter(RoomImage::isPrimary)
                    .findFirst()
                    .map(RoomImage::getImageUrl)
                    .orElse(room.getImages().getFirst().getImageUrl());
        }
        return null;
    }
}