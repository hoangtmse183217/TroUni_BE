package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.request.payment.PaymentWebhookRequest;
import com.trouni.tro_uni.dto.request.payment.VietQRPaymentRequest;
import com.trouni.tro_uni.dto.response.payment.PaymentResponse;
import com.trouni.tro_uni.dto.response.payment.VietQRPaymentResponse;
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
 * - POST /api/payments/vietqr - Tạo thanh toán VietQR
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

    /**
     * Tạo thanh toán VietQR
     *
     * @param request - Thông tin thanh toán
     * @return VietQRPaymentResponse - Response chứa QR code
     */
    @PostMapping("/viet-qr")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VietQRPaymentResponse> createVietQRPayment(
            @Valid @RequestBody VietQRPaymentRequest request) {

        log.info("Creating VietQR payment for amount: {}", request.getAmount());
        VietQRPaymentResponse response = paymentService.createVietQRPayment(request);

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
     * @param request - Thông tin từ webhook
     * @return PaymentResponse - Payment đã được cập nhật
     */
    @PostMapping("/webhook")
    public ResponseEntity<PaymentResponse> handlePaymentWebhook(
            @RequestBody PaymentWebhookRequest request) {

        log.info("Received payment webhook for transaction: {}", request.getTransactionCode());

        // TODO: Validate webhook signature để đảm bảo request từ nguồn tin cậy
        // String signature = request.getSignature();
        // if (!validateSignature(signature)) {
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        // }

        PaymentResponse response = paymentService.confirmPayment(request);

        return ResponseEntity.ok(response);
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
}
