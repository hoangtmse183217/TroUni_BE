package com.trouni.tro_uni.exception;

import com.trouni.tro_uni.exception.errorcode.*;
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
    private RoomErrorCode roomErrorCode;
    private ReviewErrorCode reviewErrorCode;
    private MasterAmenityErrorCode masterAmenityErrorCode;
//    private BookmarkErrorCode bookmarkErrorCode;
    private SubscriptionErrorCode subscriptionErrorCode;
    private PaymentErrorCode paymentErrorCode;
    private PackageErrorCode packageErrorCode;
//    private UserErrorCode userErrorCode;

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

    public AppException(RoomErrorCode roomErrorCode) {
        super(roomErrorCode.getMessage());
        this.roomErrorCode = roomErrorCode;
    }

    public AppException(MasterAmenityErrorCode masterAmenityErrorCode) {
        super(masterAmenityErrorCode.getMessage());
        this.masterAmenityErrorCode = masterAmenityErrorCode;
    }

    public AppException(ReviewErrorCode reviewErrorCode) {
        super(reviewErrorCode.getMessage());
        this.reviewErrorCode = reviewErrorCode;
    }

    public AppException(SubscriptionErrorCode subscriptionErrorCode) {
        super(subscriptionErrorCode.getMessage());
        this.subscriptionErrorCode = subscriptionErrorCode;
    }

    public AppException(PaymentErrorCode paymentErrorCode) {
        super(paymentErrorCode.getMessage());
        this.paymentErrorCode = paymentErrorCode;
    }

    public AppException(PackageErrorCode packageErrorCode) {
        super(packageErrorCode.getMessage());
        this.packageErrorCode = packageErrorCode;
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
        if (roomErrorCode != null) {
            return roomErrorCode.getCode();
        }
        if (masterAmenityErrorCode != null){
            return masterAmenityErrorCode.getCode();
        }
        if (reviewErrorCode != null){
            return reviewErrorCode.getCode();
        }
        if (subscriptionErrorCode != null) {
            return subscriptionErrorCode.getCode();
        }
        if (paymentErrorCode != null) {
            return paymentErrorCode.getCode();
        }
        if (packageErrorCode != null) {
            return packageErrorCode.getCode();
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
        if (roomErrorCode != null) {
            return roomErrorCode.getMessage();
        }
        if (masterAmenityErrorCode != null) {
            return masterAmenityErrorCode.getMessage();
        }
        if (reviewErrorCode != null) {
            return reviewErrorCode.getMessage();
        }
        if (subscriptionErrorCode != null) {
            return subscriptionErrorCode.getMessage();
        }
        if (paymentErrorCode != null) {
            return paymentErrorCode.getMessage();
        }
        if (packageErrorCode != null) {
            return packageErrorCode.getMessage();
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
        if (roomErrorCode != null) {
            return roomErrorCode.getStatusCode();
        }
        if (masterAmenityErrorCode != null) {
            return masterAmenityErrorCode.getStatusCode();
        }
        if (reviewErrorCode != null) {
            return reviewErrorCode.getStatusCode();
        }
        if (subscriptionErrorCode != null) {
            return subscriptionErrorCode.getStatusCode();
        }
        if (paymentErrorCode != null) {
            return paymentErrorCode.getStatusCode();
        }
        if (packageErrorCode != null) {
            return packageErrorCode.getStatusCode();
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}