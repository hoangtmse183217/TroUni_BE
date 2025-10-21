package com.trouni.tro_uni.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trouni.tro_uni.config.PayOSProperties;
import com.trouni.tro_uni.dto.request.payment.PayOSPaymentRequest;
import com.trouni.tro_uni.dto.request.payment.PayOSWebhookRequest;
import com.trouni.tro_uni.dto.request.payment.CreatePaymentRequestServiceDto;
import com.trouni.tro_uni.dto.response.payment.PayOSPaymentResponse;
import com.trouni.tro_uni.dto.response.payment.PaymentResponse;
import com.trouni.tro_uni.entity.Payment;
import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.entity.Subscription;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.PaymentMethod;
import com.trouni.tro_uni.enums.PaymentStatus;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.PaymentErrorCode;
import com.trouni.tro_uni.exception.errorcode.AuthenticationErrorCode;
import com.trouni.tro_uni.exception.errorcode.RoomErrorCode;
import com.trouni.tro_uni.repository.RoomRepository;
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
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * PaymentService - Service xử lý thanh toán
 *
 * Chức năng chính:
 * - Tạo thanh toán PayOS
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
    RoomRepository roomRepository;
    PayOSService payOSService;
    PayOSProperties payOSProperties;
    ObjectMapper objectMapper;

//    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository, SubscriptionRepository subscriptionRepository, RoomRepository roomRepository, PayOSService payOSService, PayOSProperties payOSProperties, ObjectMapper objectMapper) {
//        this.paymentRepository = paymentRepository;
//        this.userRepository = userRepository;
//        this.subscriptionRepository = subscriptionRepository;
//        this.roomRepository = roomRepository;
//        this.payOSService = payOSService;
//        this.payOSProperties = payOSProperties;
//        this.objectMapper = objectMapper;
//    }

    /**
     * Tạo thanh toán PayOS
     *
     * @param request - Thông tin thanh toán PayOS
     * @return PayOSPaymentResponse - Response chứa URL liên kết thanh toán PayOS
     */
    @Transactional
    public PayOSPaymentResponse createPayOSPayment(PayOSPaymentRequest request) {
        User currentUser = getCurrentUser();

        // Tạo Payment entity
        Payment payment = new Payment();
        payment.setUser(currentUser);
        payment.setAmount(BigDecimal.valueOf(request.getAmount()));
        payment.setPaymentMethod(PaymentMethod.PAYOS.name());
        payment.setStatus(PaymentStatus.PENDING.name());
        payment.setCreatedAt(LocalDateTime.now());

//        if (request.getSubscriptionId() != null) {
//            Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
//                    .orElseThrow(() -> new AppException(PaymentErrorCode.PAYMENT_NOT_FOUND));
//            payment.setSubscription(subscription);
//        }

        if (request.getRoomId() != null) {
            Room room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new AppException(RoomErrorCode.ROOM_NOT_FOUND));
            payment.setRoom(room);
        }

        String descriptionForPayOS = request.getDescription() != null
                ? request.getDescription()
                : "Thanh toan TroUni qua PayOS";

        CreatePaymentRequestServiceDto createPaymentRequestServiceDto = CreatePaymentRequestServiceDto.builder()
                .productName(descriptionForPayOS)
                .description(descriptionForPayOS)
                .price(request.getAmount().longValue())
                .returnUrl(request.getReturnUrl())
                .cancelUrl(request.getCancelUrl())
                .build();

        CreatePaymentLinkResponse payOSResponse = payOSService.createPaymentLink(createPaymentRequestServiceDto);
        String checkoutUrl = payOSResponse.getCheckoutUrl();

        // Lấy orderCode từ phản hồi của PayOS và lưu vào transactionCode
        String payosOrderCode = String.valueOf(payOSResponse.getOrderCode());
        payment.setTransactionCode(payosOrderCode);

        Payment savedPayment = paymentRepository.save(payment);

        PayOSPaymentResponse response = PayOSPaymentResponse.builder()
                .paymentId(savedPayment.getId())
                .transactionCode(payosOrderCode)
                .amount(request.getAmount())
                .status(PaymentStatus.PENDING)
                .checkoutUrl(checkoutUrl)
                .description(descriptionForPayOS)
                .createdAt(savedPayment.getCreatedAt())
                .expiresAt(LocalDateTime.now().plusMinutes(15)) // PayOS link typically expires in 15 minutes
                .build();

        log.info("Created PayOS payment for user: {}, transaction: {}",
                currentUser.getUsername(), payosOrderCode);

        return response;
    }

    public PayOSWebhookRequest validateAndParsePayOSWebhook(String rawRequestBody) {
        log.debug("Starting validateAndParsePayOSWebhook. Raw body: {}", rawRequestBody);
        try {
            // Verify webhook signature and get verified data from PayOS SDK
            WebhookData payosVerifiedWebhookData = payOSService.verifyWebhook(rawRequestBody);
            log.info("PayOS webhook signature verified for orderCode: {}", payosVerifiedWebhookData.getOrderCode());

            // Directly convert the verified WebhookData from PayOS SDK to the flattened PayOSWebhookRequest DTO
            PayOSWebhookRequest payOSWebhookRequest = objectMapper.convertValue(payosVerifiedWebhookData, PayOSWebhookRequest.class);

            return payOSWebhookRequest;
        } catch (Exception e) {
            log.error("Error validating PayOS webhook: {}", e.getMessage());
            throw new AppException(PaymentErrorCode.PAYMENT_PROCESSING_FAILED);
        }
    }

    /**
     * Xác nhận thanh toán qua webhook
     *
     * @param request - Thông tin từ webhook
     * @return PaymentResponse - Payment đã được cập nhật
     */
    @Transactional
    public PaymentResponse confirmPayment(PayOSWebhookRequest request) {
        log.debug("Starting confirmPayment for orderCode: {}", request.getOrderCode());

        // Tìm payment theo transaction code
        String transactionCodeToSearch = String.valueOf(request.getOrderCode());
        log.info("Searching for payment with transactionCode: {}", transactionCodeToSearch);
        Payment payment = paymentRepository.findByTransactionCode(transactionCodeToSearch)
                .orElseThrow(() -> {
                    log.error("Payment not found for transactionCode: {}", transactionCodeToSearch);
                    return new AppException(PaymentErrorCode.PAYMENT_NOT_FOUND);
                });
        log.info("Payment found: {} for transactionCode: {}", payment.getId(), transactionCodeToSearch);

        // Kiểm tra payment đã được xử lý chưa
        // Nếu đã hoàn thành, thất bại hoặc bị hủy, không xử lý lại
        if (PaymentStatus.COMPLETED.name().equals(payment.getStatus()) ||
            PaymentStatus.FAILED.name().equals(payment.getStatus()) ||
            PaymentStatus.CANCELLED.name().equals(payment.getStatus())) {
            throw new AppException(PaymentErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        // Kiểm tra số tiền
        if (payment.getAmount().compareTo(request.getAmount()) != 0) {
            throw new AppException(PaymentErrorCode.PAYMENT_AMOUNT_INVALID);
        }

        // Cập nhật status dựa trên trạng thái từ webhook
        PaymentStatus newStatus;
        if ("00".equals(request.getCode())) {
            newStatus = PaymentStatus.COMPLETED;
        } else if ("CANCELLED".equals(request.getStatus())) { // Sử dụng trường status mới
            newStatus = PaymentStatus.CANCELLED;
        } else {
            newStatus = PaymentStatus.FAILED;
        }

        switch (newStatus) {
            case COMPLETED:
                payment.setStatus(PaymentStatus.COMPLETED.name());
                // Nếu có subscription, cập nhật subscription
                if (payment.getSubscription() != null) {
                    updateSubscription(payment.getSubscription());
                }
                log.info("Payment confirmed as COMPLETED: {}, user: {}",
                        payment.getTransactionCode(),
                        payment.getUser().getUsername());
                break;
            case PROCESSING:
                payment.setStatus(PaymentStatus.PROCESSING.name());
                // If this payment is for a room, set the room status to 'rented'
                if (payment.getRoom() != null) {
                    Room roomToUpdate = payment.getRoom();
                    roomToUpdate.setStatus("rented");
                    roomRepository.save(roomToUpdate);
                    log.info("Room {} status set to 'rented' due to payment processing.", roomToUpdate.getId());
                }
                log.info("Payment status updated to PROCESSING: {}, user: {}",
                        payment.getTransactionCode(),
                        payment.getUser().getUsername());
                break;
            case FAILED:
                payment.setStatus(PaymentStatus.FAILED.name());
                log.warn("Payment confirmed as FAILED: {}, user: {}",
                        payment.getTransactionCode(),
                        payment.getUser().getUsername());
                break;
            case CANCELLED:
                payment.setStatus(PaymentStatus.CANCELLED.name());
                // If this payment was for a room, set the room status back to 'available'
                if (payment.getRoom() != null) {
                    Room roomToUpdate = payment.getRoom();
                    roomToUpdate.setStatus("available");
                    roomRepository.save(roomToUpdate);
                    log.warn("Room {} status set back to 'available' due to payment cancellation.", roomToUpdate.getId());
                }
                log.warn("Payment confirmed as CANCELLED: {}, user: {}",
                        payment.getTransactionCode(),
                        payment.getUser().getUsername());
                break;
            default:
                // Nếu webhook gửi trạng thái không mong muốn hoặc PENDING, giữ nguyên trạng thái hiện tại
                log.warn("Received unexpected webhook status '{}' for transaction {}. Keeping current status '{}'.",
                        request.getCode(), request.getOrderCode(), payment.getStatus());
                break;
        }

        Payment updatedPayment = paymentRepository.save(payment);
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

    @Transactional
    public PaymentResponse handlePayOSCancel(String transactionCode, String status) {
        log.info("Handling PayOS cancel for transactionCode: {}, status: {}", transactionCode, status);

        Payment payment = paymentRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new AppException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        if (PaymentStatus.CANCELLED.name().equals(payment.getStatus())) {
            log.warn("Payment {} is already CANCELLED. No further action needed.", transactionCode);
            return PaymentResponse.fromPayment(payment);
        }

        if ("CANCELLED".equalsIgnoreCase(status)) {
            payment.setStatus(PaymentStatus.CANCELLED.name());
            // If this payment was for a room, set the room status back to 'available'
            if (payment.getRoom() != null) {
                Room roomToUpdate = payment.getRoom();
                roomToUpdate.setStatus("available");
                roomRepository.save(roomToUpdate);
                log.warn("Room {} status set back to 'available' due to payment cancellation.", roomToUpdate.getId());
            }
            log.info("Payment {} status updated to CANCELLED.", transactionCode);
        } else {
            log.warn("PayOS cancel callback received with status: {} for transactionCode: {}. No status change performed.", status, transactionCode);
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return PaymentResponse.fromPayment(updatedPayment);
    }

    /**
     * Generate unique transaction code
     */
    private String generateTransactionCode() {
        String code;
        do {
            code = String.valueOf(System.currentTimeMillis());
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
                .orElseThrow(() -> new AppException(AuthenticationErrorCode.UNAUTHORIZED));
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