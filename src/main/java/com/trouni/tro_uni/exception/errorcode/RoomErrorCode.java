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
public enum RoomErrorCode {
    ROOM_NOT_FOUND("ROOM_NOT_FOUND", "Room not found", HttpStatus.NOT_FOUND),
    NOT_ROOM_OWNER("NOT_ROOM_OWNER", "You are not the owner of this room", HttpStatus.FORBIDDEN),
    INVALID_ROOM_STATUS("INVALID_ROOM_STATUS", "Invalid room status", HttpStatus.BAD_REQUEST),
    ROOM_ALREADY_EXISTS("ROOM_ALREADY_EXISTS", "Room already exists", HttpStatus.CONFLICT),
    INVALID_PRICE("INVALID_PRICE", "Invalid room price", HttpStatus.BAD_REQUEST),
    INVALID_AREA("INVALID_AREA", "Invalid room area", HttpStatus.BAD_REQUEST),
    INVALID_LOCATION("INVALID_LOCATION", "Invalid location information", HttpStatus.BAD_REQUEST),
    MAX_IMAGES_EXCEEDED("MAX_IMAGES_EXCEEDED", "Maximum number of images exceeded", HttpStatus.BAD_REQUEST),
    INVALID_AMENITY("INVALID_AMENITY", "Invalid amenity", HttpStatus.BAD_REQUEST),
    ROOM_ALREADY_RENTED("ROOM_ALREADY_RENTED", "Room is already rented", HttpStatus.BAD_REQUEST),
    ROOM_NOT_AVAILABLE("ROOM_NOT_AVAILABLE", "Room is not available", HttpStatus.BAD_REQUEST),
    ROOM_BOOST_ACTIVE("ROOM_BOOST_ACTIVE", "Room boost is already active", HttpStatus.BAD_REQUEST),
    ROOM_IMAGE_NOT_FOUND("ROOM_IMAGE_NOT_FOUND", "Room image not found", HttpStatus.NOT_FOUND),
    NOT_LANDLORD("NOT_LANDLORD", "You are not a landlord", HttpStatus.FORBIDDEN),
    INSUFFICIENT_PERMISSIONS("INSUFFICIENT_PERMISSIONS", "Insufficient permissions to perform this action", HttpStatus.FORBIDDEN);

    String code;
    String message;
    HttpStatusCode statusCode;
}
