package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.request.masteramenity.MasterAmenityRequest;
import com.trouni.tro_uni.dto.response.MasterAmenity.MasterAmenityResponse;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.service.MasterAmenityService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/master-amenities")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MasterAmenityController {

    MasterAmenityService masterAmenityService;

    /**
     * Create a new master amenity.
     * (Typically restricted to Admin users)
     * @param request - The details of the amenity to create.
     * @return ResponseEntity<MasterAmenityResponse>
     */
    @PostMapping("/{roomId}")
    public ResponseEntity<MasterAmenityResponse> createMasterAmenity(
            @PathVariable UUID roomId,
            @Valid @RequestBody MasterAmenityRequest request) {

        MasterAmenityResponse response = masterAmenityService.createMasterAmenity(roomId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all master amenities.
     * @return ResponseEntity<List<MasterAmenityResponse>>
     */
    @GetMapping("/{amenityId}")
    public ResponseEntity<List<MasterAmenityResponse>> getAllMasterAmenities(@PathVariable UUID amenityId) {
        return ResponseEntity.ok(masterAmenityService.getMasterAmenities(amenityId));
    }

    /**
     * Update an existing master amenity.
     * (Typically restricted to Admin users)
     * @param amenityId - The ID of the amenity to update.
     * @param request - The new details for the amenity.
     * @return ResponseEntity<MasterAmenityResponse>
     */
//    @PutMapping("/{amenityId}")
//    public ResponseEntity<MasterAmenityResponse> updateMasterAmenity(
//            @PathVariable UUID amenityId,
//            @Valid @RequestBody MasterAmenityRequest request) {
//        return ResponseEntity.ok(masterAmenityService.updateMasterAmenity(amenityId, request));
//    }

    /**
     * Delete a master amenity.
     * (Typically restricted to Admin users)
     * @param amenityId - The ID of the amenity to delete.
     * @return ResponseEntity<Void>
     */
    @DeleteMapping("/{amenityId}")
    public ResponseEntity<Void> deleteMasterAmenity(
            @AuthenticationPrincipal User curentUser,
            @PathVariable UUID amenityId) {
        masterAmenityService.deleteMasterAmenity(curentUser,amenityId);
        return ResponseEntity.noContent().build();
    }
}
