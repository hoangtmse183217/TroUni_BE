package com.trouni.tro_uni.enums;

/**
 * Enum for different types of rooms available for rent
 */
public enum RoomType {
    PHONG_TRO("phong_tro"),
    CHUNG_CU_MINI("chung_cu_mini"),
    O_GHEP("o_ghep"),
    KY_TUC_XA("ky_tuc_xa");
    
    private final String value;
    
    RoomType(String value) {
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