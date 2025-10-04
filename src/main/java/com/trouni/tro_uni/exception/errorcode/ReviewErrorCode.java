package com.trouni.tro_uni.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode {
    REVIEW_NOT_FOUND("REVIEW_NOT_FOUND", "Review not found", HttpStatus.NOT_FOUND),
    NOT_REVIEW_OWNER("NOT_REVIEW_OWNER", "You are not the owner of this review", HttpStatus.FORBIDDEN),
    REVIEW_ALREADY_EXISTS("REVIEW_ALREADY_EXISTS", "You have already reviewed this room", HttpStatus.CONFLICT),
    INVALID_RATING("INVALID_RATING", "Rating must be between 1 and 5", HttpStatus.BAD_REQUEST),
    REVIEW_DELETED("REVIEW_DELETED", "Review has been deleted", HttpStatus.GONE),
    CANNOT_REVIEW_OWN_ROOM("CANNOT_REVIEW_OWN_ROOM", "You cannot review your own room", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatusCode statusCode;
}
