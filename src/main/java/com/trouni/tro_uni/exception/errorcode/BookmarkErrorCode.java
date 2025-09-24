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
public enum BookmarkErrorCode {
    BOOKMARK_NOT_FOUND("BOOKMARK_NOT_FOUND", "Bookmark not found", HttpStatus.NOT_FOUND),
    BOOKMARK_ALREADY_EXISTS("BOOKMARK_ALREADY_EXISTS", "Bookmark already exists", HttpStatus.CONFLICT),
    BOOKMARK_ACCESS_DENIED("BOOKMARK_ACCESS_DENIED", "Access denied to bookmark", HttpStatus.FORBIDDEN),
    MAX_BOOKMARKS_EXCEEDED("MAX_BOOKMARKS_EXCEEDED", "Maximum number of bookmarks exceeded", HttpStatus.BAD_REQUEST),
    CANNOT_BOOKMARK_OWN_ROOM("CANNOT_BOOKMARK_OWN_ROOM", "Cannot bookmark your own room", HttpStatus.BAD_REQUEST);

    String code;
    String message;
    HttpStatusCode statusCode;
}


