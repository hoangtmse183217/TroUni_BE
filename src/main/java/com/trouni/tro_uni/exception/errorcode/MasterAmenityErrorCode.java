package com.trouni.tro_uni.exception.errorcode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public enum MasterAmenityErrorCode  {

    MASTER_AMENITY_NOT_FOUND("MASTER_AMENITY_NOT_FOUND", "Master amenity not found",HttpStatus.NOT_FOUND),
    MASTER_AMENITY_ALREADY_EXISTS("MASTER_AMENITY_ALREADY_EXISTS", "Master amenity with the same name already exists",HttpStatus.CONFLICT),
    NO_PERMISSION_TO_MODIFY_MASTER_AMENITY("NO_PERMISSION_TO_MODIFY_MASTER_AMENITY", "You do not have permission to modify master amenities",HttpStatus.FORBIDDEN);

    String code;
    String message;
    HttpStatus statusCode;


}
