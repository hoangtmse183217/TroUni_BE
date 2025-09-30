package com.trouni.tro_uni.enums;

/**
 * Enum for user roles in the system
 */
public enum UserRole {
    STUDENT("student"),
    LANDLORD("landlord"), 
    MANAGER("manager"),
    ADMIN("admin");
    
    private final String value;
    
    UserRole(String value) {
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