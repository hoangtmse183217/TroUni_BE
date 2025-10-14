package com.trouni.tro_uni.mapper;

import com.trouni.tro_uni.dto.request.AdminUpdateUserRequest;
import com.trouni.tro_uni.dto.request.UpdateUserRequest;
import com.trouni.tro_uni.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for User entity and DTOs
 */
@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    /**
     * Updates only specific fields from UpdateUserRequest to User
     * Note: This only updates fields that are not null in the request
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "googleAccount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateUserFields(UpdateUserRequest request, @MappingTarget User user);

    /**
     * Updates only specific fields from AdminUpdateUserRequest to User
     * Note: This only updates fields that are not null in the request
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "googleAccount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateUserFields(AdminUpdateUserRequest request, @MappingTarget User user);
}
