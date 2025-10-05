package com.trouni.tro_uni.mapper;

import com.trouni.tro_uni.dto.request.CreateReportRequest;
import com.trouni.tro_uni.entity.Report;
import com.trouni.tro_uni.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Report entity and DTOs
 */
@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReportMapper {

    /**
     * Maps CreateReportRequest to Report entity for creation
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reporter", source = "currentUser")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Report toEntity(CreateReportRequest request, User currentUser);

    /**
     * Updates only specific fields for status update
     */
    void updateStatus(String status, @MappingTarget Report report);
}
