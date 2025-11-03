package com.trouni.tro_uni.dto.request.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequestServiceDto {
    private String productName;
    private String description;
    private String returnUrl;
    private String cancelUrl;
    private Long price;
    private Long orderCode;
}
