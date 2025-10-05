package com.trouni.tro_uni.mapper;

import com.trouni.tro_uni.dto.request.review.ReviewRequest;
import com.trouni.tro_uni.entity.Review;
import org.mapstruct.*;

/**
 * MapStruct mapper for Review entity and DTOs
 */
@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReviewMapper {

    /**
     * Updates existing Review entity with ReviewRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateReviewFromRequest(ReviewRequest request, @MappingTarget Review review);

    /**
     * Updates only specific fields from ReviewRequest to Review
     */
    void updateReviewFields(ReviewRequest request, @MappingTarget Review review);
}
