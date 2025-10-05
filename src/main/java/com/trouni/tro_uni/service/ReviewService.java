 package com.trouni.tro_uni.service;

 import com.trouni.tro_uni.dto.request.review.ReviewRequest;
 import com.trouni.tro_uni.dto.response.review.ReviewResponse;
 import com.trouni.tro_uni.entity.Review;
 import com.trouni.tro_uni.entity.Room;
 import com.trouni.tro_uni.entity.User;
 import com.trouni.tro_uni.enums.UserRole;
 import com.trouni.tro_uni.exception.AppException;
 import com.trouni.tro_uni.exception.errorcode.ReviewErrorCode;
 import com.trouni.tro_uni.exception.errorcode.RoomErrorCode;
 import com.trouni.tro_uni.repository.ReviewRepository;
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
 @FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
 @Slf4j
 public class ReviewService {
     ReviewRepository reviewRepository;
     RoomRepository  roomRepository;


      /*
      * Create a new review for a room.
      *
      * @param currentUser - The user writing the review.
      * @param roomId      - ID of the room to be reviewed.
      * @param request     - Review details (rating, comment).
      * @return ReviewResponse - Details of the created review.
      * @throws AppException - If room is not found, user is the owner, or review already exists.
      */
     public ReviewResponse createReview(User currentUser, UUID roomId, ReviewRequest request) {
         Room room = roomRepository.findById(roomId)
                 .orElseThrow(() -> new AppException(RoomErrorCode.ROOM_NOT_FOUND));

//         if (room.getOwner().getId().equals(currentUser.getId())) {
//             throw new AppException(ReviewErrorCode.CANNOT_REVIEW_OWN_ROOM);
//         }

//          if (reviewRepository.existsByUserAndRoomId(currentUser, roomId)) {
//             throw new AppException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
//         }

         Review review = Review.builder()
                 .user(currentUser)
                 .room(room)
                 .score(request.getScore())
                 .comment(request.getComment())
                 .build();

         Review savedReview = reviewRepository.save(review);
         log.info("User '{}' created a review with ID '{}' for room ID '{}'", currentUser.getUsername(), savedReview.getId(), roomId);
         return ReviewResponse.fromReview(savedReview);
     }


     /*
      * Get all reviews for a specific room.
      *
      * @param roomId - Unique identifier of the room.
      * @return List<ReviewResponse> - A list of reviews for the room.
      * @throws AppException - When room is not found.
      */
     public List<ReviewResponse> getReviewsByRoom(UUID roomId) {
         if (!roomRepository.existsById(roomId)) {
             throw new AppException(RoomErrorCode.ROOM_NOT_FOUND);
         }
         log.info("Retrieving reviews for room ID: {}", roomId);

         List<Review> reviews = reviewRepository.findByRoomId(roomId);
         return reviews.stream()
                 .map(ReviewResponse::fromReview)
                 .collect(Collectors.toList());
     }

     /*
      * Update an existing review.
      *
      * @param currentUser - The user updating the review.
      * @param reviewId    - ID of the review to update.
      * @param request     - New review details.
      * @return ReviewResponse - The updated review details.
      * @throws AppException - If review is not found or user is not the author.
      */
     public ReviewResponse updateReview(User currentUser, UUID reviewId, ReviewRequest request) {
         Review review = reviewRepository.findById(reviewId)
                 .orElseThrow(() -> new AppException(ReviewErrorCode.REVIEW_NOT_FOUND));

         if (!review.getUser().getId().equals(currentUser.getId())) {
             throw new AppException(ReviewErrorCode.CANNOT_REVIEW_OWN_ROOM);
         }

         review.setScore(request.getScore());
         review.setComment(request.getComment());

         Review updatedReview = reviewRepository.save(review);
         log.info("User '{}' updated review ID '{}'", currentUser.getUsername(), reviewId);
         return ReviewResponse.fromReview(updatedReview);
     }

     /*
      * Delete a review.
      *
      * @param currentUser - The user deleting the review.
      * @param reviewId    - ID of the review to delete.
      * @throws AppException - If review is not found or user is not the author/admin.
      */
     public void deleteReview(User currentUser, UUID reviewId) {
         Review review = reviewRepository.findById(reviewId)
                 .orElseThrow(() -> new AppException(ReviewErrorCode.REVIEW_NOT_FOUND));

         // Allow deletion if the user is the author OR the user is an Admin
         if (!review.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != UserRole.ADMIN) {
             throw new AppException(ReviewErrorCode.REVIEW_NOT_FOUND);
         }

         reviewRepository.delete(review);
         log.info("User '{}' deleted review ID '{}'", currentUser.getUsername(), reviewId);
     }
     }
