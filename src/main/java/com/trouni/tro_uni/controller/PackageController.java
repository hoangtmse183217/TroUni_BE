package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.request.packages.PackageRequest;
import com.trouni.tro_uni.dto.response.packages.PackageResponse;
import com.trouni.tro_uni.service.PackageService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/packages")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PackageController {
    PackageService packageService;

    /**
     * Create a new package (Admin only)
     * @param request - Package creation details
     * @return ResponseEntity<ApiResponse<PackageResponse>>
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PackageResponse>> createPackage(
            @Valid @RequestBody PackageRequest request
    ) {
        PackageResponse response = packageService.createPackage(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Package created successfully", response));
    }

    /**
     * Get package by ID
     * @param packageId - Package identifier
     * @return ResponseEntity<ApiResponse<PackageResponse>>
     */
    @GetMapping("/{packageId}")
    public ResponseEntity<ApiResponse<PackageResponse>> getPackageById(
            @PathVariable UUID packageId
    ) {
        PackageResponse response = packageService.getPackageById(packageId);
        return ResponseEntity.ok(ApiResponse.success("Package retrieved successfully", response));
    }

    /**
     * Get package by name
     * @param name - Package name
     * @return ResponseEntity<ApiResponse<PackageResponse>>
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<PackageResponse>> getPackageByName(
            @PathVariable String name
    ) {
        PackageResponse response = packageService.getPackageByName(name);
        return ResponseEntity.ok(ApiResponse.success("Package retrieved successfully", response));
    }

    /**
     * Get all packages
     * @return ResponseEntity<ApiResponse<List<PackageResponse>>>
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PackageResponse>>> getAllPackages() {
        List<PackageResponse> responses = packageService.getAllPackages();
        return ResponseEntity.ok(ApiResponse.success("Packages retrieved successfully", responses));
    }

    /**
     * Update package (Admin only)
     * @param packageId - Package identifier
     * @param request - Updated package details
     * @return ResponseEntity<ApiResponse<PackageResponse>>
     */
    @PutMapping("/{packageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PackageResponse>> updatePackage(
            @PathVariable UUID packageId,
            @Valid @RequestBody PackageRequest request
    ) {
        PackageResponse response = packageService.updatePackage(packageId, request);
        return ResponseEntity.ok(ApiResponse.success("Package updated successfully", response));
    }

    /**
     * Delete package (Admin only)
     * @param packageId - Package identifier
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @DeleteMapping("/{packageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePackage(
            @PathVariable UUID packageId
    ) {
        packageService.deletePackage(packageId);
        return ResponseEntity.ok(ApiResponse.success("Package deleted successfully", null));
    }
}
