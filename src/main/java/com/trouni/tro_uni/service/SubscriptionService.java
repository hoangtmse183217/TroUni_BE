package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.request.subscription.SubscriptionRequest;
import com.trouni.tro_uni.dto.response.subscription.SubscriptionResponse;
import com.trouni.tro_uni.entity.Package;
import com.trouni.tro_uni.entity.Subscription;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.PackageErrorCode;
import com.trouni.tro_uni.exception.errorcode.SubscriptionErrorCode;
import com.trouni.tro_uni.repository.PackageRepository;
import com.trouni.tro_uni.repository.SubscriptionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionService {
    SubscriptionRepository subscriptionRepository;
    PackageRepository packageRepository;

    /**
     * Create a new subscription for a user
     * @param currentUser - The user subscribing to a package
     * @param request - Subscription details
     * @return SubscriptionResponse - Details of the created subscription
     * @throws AppException - If user already has active subscription or package not found
     */
    @Transactional
    public SubscriptionResponse createSubscription(User currentUser, SubscriptionRequest request) {
        // Check if user already has an active subscription
        Optional<Subscription> existingSubscription = subscriptionRepository.findActiveSubscriptionByUser(currentUser);
        if (existingSubscription.isPresent()) {
            throw new AppException(SubscriptionErrorCode.SUBSCRIPTION_ALREADY_ACTIVE);
        }

        Package packageEntity = packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new AppException(PackageErrorCode.PACKAGE_NOT_FOUND));


        Subscription subscription = new Subscription();
        subscription.setUser(currentUser);
        subscription.setPackageEntity(packageEntity);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusMonths(request.getDurationMonths()));
        subscription.setStatus("active");

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        log.info("User {} subscribed to package {} for {} months",
                 currentUser.getUsername(), packageEntity.getName(), request.getDurationMonths());

        return SubscriptionResponse.fromSubscription(savedSubscription);
    }

    /**
     * Get user's current subscription
     * @param currentUser - The user whose subscription to retrieve
     * @return SubscriptionResponse - Current subscription details
     * @throws AppException - When subscription is not found
     */
    public SubscriptionResponse getUserSubscription(User currentUser) {
        Subscription subscription = subscriptionRepository.findByUser(currentUser)
                .orElseThrow(() -> new AppException(SubscriptionErrorCode.SUBSCRIPTION_NOT_FOUND));

        return SubscriptionResponse.fromSubscription(subscription);
    }

    /**
     * Get user's active subscription
     * @param currentUser - The user whose active subscription to retrieve
     * @return SubscriptionResponse - Active subscription details
     * @throws AppException - When active subscription is not found
     */
    public SubscriptionResponse getActiveSubscription(User currentUser) {
        Subscription subscription = subscriptionRepository.findActiveSubscriptionByUser(currentUser)
                .orElseThrow(() -> new AppException(SubscriptionErrorCode.SUBSCRIPTION_NOT_FOUND));

        return SubscriptionResponse.fromSubscription(subscription);
    }

    /**
     * Get subscription by ID
     * @param subscriptionId - Subscription identifier
     * @return SubscriptionResponse - Subscription details
     * @throws AppException - When subscription is not found
     */
    public SubscriptionResponse getSubscriptionById(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new AppException(SubscriptionErrorCode.SUBSCRIPTION_NOT_FOUND));

        return SubscriptionResponse.fromSubscription(subscription);
    }

    /**
     * Get all subscriptions
     * @return List<SubscriptionResponse> - List of all subscriptions
     */
    public List<SubscriptionResponse> getAllSubscriptions() {
        return subscriptionRepository.findAll().stream()
                .map(SubscriptionResponse::fromSubscription)
                .collect(Collectors.toList());
    }

    /**
     * Get subscriptions by status
     * @param status - Subscription status (active, expired, cancelled)
     * @return List<SubscriptionResponse> - List of subscriptions with specified status
     */
    public List<SubscriptionResponse> getSubscriptionsByStatus(String status) {
        return subscriptionRepository.findByStatus(status).stream()
                .map(SubscriptionResponse::fromSubscription)
                .collect(Collectors.toList());
    }

    /**
     * Upgrade user's subscription to a different package
     * @param currentUser - The user upgrading subscription
     * @param request - New subscription details
     * @return SubscriptionResponse - Updated subscription details
     * @throws AppException - If no active subscription found or upgrade fails
     */
    @Transactional
    public SubscriptionResponse upgradeSubscription(User currentUser, SubscriptionRequest request) {
        Subscription subscription = subscriptionRepository.findActiveSubscriptionByUser(currentUser)
                .orElseThrow(() -> new AppException(SubscriptionErrorCode.SUBSCRIPTION_NOT_FOUND));

        Package newPackage = packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new AppException(PackageErrorCode.PACKAGE_NOT_FOUND));


        // Check if it's actually an upgrade (new package price > current package price)
        if (newPackage.getPricePerMonth().compareTo(subscription.getPackageEntity().getPricePerMonth()) <= 0) {
            throw new AppException(SubscriptionErrorCode.SUBSCRIPTION_UPGRADE_NOT_ALLOWED);
        }

        subscription.setPackageEntity(newPackage);
        subscription.setEndDate(LocalDateTime.now().plusMonths(request.getDurationMonths()));

        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        log.info("User {} upgraded subscription to package {}",
                 currentUser.getUsername(), newPackage.getName());

        return SubscriptionResponse.fromSubscription(updatedSubscription);
    }

    /**
     * Renew user's subscription
     * @param currentUser - The user renewing subscription
     * @param request - Renewal details (same package, extended duration)
     * @return SubscriptionResponse - Renewed subscription details
     * @throws AppException - If no active subscription found
     */
    @Transactional
    public SubscriptionResponse renewSubscription(User currentUser, SubscriptionRequest request) {
        Subscription subscription = subscriptionRepository.findActiveSubscriptionByUser(currentUser)
                .orElseThrow(() -> new AppException(SubscriptionErrorCode.SUBSCRIPTION_NOT_FOUND));

        Package packageEntity = packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new AppException(PackageErrorCode.PACKAGE_NOT_FOUND));


        // Extend end date from current end date
        LocalDateTime newEndDate = subscription.getEndDate().plusMonths(request.getDurationMonths());
        subscription.setEndDate(newEndDate);
        subscription.setPackageEntity(packageEntity);
        subscription.setStatus("active");

        Subscription renewedSubscription = subscriptionRepository.save(subscription);
        log.info("User {} renewed subscription for {} months",
                 currentUser.getUsername(), request.getDurationMonths());

        return SubscriptionResponse.fromSubscription(renewedSubscription);
    }

    /**
     * Cancel user's subscription
     * @param currentUser - The user cancelling subscription
     * @throws AppException - When subscription is not found
     */
    @Transactional
    public void cancelSubscription(User currentUser) {
        Subscription subscription = subscriptionRepository.findActiveSubscriptionByUser(currentUser)
                .orElseThrow(() -> new AppException(SubscriptionErrorCode.SUBSCRIPTION_NOT_FOUND));

        subscription.setStatus("cancelled");
        subscription.setEndDate(LocalDateTime.now());

        subscriptionRepository.save(subscription);
        log.info("User {} cancelled subscription", currentUser.getUsername());
    }

    /**
     * Check if user has an active subscription
     * @param currentUser - The user to check
     * @return boolean - True if user has active subscription, false otherwise
     */
    public boolean hasActiveSubscription(User currentUser) {
        Optional<Subscription> subscription = subscriptionRepository.findActiveSubscriptionByUser(currentUser);
        return subscription.isPresent();
    }

    /**
     * Scheduled task to expire subscriptions that have passed their end date
     * Runs every day at midnight
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void expireSubscriptions() {
        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findExpiredActiveSubscriptions(LocalDateTime.now());

        for (Subscription subscription : expiredSubscriptions) {
            subscription.setStatus("expired");
            subscriptionRepository.save(subscription);
            log.info("Expired subscription for user: {}", subscription.getUser().getUsername());
        }

        if (!expiredSubscriptions.isEmpty()) {
            log.info("Expired {} subscriptions", expiredSubscriptions.size());
        }
    }
}
