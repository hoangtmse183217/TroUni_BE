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
public enum UserErrorCode {
    PROFILE_NOT_FOUND("PROFILE_NOT_FOUND", "Profile not found", HttpStatus.NOT_FOUND),
    PROFILE_ALREADY_EXISTS("PROFILE_ALREADY_EXISTS", "Profile already exists", HttpStatus.CONFLICT),
    INVALID_VERIFICATION_STATUS("INVALID_VERIFICATION_STATUS", "Invalid verification status", HttpStatus.BAD_REQUEST),
    USER_VERIFICATION_NOT_FOUND("USER_VERIFICATION_NOT_FOUND", "User verification not found", HttpStatus.NOT_FOUND),
    VERIFICATION_ALREADY_SUBMITTED("VERIFICATION_ALREADY_SUBMITTED", "Verification already submitted", HttpStatus.CONFLICT),
    INVALID_VERIFICATION_DOCUMENT("INVALID_VERIFICATION_DOCUMENT", "Invalid verification document", HttpStatus.BAD_REQUEST),
    USER_ROLE_INVALID("USER_ROLE_INVALID", "Invalid user role", HttpStatus.BAD_REQUEST),
    USER_ALREADY_VERIFIED("USER_ALREADY_VERIFIED", "User is already verified", HttpStatus.CONFLICT),
    PROFILE_UPDATE_DENIED("PROFILE_UPDATE_DENIED", "Profile update denied", HttpStatus.FORBIDDEN),
    INVALID_AVATAR_FORMAT("INVALID_AVATAR_FORMAT", "Invalid avatar format", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_ALREADY_EXISTS("PHONE_NUMBER_ALREADY_EXISTS", "Phone number already exists", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS", "Username already exists", HttpStatus.CONFLICT);

    String code;
    String message;
    HttpStatusCode statusCode;
}


