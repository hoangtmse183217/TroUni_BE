package com.trouni.tro_uni.exception;

import com.trouni.tro_uni.exception.errorcode.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppException extends RuntimeException {

    private TokenErrorCode tokenErrorCode;
    private AuthenticationErrorCode authErrorCode;
    private GeneralErrorCode generalErrorCode;
    private RoomErrorCode roomErrorCode;
    private ReviewErrorCode reviewErrorCode;
    private MasterAmenityErrorCode masterAmenityErrorCode;
    private BookmarkErrorCode bookmarkErrorCode;
    private SubscriptionErrorCode subscriptionErrorCode;
    private PaymentErrorCode paymentErrorCode;
    private UserErrorCode userErrorCode;

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

    public AppException(BookmarkErrorCode bookmarkErrorCode) {
        super(bookmarkErrorCode.getMessage());
        this.bookmarkErrorCode = bookmarkErrorCode;
    }

    public AppException(SubscriptionErrorCode subscriptionErrorCode) {
        super(subscriptionErrorCode.getMessage());
        this.subscriptionErrorCode = subscriptionErrorCode;
    }

    public AppException(PaymentErrorCode paymentErrorCode) {
        super(paymentErrorCode.getMessage());
        this.paymentErrorCode = paymentErrorCode;
    }
    public AppException(UserErrorCode userErrorCode) {
        super(userErrorCode.getMessage());
        this.userErrorCode = userErrorCode;
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
        if (bookmarkErrorCode != null) {
            return bookmarkErrorCode.getCode();
        }
        if (subscriptionErrorCode != null) {
            return subscriptionErrorCode.getCode();
        }
        if (paymentErrorCode != null) {
            return paymentErrorCode.getCode();
        }
        if (userErrorCode != null) {
            return userErrorCode.getCode();
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
        if (roomErrorCode != null) {
            return roomErrorCode.getMessage();
        }
        if (masterAmenityErrorCode != null){
            return masterAmenityErrorCode.getMessage();
        }
        if (reviewErrorCode != null){
            return reviewErrorCode.getMessage();
        }
        if (bookmarkErrorCode != null) {
            return bookmarkErrorCode.getMessage();
        }
        if (subscriptionErrorCode != null) {
            return subscriptionErrorCode.getMessage();
        }
         if (paymentErrorCode != null) {
            return paymentErrorCode.getMessage();
        }
          if (userErrorCode != null) {
            return userErrorCode.getMessage();
        }
        return "Unknown error occurred";
    }

    // Helper method để lấy status code
    public org.springframework.http.HttpStatusCode getStatusCode() {
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
         if (masterAmenityErrorCode != null){
            return masterAmenityErrorCode.getStatusCode();
        }
        if (reviewErrorCode != null){
            return reviewErrorCode.getStatusCode();
        }
        if (bookmarkErrorCode != null) {
            return bookmarkErrorCode.getStatusCode();
        }
        if (subscriptionErrorCode != null) {
            return subscriptionErrorCode.getStatusCode();
        }
         if (paymentErrorCode != null) {
            return paymentErrorCode.getStatusCode();
        }
         if (userErrorCode != null) {
            return userErrorCode.getStatusCode();
        }
        return org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
