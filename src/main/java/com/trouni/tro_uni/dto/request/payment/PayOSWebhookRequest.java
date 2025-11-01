package com.trouni.tro_uni.dto.request.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayOSWebhookRequest {
    String code;
    String desc;
    boolean success;
    @JsonProperty("orderCode")
    Long orderCode;
    BigDecimal amount;
    String description;
    @JsonProperty("accountNumber")
    String accountNumber;
    String reference;
    @JsonProperty("transactionDateTime")
    String transactionDateTime;
    String currency;
    @JsonProperty("paymentLinkId")
    String paymentLinkId;
    String signature;
    String status;

}
