package com.trouni.tro_uni.dto.request.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PayOSPaymentRequest {
    @NotNull(message = "Amount cannot be null")
    @Min(value = 1000, message = "Amount must be at least 1000")
    private Integer amount;

    private String description;

    @NotBlank(message = "Return URL cannot be blank")
    private String returnUrl;

    @NotBlank(message = "Cancel URL cannot be blank")
    private String cancelUrl;

//    private UUID subscriptionId;
    private UUID roomId;
}
