package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.request.masteramenity.MasterAmenityRequest;
import com.trouni.tro_uni.dto.response.MasterAmenity.MasterAmenityResponse;
import com.trouni.tro_uni.entity.MasterAmenity;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.MasterAmenityErrorCode;
import com.trouni.tro_uni.exception.errorcode.RoomErrorCode;
import com.trouni.tro_uni.repository.MasterAmenityRepository;
import com.trouni.tro_uni.repository.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MasterAmenityService {
    private final RoomRepository roomRepository;

    MasterAmenityRepository masterAmenityRepository;

    /**
     * Creates a new master amenity with the provided details.
     *
     * @param request The master amenity request containing name and icon URL
     * @return MasterAmenityResponse containing the created amenity details
     * @throws AppException if an amenity with the same name already exists
     */
    public MasterAmenityResponse createMasterAmenity(MasterAmenityRequest request) {
        // Check if amenity with the same name already exists
        if (masterAmenityRepository.existsByName(request.getName())) {
            throw new AppException(MasterAmenityErrorCode.MASTER_AMENITY_ALREADY_EXISTS);
        }

        // Create new amenity entity and set properties
        MasterAmenity amenity = MasterAmenity.builder()
                .name(request.getName())
                .iconUrl(request.getIcon())
                .build();

        // Save the amenity to database
        MasterAmenity savedAmenity = masterAmenityRepository.save(amenity);
        log.info("Created new master amenity with ID: {}", savedAmenity.getId());
        return MasterAmenityResponse.fromMasterAmenity(savedAmenity);
    }

    /**
     * Retrieves all master amenities from the database.
     *
     * @return List of MasterAmenityResponse containing all available amenities
     */
    public List<MasterAmenityResponse> getAllMasterAmenities() {
        log.info("Retrieving all master amenities");
        // Fetch all amenities and convert to response DTOs
        return masterAmenityRepository.findAll().stream()
                .map(MasterAmenityResponse::fromMasterAmenity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all master amenities associated with a specific room.
     *
     * @param roomId The UUID of the room to get amenities for
     * @return List of MasterAmenityResponse containing amenities for the specified room
     * @throws AppException if the room with the given ID does not exist
     */
    public List<MasterAmenityResponse> getMasterAmenities(UUID roomId) {
        // Validate that the room exists
        if (!roomRepository.existsById(roomId)) {
            throw new AppException(RoomErrorCode.ROOM_NOT_FOUND);
        }
        log.info("Retrieving amenities for room ID: {}", roomId);

        // Fetch amenities associated with the room
        List<MasterAmenity> masterAmenity = masterAmenityRepository.findByRoomId(roomId);
        log.info("Retrieved {} amenities for room ID: {}", masterAmenity.size(), roomId);
        return masterAmenity.stream()
                .map(MasterAmenityResponse::fromMasterAmenity)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing master amenity with new information.
     *
     * @param amenityId The UUID of the amenity to update
     * @param request The master amenity request containing updated name and icon URL
     * @return MasterAmenityResponse containing the updated amenity details
     * @throws AppException if the amenity is not found or if another amenity with the new name already exists
     */
    public MasterAmenityResponse updateMasterAmenity(UUID amenityId, MasterAmenityRequest request) {
        // Find the existing amenity
        MasterAmenity amenity = masterAmenityRepository.findById(amenityId)
                .orElseThrow(() -> new AppException(MasterAmenityErrorCode.MASTER_AMENITY_NOT_FOUND));

        // Check if another amenity with the new name already exists (only if name is being changed)
        if (!amenity.getName().equals(request.getName()) && masterAmenityRepository.existsByName(request.getName())) {
            throw new AppException(MasterAmenityErrorCode.MASTER_AMENITY_ALREADY_EXISTS);
        }

        // Update amenity properties
        amenity.setName(request.getName());
        amenity.setIconUrl(request.getIcon());

        // Save the updated amenity
        MasterAmenity updatedAmenity = masterAmenityRepository.save(amenity);
        log.info("Updated master amenity with ID: {}", updatedAmenity.getId());
        return MasterAmenityResponse.fromMasterAmenity(updatedAmenity);
    }

    /**
     * Deletes a master amenity from the system.
     * Only users with LANDLORD role are authorized to perform this operation.
     *
     * @param currentUser The user attempting to delete the amenity (must be LANDLORD)
     * @param amenityId The UUID of the amenity to delete
     * @throws AppException if the user doesn't have LANDLORD role or if the amenity is not found
     */
    public void deleteMasterAmenity(User currentUser, UUID amenityId) {
        // Check if current user has permission to delete amenities (must be LANDLORD)
        if(currentUser.getRole() != UserRole.LANDLORD){
            throw new AppException(MasterAmenityErrorCode.NO_PERMISSION_TO_MODIFY_MASTER_AMENITY);
        }

        // Find the amenity to delete
        MasterAmenity amenity = masterAmenityRepository.findById(amenityId)
                .orElseThrow(() -> new AppException(MasterAmenityErrorCode.NO_PERMISSION_TO_MODIFY_MASTER_AMENITY));

        // Delete the amenity from database
        masterAmenityRepository.delete(amenity);
        log.info("Deleted master amenity with ID: {}", amenityId);
    }
}
