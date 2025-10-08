package com.trouni.tro_uni.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoomStatus {
    AVAILABLE("available"),
    RENTED("rented"),
    HIDDEN("hidden"),
    PENDING("pending");

    private final String value;

    RoomStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RoomStatus fromValue(String value) {
        for (RoomStatus status : RoomStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid RoomStatus value: " + value);
    }
}
