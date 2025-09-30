package com.trouni.tro_uni.exception;

import com.trouni.tro_uni.exception.errorcode.TokenErrorCode;
import com.trouni.tro_uni.exception.errorcode.AuthenticationErrorCode;
import com.trouni.tro_uni.exception.errorcode.GeneralErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Setter
@Getter
public class AppException extends RuntimeException {

    private TokenErrorCode tokenErrorCode;
    private AuthenticationErrorCode authErrorCode;
    private GeneralErrorCode generalErrorCode;

    public AppException(TokenErrorCode tokenErrorCode) {
        super(tokenErrorCode.getMessage());
        this.tokenErrorCode = tokenErrorCode;
    }

    public AppException(AuthenticationErrorCode authErrorCode) {
        super(authErrorCode.getMessage());
        this.authErrorCode = authErrorCode;
    }

    public AppException(GeneralErrorCode generalErrorCode) {
        super(generalErrorCode.getMessage());
        this.generalErrorCode = generalErrorCode;
    }

    // Helper method để lấy code
    public String getErrorCode() {
        if (tokenErrorCode != null) {
            return tokenErrorCode.getCode();
        }
        if (authErrorCode != null) {
            return authErrorCode.getCode();
        }
        if (generalErrorCode != null) {
            return generalErrorCode.getCode();
        }
        return "UNKNOWN_ERROR";
    }

    // Helper method để lấy message
    public String getErrorMessage() {
        if (tokenErrorCode != null) {
            return tokenErrorCode.getMessage();
        }
        if (authErrorCode != null) {
            return authErrorCode.getMessage();
        }
        if (generalErrorCode != null) {
            return generalErrorCode.getMessage();
        }
        return "Unknown error occurred";
    }

    // Helper method để lấy status code
    public HttpStatusCode getStatusCode() {
        if (tokenErrorCode != null) {
            return tokenErrorCode.getStatusCode();
        }
        if (authErrorCode != null) {
            return authErrorCode.getStatusCode();
        }
        if (generalErrorCode != null) {
            return generalErrorCode.getStatusCode();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
