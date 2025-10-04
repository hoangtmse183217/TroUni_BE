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

    public AppException(TokenErrorCode tokenErrorCode, String customMessage) {
        super(customMessage);
        this.tokenErrorCode = tokenErrorCode;
    }

    public AppException(AuthenticationErrorCode authErrorCode, String customMessage) {
        super(customMessage);
        this.authErrorCode = authErrorCode;
    }

    public AppException(GeneralErrorCode generalErrorCode, String customMessage) {
        super(customMessage);
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
        // Sử dụng custom message nếu có, nếu không thì dùng default message từ error code
        String customMessage = super.getMessage();
        
        if (tokenErrorCode != null) {
            return customMessage != null && !customMessage.equals(tokenErrorCode.getMessage()) ? 
                   customMessage : tokenErrorCode.getMessage();
        }
        if (authErrorCode != null) {
            return customMessage != null && !customMessage.equals(authErrorCode.getMessage()) ? 
                   customMessage : authErrorCode.getMessage();
        }
        if (generalErrorCode != null) {
            return customMessage != null && !customMessage.equals(generalErrorCode.getMessage()) ? 
                   customMessage : generalErrorCode.getMessage();
        }
        return customMessage != null ? customMessage : "Unknown error occurred";
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
