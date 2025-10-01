package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.request.room.RoomImageRequest;
import com.trouni.tro_uni.dto.response.room.RoomImageResponse;
import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.entity.RoomImage;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.RoomErrorCode;
import com.trouni.tro_uni.repository.RoomImageRepository;
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
public class RoomImageService {
    RoomImageRepository roomImageRepository;
    RoomRepository roomRepository;

    /**
     * Add a new image to a room
     * <p>
     * @param currentUser - The landlord user adding the image
     * @param roomId - ID of the room to add the image to
     * @param request - Image details, e.g., URL
     * @return RoomImageResponse - Details of the created image
     * @throws AppException - If room is not found or user is not the owner
     */
    public RoomImageResponse createRoomImage(User currentUser, UUID roomId, RoomImageRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(RoomErrorCode.ROOM_NOT_FOUND));

        if (!room.getOwner().getId().equals(currentUser.getId())) {
            throw new AppException(RoomErrorCode.NOT_ROOM_OWNER);
        }

        if(currentUser.getRole() != UserRole.LANDLORD){
            throw new AppException(RoomErrorCode.NOT_LANDLORD);
        }

        RoomImage roomImage = RoomImage.builder()
                .room(room)
                .imageUrl(request.getImageUrl())
                .primary(true)
                .build();

        RoomImage savedImage = roomImageRepository.save(roomImage);
        log.info("Added new image with ID: {} to room ID: {} by user: {}", savedImage.getId(), roomId, currentUser.getUsername());
        return RoomImageResponse.fromRoomImage(savedImage);
    }

    /**
     * Get all images for a specific room
     * <p>
     * @param roomId - Unique identifier of the room
     * @return List<RoomImageResponse> - A list of images for the room
     * @throws AppException - When room is not found
     */
    public List<RoomImageResponse> getRoomImages(UUID roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new AppException(RoomErrorCode.ROOM_NOT_FOUND);
        }
        log.info("Retrieving images for room ID: {}", roomId);

        List<RoomImage> roomImages = roomImageRepository.findByRoomId(roomId);
        log.info("Retrieved {} images for room ID: {}", roomImages.size(), roomId);
        return roomImages.stream()
                .map(RoomImageResponse::fromRoomImage)
                .collect(Collectors.toList());
    }

    /**
     * Delete an image from a room
     * <p>
     * @param currentUser - The landlord user deleting the image
     * @param imageId - ID of the image to delete
     * @throws AppException - When image is not found or user is not the owner
     */
    public void deleteRoomImage(User currentUser, UUID imageId) {
        if(currentUser.getRole() != UserRole.LANDLORD){
            throw new AppException(RoomErrorCode.NOT_LANDLORD);
        }
        RoomImage roomImage = roomImageRepository.findById(imageId)
                .orElseThrow(() -> new AppException(RoomErrorCode.ROOM_IMAGE_NOT_FOUND));

        Room room = roomImage.getRoom();
        if (!room.getOwner().getId().equals(currentUser.getId())) {
            throw new AppException(RoomErrorCode.NOT_ROOM_OWNER);
        }

        roomImageRepository.delete(roomImage);
        log.info("Deleted image with ID: {} from room ID: {} by user: {}", imageId, room.getId(), currentUser.getUsername());
    }
}
