package com.trouni.tro_uni.exception.errorcode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ReviewErrorCode {
    REVIEW_NOT_FOUND("REVIEW_NOT_FOUND", "Review not found", HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_EXISTS("REVIEW_ALREADY_EXISTS", "Review already exists", HttpStatus.CONFLICT),
    INVALID_RATING("INVALID_RATING", "Invalid rating value", HttpStatus.BAD_REQUEST),
    REVIEW_ACCESS_DENIED("REVIEW_ACCESS_DENIED", "Access denied to review", HttpStatus.FORBIDDEN),
    CANNOT_REVIEW_OWN_ROOM("CANNOT_REVIEW_OWN_ROOM", "Cannot review your own room", HttpStatus.BAD_REQUEST),
    REVIEW_TOO_LONG("REVIEW_TOO_LONG", "Review comment is too long", HttpStatus.BAD_REQUEST),
    REVIEW_ALREADY_UPDATED("REVIEW_ALREADY_UPDATED", "Review has already been updated", HttpStatus.CONFLICT),
    INVALID_REVIEW_STATUS("INVALID_REVIEW_STATUS", "Invalid review status", HttpStatus.BAD_REQUEST);

    String code;
    String message;
    HttpStatusCode statusCode;
}


