package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.request.subscription.SubscriptionRequest;
import com.trouni.tro_uni.dto.response.subscription.SubscriptionResponse;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.service.SubscriptionService;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionController {
    SubscriptionService subscriptionService;

    /**
     * Create a new subscription
     * @param currentUser - Authenticated user
     * @param request - Subscription details
     * @return ResponseEntity<ApiResponse<SubscriptionResponse>>
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> createSubscription(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody SubscriptionRequest request
    ) {
        SubscriptionResponse response = subscriptionService.createSubscription(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subscription created successfully", response));
    }

    /**
     * Get current user's subscription
     * @param currentUser - Authenticated user
     * @return ResponseEntity<ApiResponse<SubscriptionResponse>>
     */
    @GetMapping("/my-subscription")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getMySubscription(
            @AuthenticationPrincipal User currentUser
    ) {
        SubscriptionResponse response = subscriptionService.getUserSubscription(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Subscription retrieved successfully", response));
    }

    /**
     * Get current user's active subscription
     * @param currentUser - Authenticated user
     * @return ResponseEntity<ApiResponse<SubscriptionResponse>>
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getActiveSubscription(
            @AuthenticationPrincipal User currentUser
    ) {
        SubscriptionResponse response = subscriptionService.getActiveSubscription(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Active subscription retrieved successfully", response));
    }

    /**
     * Get subscription by ID (Admin only)
     * @param subscriptionId - Subscription identifier
     * @return ResponseEntity<ApiResponse<SubscriptionResponse>>
     */
    @GetMapping("/{subscriptionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getSubscriptionById(
            @PathVariable UUID subscriptionId
    ) {
        SubscriptionResponse response = subscriptionService.getSubscriptionById(subscriptionId);
        return ResponseEntity.ok(ApiResponse.success("Subscription retrieved successfully", response));
    }

    /**
     * Get all subscriptions (Admin only)
     * @return ResponseEntity<ApiResponse<List<SubscriptionResponse>>>
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getAllSubscriptions() {
        List<SubscriptionResponse> responses = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(ApiResponse.success("Subscriptions retrieved successfully", responses));
    }

    /**
     * Get subscriptions by status (Admin only)
     * @param status - Subscription status
     * @return ResponseEntity<ApiResponse<List<SubscriptionResponse>>>
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getSubscriptionsByStatus(
            @PathVariable String status
    ) {
        List<SubscriptionResponse> responses = subscriptionService.getSubscriptionsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Subscriptions retrieved successfully", responses));
    }

    /**
     * Upgrade subscription
     * @param currentUser - Authenticated user
     * @param request - New subscription details
     * @return ResponseEntity<ApiResponse<SubscriptionResponse>>
     */
    @PutMapping("/upgrade")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> upgradeSubscription(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody SubscriptionRequest request
    ) {
        SubscriptionResponse response = subscriptionService.upgradeSubscription(currentUser, request);
        return ResponseEntity.ok(ApiResponse.success("Subscription upgraded successfully", response));
    }

    /**
     * Renew subscription
     * @param currentUser - Authenticated user
     * @param request - Renewal details
     * @return ResponseEntity<ApiResponse<SubscriptionResponse>>
     */
    @PutMapping("/renew")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> renewSubscription(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody SubscriptionRequest request
    ) {
        SubscriptionResponse response = subscriptionService.renewSubscription(currentUser, request);
        return ResponseEntity.ok(ApiResponse.success("Subscription renewed successfully", response));
    }

    /**
     * Cancel subscription
     * @param currentUser - Authenticated user
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @DeleteMapping("/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelSubscription(
            @AuthenticationPrincipal User currentUser
    ) {
        subscriptionService.cancelSubscription(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Subscription cancelled successfully", null));
    }

    /**
     * Check if user has active subscription
     * @param currentUser - Authenticated user
     * @return ResponseEntity<ApiResponse<Map<String, Boolean>>>
     */
    @GetMapping("/check-active")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkActiveSubscription(
            @AuthenticationPrincipal User currentUser
    ) {
        boolean hasActive = subscriptionService.hasActiveSubscription(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Subscription status checked",
                Map.of("hasActiveSubscription", hasActive)));
    }
}
