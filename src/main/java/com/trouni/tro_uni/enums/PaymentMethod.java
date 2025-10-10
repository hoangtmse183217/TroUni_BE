package com.trouni.tro_uni.enums;

/**
 * PaymentMethod Enum - Phương thức thanh toán
 *
 * Các phương thức:
 * - VIETQR: Thanh toán qua VietQR (chuyển khoản ngân hàng)
 * - MOMO: Ví điện tử MoMo
 * - ZALOPAY: Ví điện tử ZaloPay
 * - VNPAY: Cổng thanh toán VNPay
 * - BANK_TRANSFER: Chuyển khoản ngân hàng thông thường
 * - CASH: Tiền mặt
 */
public enum PaymentMethod {
    VIETQR,         // VietQR
    MOMO,           // MoMo
    ZALOPAY,        // ZaloPay
    VNPAY,          // VNPay
    BANK_TRANSFER,  // Chuyển khoản
    CASH            // Tiền mặt
}
