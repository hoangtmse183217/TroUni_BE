package com.trouni.tro_uni.mapper;

import com.trouni.tro_uni.dto.request.UpdateRoommatePostRequest;
import com.trouni.tro_uni.entity.RoommatePost;
import org.mapstruct.*;

/**
 * MapStruct mapper for RoommatePost entity and DTOs
 */
@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoommatePostMapper {

    /**
     * Updates existing RoommatePost entity with UpdateRoommatePostRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateRoommatePostFromRequest(UpdateRoommatePostRequest request, @MappingTarget RoommatePost post);

    /**
     * Updates only specific fields from UpdateRoommatePostRequest to RoommatePost
     */
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateRoommatePostFields(UpdateRoommatePostRequest request, @MappingTarget RoommatePost post);
}
