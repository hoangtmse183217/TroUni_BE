package com.trouni.tro_uni.dto.request.subscription;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {

    @NotNull(message = "Package ID is required")
    private UUID packageId;

    @NotNull(message = "Duration in months is required")
    private Integer durationMonths;
}
