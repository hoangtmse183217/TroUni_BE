package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
     * @param roomId - The room ID to associate the amenity with
     * @param request - The details of the amenity to create.
     * @return ResponseEntity<?>
     */
    @PostMapping("/{roomId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createMasterAmenity(
            @PathVariable UUID roomId,
            @Valid @RequestBody MasterAmenityRequest request) {
        try {
            MasterAmenityResponse response = masterAmenityService.createMasterAmenity(roomId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Master amenity created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("CREATE_MASTER_AMENITY_ERROR", "Failed to create master amenity: " + e.getMessage()));
        }
    }

    /**
     * Get all master amenities.
     * @param amenityId - The amenity ID to get amenities for
     * @return ResponseEntity<?>
     */
    @GetMapping("/{amenityId}")
    public ResponseEntity<?> getAllMasterAmenities(@PathVariable UUID amenityId) {
        try {
            List<MasterAmenityResponse> amenities = masterAmenityService.getMasterAmenities(amenityId);
            return ResponseEntity.ok(ApiResponse.success("Master amenities retrieved successfully", amenities));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_MASTER_AMENITIES_ERROR", "Failed to get master amenities: " + e.getMessage()));
        }
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
     * @param currentUser - The authenticated user
     * @param amenityId - The ID of the amenity to delete.
     * @return ResponseEntity<?>
     */
    @DeleteMapping("/{amenityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMasterAmenity(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID amenityId) {
        try {
            masterAmenityService.deleteMasterAmenity(currentUser, amenityId);
            return ResponseEntity.ok(ApiResponse.success("Master amenity deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("DELETE_MASTER_AMENITY_ERROR", "Failed to delete master amenity: " + e.getMessage()));
        }
    }
}
