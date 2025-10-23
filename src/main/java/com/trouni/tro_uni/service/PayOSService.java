package com.trouni.tro_uni.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trouni.tro_uni.config.PayOSProperties;
import com.trouni.tro_uni.dto.request.payment.CreatePaymentRequestServiceDto;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.PaymentErrorCode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;

import static java.util.Collections.singletonList;


@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PayOSService {

    PayOSProperties payOSProperties;
    PayOS payOS;

    public PayOSService(PayOSProperties payOSProperties) {
        this.payOSProperties = payOSProperties;
        this.payOS = new PayOS(payOSProperties.getClientId(), payOSProperties.getApiKey(), payOSProperties.getChecksumKey());
    }

    /**
     * Tạo liên kết thanh toán PayOS
     *
     * @param request - Đối tượng yêu cầu tạo liên kết thanh toán
     * @return String - URL liên kết thanh toán PayOS
     */
    public CreatePaymentLinkResponse createPaymentLink(CreatePaymentRequestServiceDto request) {
        try {
            long orderCode = System.currentTimeMillis() / 1000;
            PaymentLinkItem item = PaymentLinkItem.builder()
                    .name(request.getProductName())
                    .quantity(1)
                    .price(request.getPrice())
                    .build();

            CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                    .orderCode(orderCode)
                    .description(request.getDescription())
                    .amount(request.getPrice())
                    .items(singletonList(item))
                    .returnUrl(request.getReturnUrl())
                    .cancelUrl(request.getCancelUrl())
                    .build();

            return payOS.paymentRequests().create(paymentData);

        } catch (Exception e) {
            log.error("Error creating PayOS payment link: {}", e.getMessage());
            throw new AppException(PaymentErrorCode.PAYMENT_PROCESSING_FAILED);
        }
    }

    /**
     * Xác thực webhook từ PayOS
     *
     * @param webhookData - Dữ liệu webhook nhận được
     * @return boolean - true nếu webhook hợp lệ, false nếu không
     */
    public vn.payos.model.webhooks.WebhookData verifyWebhook(String rawRequestBody) {
        try {
            return payOS.webhooks().verify(rawRequestBody);
        } catch (Exception e) {
            log.error("Error verifying PayOS webhook: {}", e.getMessage());
            throw new AppException(PaymentErrorCode.PAYMENT_PROCESSING_FAILED);
        }
    }
}
