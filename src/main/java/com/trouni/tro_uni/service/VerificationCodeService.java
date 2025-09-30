package com.trouni.tro_uni.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * VerificationCodeService - Service sinh mã xác thực
 *
 * Chức năng chính:
 * - Sinh mã 6 số ngẫu nhiên
 * - Validate format mã xác thực
 * - Tạo mã có độ bảo mật cao
 *
 * @author TroUni Team
 * @version 1.0
 */
@Slf4j
@Service
public class VerificationCodeService {

    private static final String DIGITS = "0123456789";
    private static final int CODE_LENGTH = 6;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Sinh mã xác thực 6 số ngẫu nhiên
     *
     * Sử dụng SecureRandom để đảm bảo tính ngẫu nhiên cao
     * Mã gồm 6 chữ số từ 0-9
     *
     * @return String - Mã xác thực 6 số
     */
    public String generateVerificationCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(DIGITS.length());
            code.append(DIGITS.charAt(randomIndex));
        }
        
        String generatedCode = code.toString();
        log.info("Generated verification code: {}", generatedCode);
        
        return generatedCode;
    }

    /**
     * Validate format mã xác thực
     *
     * Kiểm tra:
     * - Mã có đúng 6 ký tự không
     * - Tất cả ký tự đều là số
     * - Không null hoặc empty
     *
     * @param code - Mã cần validate
     * @return boolean - true nếu hợp lệ
     */
    public boolean isValidFormat(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        
        if (code.length() != CODE_LENGTH) {
            return false;
        }
        
        return code.matches("\\d{" + CODE_LENGTH + "}");
    }

    /**
     * Sinh mã xác thực mới và validate
     *
     * @return String - Mã xác thực hợp lệ
     */
    public String generateValidCode() {
        String code;
        do {
            code = generateVerificationCode();
        } while (!isValidFormat(code));
        
        return code;
    }

    /**
     * Tạo mã xác thực với format đẹp (có dấu gạch ngang)
     *
     * Ví dụ: 123-456
     *
     * @return String - Mã xác thực có format
     */
    public String generateFormattedCode() {
        String code = generateValidCode();
        return code.substring(0, 3) + "-" + code.substring(3);
    }

    /**
     * Làm sạch mã xác thực (bỏ dấu gạch ngang)
     *
     * @param formattedCode - Mã có format
     * @return String - Mã sạch
     */
    public String cleanCode(String formattedCode) {
        if (formattedCode == null) {
            return null;
        }
        return formattedCode.replaceAll("-", "");
    }
}

