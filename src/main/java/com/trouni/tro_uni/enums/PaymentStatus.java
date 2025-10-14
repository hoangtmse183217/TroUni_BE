package com.trouni.tro_uni.enums;

/**
 * PaymentStatus Enum - Trạng thái thanh toán
 *
 * Các trạng thái:
 * - PENDING: Chờ thanh toán (đã tạo QR nhưng chưa nhận được tiền)
 * - PROCESSING: Đang xử lý (đã nhận được webhook từ ngân hàng)
 * - COMPLETED: Thanh toán thành công
 * - FAILED: Thanh toán thất bại
 * - CANCELLED: Thanh toán bị hủy
 * - REFUNDED: Đã hoàn tiền
 * - EXPIRED: QR code đã hết hạn
 */
public enum PaymentStatus {
    PENDING,      // Chờ thanh toán
    PROCESSING,   // Đang xử lý
    COMPLETED,    // Thành công
    FAILED,       // Thất bại
    CANCELLED,    // Đã hủy
    REFUNDED,     // Đã hoàn tiền
    EXPIRED       // Hết hạn
}
