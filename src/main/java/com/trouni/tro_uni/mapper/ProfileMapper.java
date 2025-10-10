package com.trouni.tro_uni.mapper;

import com.trouni.tro_uni.dto.request.UpdateProfileRequest;
import com.trouni.tro_uni.entity.Profile;
import com.trouni.tro_uni.entity.User;
import org.mapstruct.*;

/**
 * MapStruct mapper for Profile entity and DTOs
 */
@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfileMapper {

    /**
     * Updates existing Profile entity with UpdateProfileRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateProfileFromRequest(UpdateProfileRequest request, @MappingTarget Profile profile);

    /**
     * Updates only specific fields from UpdateProfileRequest to Profile
     */
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateProfileFields(UpdateProfileRequest request, @MappingTarget Profile profile);
}
