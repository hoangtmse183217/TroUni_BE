package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.request.packages.PackageRequest;
import com.trouni.tro_uni.dto.response.packages.PackageResponse;
import com.trouni.tro_uni.entity.Package;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.PackageErrorCode;
import com.trouni.tro_uni.exception.errorcode.PaymentErrorCode;
import com.trouni.tro_uni.exception.errorcode.SubscriptionErrorCode;
import com.trouni.tro_uni.repository.PackageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PackageService {
    PackageRepository packageRepository;

    /**
     * Create a new package
     * @param request - Package creation details
     * @return PackageResponse - Details of the created package
     * @throws AppException - If package name already exists
     */
    @Transactional
    public PackageResponse createPackage(PackageRequest request) {
        if (packageRepository.existsByName(request.getName())) {
            throw new AppException(SubscriptionErrorCode.SUBSCRIPTION_ALREADY_ACTIVE);
        }

        Package packageEntity = new Package();
        packageEntity.setName(request.getName());
        packageEntity.setPricePerMonth(request.getPricePerMonth());
        packageEntity.setMaxListings(request.getMaxListings());
        packageEntity.setMaxImagesPerListing(request.getMaxImagesPerListing());
//        packageEntity.setCanViewStats(request.getCanViewStats());
        packageEntity.setBoostDays(request.getBoostDays());
        packageEntity.setFeatures(request.getFeaturesJson());

        Package savedPackage = packageRepository.save(packageEntity);
        log.info("Created new package: {}", savedPackage.getName());

        return PackageResponse.fromPackage(savedPackage);
    }

    /**
     * Get package by ID
     * @param packageId - Package identifier
     * @return PackageResponse - Package details
     * @throws AppException - When package is not found
     */
    public PackageResponse getPackageById(UUID packageId) {
        Package packageEntity = packageRepository.findById(packageId)
                .orElseThrow(() -> new AppException(PackageErrorCode.PACKAGE_NOT_FOUND));


        log.info("Retrieved package: {}", packageEntity.getName());
        return PackageResponse.fromPackage(packageEntity);
    }

    /**
     * Get package by name
     * @param name - Package name
     * @return PackageResponse - Package details
     * @throws AppException - When package is not found
     */
    public PackageResponse getPackageByName(String name) {
        Package packageEntity = packageRepository.findByName(name)
                .orElseThrow(() -> new AppException(PackageErrorCode.PACKAGE_NOT_FOUND));

        return PackageResponse.fromPackage(packageEntity);
    }

    /**
     * Get all packages
     * @return List<PackageResponse> - List of all packages
     */
    public List<PackageResponse> getAllPackages() {
        return packageRepository.findAll().stream()
                .map(PackageResponse::fromPackage)
                .collect(Collectors.toList());
    }

    /**
     * Update package information
     * @param packageId - ID of the package to update
     * @param request - Updated package details
     * @return PackageResponse - Updated package information
     * @throws AppException - When package is not found
     */
    @Transactional
    public PackageResponse updatePackage(UUID packageId, PackageRequest request) {
        Package packageEntity = packageRepository.findById(packageId)
                .orElseThrow(() -> new AppException(PackageErrorCode.PACKAGE_NOT_FOUND));


        // Check if new name conflicts with existing package
        if (!packageEntity.getName().equals(request.getName()) &&
            packageRepository.existsByName(request.getName())) {
            throw new AppException(SubscriptionErrorCode.SUBSCRIPTION_ALREADY_ACTIVE);
        }

        packageEntity.setName(request.getName());
        packageEntity.setPricePerMonth(request.getPricePerMonth());
        packageEntity.setMaxListings(request.getMaxListings());
        packageEntity.setMaxImagesPerListing(request.getMaxImagesPerListing());
//        packageEntity.setCanViewStats(request.getCanViewStats());
        packageEntity.setBoostDays(request.getBoostDays());
        packageEntity.setFeatures(request.getFeaturesJson());

        Package updatedPackage = packageRepository.save(packageEntity);
        log.info("Updated package: {}", updatedPackage.getName());

        return PackageResponse.fromPackage(updatedPackage);
    }

    /**
     * Delete a package
     * @param packageId - ID of the package to delete
     * @throws AppException - When package is not found
     */
    @Transactional
    public void deletePackage(UUID packageId) {
        Package packageEntity = packageRepository.findById(packageId)
                .orElseThrow(() -> new AppException(PackageErrorCode.PACKAGE_NOT_FOUND));


        packageRepository.delete(packageEntity);
        log.info("Deleted package: {}", packageEntity.getName());
    }
}
