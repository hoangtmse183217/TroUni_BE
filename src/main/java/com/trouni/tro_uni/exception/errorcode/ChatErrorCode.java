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
public enum ChatErrorCode {
    CHAT_ROOM_NOT_FOUND("CHAT_ROOM_NOT_FOUND", "Chat room not found", HttpStatus.NOT_FOUND),
    MESSAGE_NOT_FOUND("MESSAGE_NOT_FOUND", "Message not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_MESSAGE_ACCESS("UNAUTHORIZED_MESSAGE_ACCESS", "Unauthorized access to message", HttpStatus.FORBIDDEN),
    CHAT_ROOM_ACCESS_DENIED("CHAT_ROOM_ACCESS_DENIED", "Access denied to chat room", HttpStatus.FORBIDDEN),
    MESSAGE_TOO_LONG("MESSAGE_TOO_LONG", "Message is too long", HttpStatus.BAD_REQUEST),
    MESSAGE_EMPTY("MESSAGE_EMPTY", "Message cannot be empty", HttpStatus.BAD_REQUEST),
    CHAT_ROOM_ALREADY_EXISTS("CHAT_ROOM_ALREADY_EXISTS", "Chat room already exists", HttpStatus.CONFLICT),
    INVALID_MESSAGE_TYPE("INVALID_MESSAGE_TYPE", "Invalid message type", HttpStatus.BAD_REQUEST);

    String code;
    String message;
    HttpStatusCode statusCode;
}


