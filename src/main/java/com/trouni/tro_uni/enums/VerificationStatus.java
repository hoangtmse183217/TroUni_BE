package com.trouni.tro_uni.enums;

/**
 * Enum for user verification status
 */
public enum VerificationStatus {
    NOT_VERIFIED("not_verified"),
    PENDING_REVIEW("pending_review"),
    VERIFIED("verified"),
    REJECTED("rejected");
    
    private final String value;
    
    VerificationStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}