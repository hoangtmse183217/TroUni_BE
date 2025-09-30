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
public enum AuthenticationErrorCode {
    EMAIL_EXISTED("EMAIL_EXISTED", "Email already exists", HttpStatus.BAD_REQUEST),
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS", "Username already exists", HttpStatus.CONFLICT),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND),
    PROFILE_NOT_FOUND("PROFILE_NOT_FOUND", "Profile not found", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid email or password", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("UNAUTHORIZED", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED("UNAUTHENTICATED","Unauthenticated", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("ACCESS_DENIED", "Access denied", HttpStatus.FORBIDDEN),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("TOKEN_INVALID", "Invalid token", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("ACCOUNT_LOCKED", "Account is locked", HttpStatus.LOCKED),
    ACCOUNT_DISABLED("ACCOUNT_DISABLED", "Account is disabled", HttpStatus.FORBIDDEN),
    PASSWORD_TOO_WEAK("PASSWORD_TOO_WEAK", "Password does not meet security requirements", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED("EMAIL_NOT_VERIFIED", "Email address not verified", HttpStatus.FORBIDDEN),
    PHONE_NOT_VERIFIED("PHONE_NOT_VERIFIED", "Phone number not verified", HttpStatus.FORBIDDEN),
    ACCOUNT_REGISTERED_WITH_GOOGLE("ACCOUNT_REGISTERED_WITH_GOOGLE", "This account was registered with Google. Please use Google login instead", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already exists", HttpStatus.CONFLICT),
    CANNOT_DELETE_SELF("CANNOT_DELETE_SELF", "Cannot delete your own account", HttpStatus.BAD_REQUEST);

    String code;
    String message;
    HttpStatusCode statusCode;
}

