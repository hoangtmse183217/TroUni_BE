package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.request.payment.PaymentWebhookRequest;
import com.trouni.tro_uni.dto.request.payment.VietQRPaymentRequest;
import com.trouni.tro_uni.dto.response.payment.PaymentResponse;
import com.trouni.tro_uni.dto.response.payment.VietQRPaymentResponse;
import com.trouni.tro_uni.entity.Payment;
import com.trouni.tro_uni.entity.Subscription;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.PaymentMethod;
import com.trouni.tro_uni.enums.PaymentStatus;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.PaymentErrorCode;
import com.trouni.tro_uni.exception.errorcode.UserErrorCode;
import com.trouni.tro_uni.repository.PaymentRepository;
import com.trouni.tro_uni.repository.SubscriptionRepository;
import com.trouni.tro_uni.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * PaymentService - Service xử lý thanh toán
 *
 * Chức năng chính:
 * - Tạo thanh toán VietQR
 * - Xác nhận thanh toán qua webhook
 * - Quản lý lịch sử thanh toán
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {

    PaymentRepository paymentRepository;
    UserRepository userRepository;
    SubscriptionRepository subscriptionRepository;
    VietQRService vietQRService;

    /**
     * Tạo thanh toán VietQR
     *
     * @param request - Thông tin thanh toán
     * @return VietQRPaymentResponse - Response chứa QR code
     */
    @Transactional
    public VietQRPaymentResponse createVietQRPayment(VietQRPaymentRequest request) {
        // Lấy user hiện tại từ SecurityContext
        User currentUser = getCurrentUser();

        // Tạo mã giao dịch unique
        String transactionCode = generateTransactionCode();

        // Tạo Payment entity
        Payment payment = new Payment();
        payment.setUser(currentUser);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(PaymentMethod.VIETQR.name());
        payment.setTransactionCode(transactionCode);
        payment.setStatus(PaymentStatus.PENDING.name());
        payment.setCreatedAt(LocalDateTime.now());

        // Nếu có subscriptionId, set subscription
        if (request.getSubscriptionId() != null) {
            Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                    .orElseThrow(() -> new AppException(PaymentErrorCode.PAYMENT_FAILED));
            payment.setSubscription(subscription);
        }

        // Lưu payment
        Payment savedPayment = paymentRepository.save(payment);

        // Tạo description
        String description = request.getDescription() != null
                ? request.getDescription()
                : "Thanh toan TroUni";

        // Generate VietQR
        String qrCodeBase64 = vietQRService.generateVietQRBase64(
                request.getAmount(),
                description,
                transactionCode
        );

        String qrCodeUrl = vietQRService.generateVietQRUrl(
                request.getAmount(),
                description,
                transactionCode
        );

        // Get bank info
        Map<String, String> bankInfo = vietQRService.getBankInfo();

        // Build response
        VietQRPaymentResponse response = VietQRPaymentResponse.builder()
                .paymentId(savedPayment.getId())
                .transactionCode(transactionCode)
                .amount(request.getAmount())
                .status(PaymentStatus.PENDING)
                .qrCodeBase64(qrCodeBase64)
                .qrCodeUrl(qrCodeUrl)
                .description(description)
                .createdAt(savedPayment.getCreatedAt())
                .expiresAt(LocalDateTime.now().plusMinutes(15)) // QR code hết hạn sau 15 phút
                .bankAccountNumber(bankInfo.get("accountNumber"))
                .bankAccountName(bankInfo.get("accountName"))
                .bankName(bankInfo.get("bankName"))
                .build();

        log.info("Created VietQR payment for user: {}, transaction: {}",
                currentUser.getUsername(), transactionCode);

        return response;
    }

    /**
     * Xác nhận thanh toán qua webhook
     *
     * @param request - Thông tin từ webhook
     * @return PaymentResponse - Payment đã được cập nhật
     */
    @Transactional
    public PaymentResponse confirmPayment(PaymentWebhookRequest request) {
        // Tìm payment theo transaction code
        Payment payment = paymentRepository.findByTransactionCode(request.getTransactionCode())
                .orElseThrow(() -> new AppException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // Kiểm tra payment đã được xử lý chưa
        if (PaymentStatus.COMPLETED.name().equals(payment.getStatus())) {
            throw new AppException(PaymentErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        // Kiểm tra số tiền
        if (payment.getAmount().compareTo(request.getAmount()) != 0) {
            throw new AppException(PaymentErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        // Cập nhật status
        payment.setStatus(PaymentStatus.COMPLETED.name());
        Payment updatedPayment = paymentRepository.save(payment);

        // Nếu có subscription, cập nhật subscription
        if (payment.getSubscription() != null) {
            updateSubscription(payment.getSubscription());
        }

        log.info("Payment confirmed: {}, user: {}",
                payment.getTransactionCode(),
                payment.getUser().getUsername());

        return PaymentResponse.fromPayment(updatedPayment);
    }

    /**
     * Lấy payment theo ID
     */
    public PaymentResponse getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        return PaymentResponse.fromPayment(payment);
    }

    /**
     * Lấy payment theo transaction code
     */
    public PaymentResponse getPaymentByTransactionCode(String transactionCode) {
        Payment payment = paymentRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new AppException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        return PaymentResponse.fromPayment(payment);
    }

    /**
     * Lấy lịch sử thanh toán của user hiện tại
     */
    public List<PaymentResponse> getMyPaymentHistory() {
        User currentUser = getCurrentUser();

        List<Payment> payments = paymentRepository.findByUser(currentUser);

        return payments.stream()
                .map(PaymentResponse::fromPayment)
                .collect(Collectors.toList());
    }

    /**
     * Lấy lịch sử thanh toán với phân trang
     */
    public Page<PaymentResponse> getMyPaymentHistoryPaginated(Pageable pageable) {
        User currentUser = getCurrentUser();

        Page<Payment> payments = paymentRepository.findByUserOrderByCreatedAtDesc(currentUser, pageable);

        return payments.map(PaymentResponse::fromPayment);
    }

    /**
     * Hủy thanh toán
     */
    @Transactional
    public PaymentResponse cancelPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // Kiểm tra payment có thể hủy không
        if (!PaymentStatus.PENDING.name().equals(payment.getStatus())) {
            throw new AppException(PaymentErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        // Kiểm tra quyền
        User currentUser = getCurrentUser();
        if (!payment.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }

        payment.setStatus(PaymentStatus.CANCELLED.name());
        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Payment cancelled: {}", payment.getTransactionCode());

        return PaymentResponse.fromPayment(updatedPayment);
    }

    /**
     * Generate unique transaction code
     */
    private String generateTransactionCode() {
        String code;
        do {
            code = "TRO" + System.currentTimeMillis();
        } while (paymentRepository.existsByTransactionCode(code));

        return code;
    }

    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_ROLE_INVALID));
    }

    /**
     * Update subscription after payment completed
     */
    private void updateSubscription(Subscription subscription) {
        // Logic cập nhật subscription (ví dụ: extend thời gian)
        subscription.setStatus("active");
        subscriptionRepository.save(subscription);

        log.info("Subscription updated: {}", subscription.getId());
    }
}
