package com.trouni.tro_uni.mapper;

import com.trouni.tro_uni.dto.request.room.RoomRequest;
import com.trouni.tro_uni.dto.request.room.UpdateRoomRequest;
import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.entity.User;
import org.mapstruct.*;

/**
 * MapStruct mapper for Room entity and DTOs
 */
@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomMapper {

    /**
     * Maps RoomRequest to Room entity for creation
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", source = "currentUser")
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Room toEntity(RoomRequest request, User currentUser);

    /**
     * Updates existing Room entity with UpdateRoomRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateRoomFromRequest(UpdateRoomRequest request, @MappingTarget Room room);

    /**
     * Updates only specific fields from UpdateRoomRequest to Room
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateRoomFields(UpdateRoomRequest request, @MappingTarget Room room);
}
