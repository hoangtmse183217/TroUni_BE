package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.request.review.ReviewRequest;
import com.trouni.tro_uni.dto.response.review.ReviewResponse;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * ReviewController - Controller to handle APIs related to room reviews.

 * Main functions:
 * - Create a new review for a room.
 * - Get all reviews for a specific room.
 * - Update an existing review.
 * - Delete a review.
 *
 * @author TroUni Team
 * @version 1.0
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {

    ReviewService reviewService;

    /**
     * API to create a new review for a room.
     * <p>
     * Endpoint: POST /api/v1/reviews/{roomId}
     * <p>
     * Headers:
     * Authorization: Bearer <JWT_TOKEN>
     * <p>
     * @param currentUser The currently authenticated user.
     * @param roomId The ID of the room to be reviewed.
     * @param request The review content (rating and comment).
     * @return ResponseEntity - Response containing the created review.
     */
    @PostMapping("/{roomId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID roomId,
            @Valid @RequestBody ReviewRequest request) {
        try {
            ReviewResponse review = reviewService.createReview(currentUser, roomId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Review created successfully!", review));
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("REVIEW_CREATION_FAILED", "Failed to create review!"));
        }
    }

    /**
     * API to get all reviews for a specific room.
     * <p>
     * Endpoint: GET /api/v1/reviews/{roomId}
     * <p>
     * @param roomId The ID of the room.
     * @return ResponseEntity - Response containing the list of reviews.
     */
    @GetMapping("/{roomId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByRoom(@PathVariable UUID roomId) {
        try {
            List<ReviewResponse> reviews = reviewService.getReviewsByRoom(roomId);
            return ResponseEntity.ok(ApiResponse.success("Reviews retrieved successfully!", reviews));
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("REVIEW_RETRIEVAL_FAILED", "Failed to retrieve reviews!"));
        }
    }

    /**
     * API to update an existing review.
     * <p>
     * Endpoint: PUT /api/v1/reviews/{reviewId}
     * <p>
     * Headers:
     * Authorization: Bearer <JWT_TOKEN>
     * <p>
     * @param currentUser The currently authenticated user.
     * @param reviewId The ID of the review to be updated.
     * @param request The updated review content.
     * @return ResponseEntity - Response containing the updated review.
     */
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewRequest request) {
        try {
            ReviewResponse updatedReview = reviewService.updateReview(currentUser, reviewId, request);
            return ResponseEntity.ok(ApiResponse.success("Review updated successfully!", updatedReview));
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("REVIEW_UPDATE_FAILED", "Failed to update review!"));
        }
    }

    /**
     * API to delete a review.
     * <p>
     * Endpoint: DELETE /api/v1/reviews/{reviewId}
     * <p>
     * Headers:
     * Authorization: Bearer <JWT_TOKEN>
     * <p>
     * @param currentUser The currently authenticated user.
     * @param reviewId The ID of the review to be deleted.
     * @return ResponseEntity - Response indicating the result of the operation.
     */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID reviewId) {
        try {
            reviewService.deleteReview(currentUser, reviewId);
            return ResponseEntity.ok(ApiResponse.success("Review deleted successfully!", null));
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("REVIEW_DELETION_FAILED", "Failed to delete review!"));
        }
    }
}
