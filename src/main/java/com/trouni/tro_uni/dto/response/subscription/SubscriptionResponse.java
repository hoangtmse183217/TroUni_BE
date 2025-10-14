package com.trouni.tro_uni.dto.response.subscription;

import com.trouni.tro_uni.dto.response.UserResponse;
import com.trouni.tro_uni.dto.response.packages.PackageResponse;
import com.trouni.tro_uni.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private UUID id;
    private UUID userId;
    private String username;
    private UUID packageId;
    private String packageName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private UserResponse user;
    private PackageResponse packageInfo;
    private Long daysRemaining;
    private Boolean isActive;

    /**
     * Convert Subscription entity to SubscriptionResponse DTO
     * @param subscription - Subscription entity to convert
     * @return SubscriptionResponse
     */
    public static SubscriptionResponse fromSubscription(Subscription subscription) {
        if (subscription == null) return null;

        Long daysRemaining = null;
        if (subscription.getEndDate() != null && subscription.getStatus().equals("active")) {
            daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDateTime.now(),
                subscription.getEndDate()
            );
            if (daysRemaining < 0) daysRemaining = 0L;
        }

        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .userId(subscription.getUser().getId())
                .username(subscription.getUser().getUsername())
                .packageId(subscription.getPackageEntity().getId())
                .packageName(subscription.getPackageEntity().getName())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .user(UserResponse.fromUser(subscription.getUser()))
                .packageInfo(PackageResponse.fromPackage(subscription.getPackageEntity()))
                .daysRemaining(daysRemaining)
                .isActive(subscription.getStatus().equals("active"))
                .build();
    }

    /**
     * Convert Subscription entity to simplified SubscriptionResponse DTO (without nested objects)
     * @param subscription - Subscription entity to convert
     * @return SubscriptionResponse
     */
    public static SubscriptionResponse fromSubscriptionSimple(Subscription subscription) {
        if (subscription == null) return null;

        Long daysRemaining = null;
        if (subscription.getEndDate() != null && subscription.getStatus().equals("active")) {
            daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDateTime.now(),
                subscription.getEndDate()
            );
            if (daysRemaining < 0) daysRemaining = 0L;
        }

        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .userId(subscription.getUser().getId())
                .username(subscription.getUser().getUsername())
                .packageId(subscription.getPackageEntity().getId())
                .packageName(subscription.getPackageEntity().getName())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .daysRemaining(daysRemaining)
                .isActive(subscription.getStatus().equals("active"))
                .build();
    }
}
