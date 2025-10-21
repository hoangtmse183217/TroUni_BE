package com.trouni.tro_uni.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trouni.tro_uni.config.PayOSProperties;
import com.trouni.tro_uni.dto.request.payment.PayOSPaymentRequest;
import com.trouni.tro_uni.dto.request.payment.PayOSWebhookRequest;
import com.trouni.tro_uni.dto.response.payment.PayOSPaymentResponse;
import com.trouni.tro_uni.dto.response.payment.PaymentResponse;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.PaymentErrorCode;
import com.trouni.tro_uni.service.PaymentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * PaymentController - API endpoints cho thanh toán
 *
 * Endpoints:
 * - POST /api/payments/payos - Tạo thanh toán PayOS
 * - POST /api/payments/webhook - Webhook xác nhận thanh toán
 * - GET /api/payments/{id} - Lấy thông tin payment
 * - GET /api/payments/transaction/{code} - Lấy payment theo transaction code
 * - GET /api/payments/my-history - Lịch sử thanh toán của user
 * - DELETE /api/payments/{id}/cancel - Hủy thanh toán
 */
@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentService paymentService;
    PayOSProperties payOSProperties;

    /**
     * Tạo thanh toán PayOS
     *
     * @param request - Thông tin thanh toán PayOS
     * @return PayOSPaymentResponse - Response chứa URL liên kết thanh toán PayOS
     */
    @PostMapping("/payos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PayOSPaymentResponse> createPayOSPayment(
            @Valid @RequestBody PayOSPaymentRequest request) {

        log.info("Creating PayOS payment for amount: {}", request.getAmount());
        PayOSPaymentResponse response = paymentService.createPayOSPayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Webhook để xác nhận thanh toán từ ngân hàng
     *
     * Endpoint này sẽ được gọi bởi ngân hàng/payment gateway
     * khi có giao dịch chuyển khoản thành công
     *
     * @param rawRequestBody - Thông tin từ webhook
     * @return PaymentResponse - Payment đã được cập nhật
     */
    @PostMapping("/webhook")
    public ResponseEntity<PaymentResponse> handlePaymentWebhook(
            @RequestBody String rawRequestBody) {

        log.info("Received raw payment webhook request: {}", rawRequestBody);

        // Handle PayOS webhook URL validation request (empty body)
        if (rawRequestBody == null || rawRequestBody.trim().isEmpty()) {
            log.info("Received empty webhook request, likely PayOS URL validation. Returning 200 OK.");
            return ResponseEntity.ok().build();
        }

        log.debug("Attempting to validate and parse webhook request.");
        // Validate PayOS webhook signature and parse request in PayOSService
        PayOSWebhookRequest request = paymentService.validateAndParsePayOSWebhook(rawRequestBody);

        log.info("Webhook request successfully validated and parsed. OrderCode: {}", request.getOrderCode());

        try {
            PaymentResponse response = paymentService.confirmPayment(request);
            log.info("Payment webhook processed successfully for transaction: {}", request.getOrderCode());
            return ResponseEntity.ok(response);
        } catch (AppException e) {
            if (e.getErrorCode().equals(PaymentErrorCode.PAYMENT_NOT_FOUND.name())) {
                log.warn("Payment not found for webhook validation ping (orderCode: {}). Returning 200 OK.", request.getOrderCode());
                return ResponseEntity.ok().build(); // Return 200 OK for validation pings
            } else {
                throw e; // Re-throw other AppExceptions
            }
        }

    }

    /**
     * Lấy thông tin payment theo ID
     *
     * @param paymentId - ID của payment
     * @return PaymentResponse
     */
    @GetMapping("/{paymentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID paymentId) {

        log.info("Getting payment by ID: {}", paymentId);
        PaymentResponse response = paymentService.getPaymentById(paymentId);

        return ResponseEntity.ok(response);
    }

    /**
     * Lấy payment theo transaction code
     *
     * @param transactionCode - Mã giao dịch
     * @return PaymentResponse
     */
    @GetMapping("/transaction/{transactionCode}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> getPaymentByTransactionCode(
            @PathVariable String transactionCode) {

        log.info("Getting payment by transaction code: {}", transactionCode);
        PaymentResponse response = paymentService.getPaymentByTransactionCode(transactionCode);

        return ResponseEntity.ok(response);
    }

    /**
     * Lấy lịch sử thanh toán của user hiện tại
     *
     * @return List<PaymentResponse>
     */
    @GetMapping("/my-history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PaymentResponse>> getMyPaymentHistory() {

        log.info("Getting payment history for current user");
        List<PaymentResponse> response = paymentService.getMyPaymentHistory();

        return ResponseEntity.ok(response);
    }

    /**
     * Lấy lịch sử thanh toán với phân trang
     *
     * @param pageable - Thông tin phân trang
     * @return Page<PaymentResponse>
     */
    @GetMapping("/my-history/paginated")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PaymentResponse>> getMyPaymentHistoryPaginated(Pageable pageable) {

        log.info("Getting paginated payment history for current user");
        Page<PaymentResponse> response = paymentService.getMyPaymentHistoryPaginated(pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * Hủy thanh toán
     *
     * @param paymentId - ID của payment cần hủy
     * @return PaymentResponse
     */
    @DeleteMapping("/{paymentId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable UUID paymentId) {

        log.info("Cancelling payment: {}", paymentId);
        PaymentResponse response = paymentService.cancelPayment(paymentId);

        return ResponseEntity.ok(response);
    }

    /**
     * Kiểm tra trạng thái thanh toán
     *
     * @param transactionCode - Mã giao dịch
     * @return Map chứa status
     */
    @GetMapping("/status/{transactionCode}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> checkPaymentStatus(
            @PathVariable String transactionCode) {

        log.info("Checking payment status for transaction: {}", transactionCode);
        PaymentResponse payment = paymentService.getPaymentByTransactionCode(transactionCode);

        return ResponseEntity.ok(Map.of(
                "transactionCode", transactionCode,
                "status", payment.getStatus().name(),
                "amount", payment.getAmount().toString()
        ));
    }

    /**
     * Xử lý callback từ PayOS khi người dùng hủy thanh toán
     *
     * @param code - Mã phản hồi từ PayOS
     * @param id - ID giao dịch từ PayOS
     * @param cancel - Trạng thái hủy
     * @param status - Trạng thái thanh toán
     * @param orderCode - Mã đơn hàng (transactionCode của ứng dụng)
     * @return Chuyển hướng đến trang hủy thanh toán
     */
    @GetMapping("/cancel")
    public ResponseEntity<Void> handlePayOSCancel(
            @RequestParam("code") String code,
            @RequestParam("id") String id,
            @RequestParam("cancel") boolean cancel,
            @RequestParam("status") String status,
            @RequestParam("orderCode") String orderCode) {

        log.info("Received PayOS cancel callback. OrderCode: {}, Status: {}", orderCode, status);

        // Call service to update payment status to CANCELLED
        paymentService.handlePayOSCancel(orderCode, status);

        // Redirect to a user-friendly cancellation page
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/cancel_payment.html")
                .build();
    }
}
