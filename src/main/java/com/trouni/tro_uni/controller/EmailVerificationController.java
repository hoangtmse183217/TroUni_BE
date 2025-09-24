package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.ApiResponse;
import com.trouni.tro_uni.dto.ResendCodeRequest;
import com.trouni.tro_uni.dto.ResendCodeResponse;
import com.trouni.tro_uni.dto.VerifyEmailRequest;
import com.trouni.tro_uni.dto.VerifyEmailResponse;
import com.trouni.tro_uni.dto.VerificationStatusResponse;
import com.trouni.tro_uni.entity.EmailVerification;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * EmailVerificationController - Controller xử lý xác thực email
 * <p>
 * Chức năng chính:
 * - API verify email với mã 6 số
 * - API gửi lại mã xác thực
 * - API kiểm tra trạng thái verification
 * - API lấy thông tin verification
 * <p>
 * @author TroUni Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/email-verification")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;


    /**
     * API xác thực email với mã 6 số
     * <p>
     * Endpoint: POST /api/email-verification/verify
     *<p>
     * Request body:
     * {
     *   "email": "user@example.com",
     *   "verificationCode": "123456"
     * }
     * <p>
     * Response thành công:
     * {
     *   "code": "200",
     *   "message": "Email verified successfully!",
     *   "result": {
     *     "email": "user@example.com",
     *     "verified": true,
     *     "verifiedAt": "2024-01-01T10:00:00"
     *   }
     * }
     * <p>
     * @param request - Thông tin verify email
     * @return ResponseEntity - Kết quả verification
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        try {
            boolean isVerified = emailVerificationService.verifyEmail(
                    request.getEmail(),
                    request.getVerificationCode()
            );

            if (isVerified) {
                VerifyEmailResponse response = VerifyEmailResponse.success(
                        request.getEmail(), 
                        java.time.LocalDateTime.now()
                );

                return ResponseEntity.ok(ApiResponse.builder()
                        .code("200")
                        .message("Email verified successfully!")
                        .data(response)
                        .build());
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.builder()
                                .code("400")
                                .message("Verification failed")
                                .build());
            }

        } catch (AppException e) {
            // Sử dụng GlobalExceptionHandler để xử lý AppException
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during email verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .code("500")
                            .message("Internal server error")
                            .build());
        }
    }

    /**
     * API gửi lại mã xác thực
     * <p>
     * Endpoint: POST /api/email-verification/resend
     *<p>
     * Request body:
     * {
     *   "email": "user@example.com"
     * }
     * <p>
     * Response thành công:
     * {
     *   "code": "200",
     *   "message": "Verification code resent successfully!",
     *   "result": {
     *     "email": "user@example.com",
     *     "expiresAt": "2024-01-01T10:05:00"
     *   }
     * }
     * <p>
     * @param request - Thông tin email cần gửi lại mã
     * @return ResponseEntity - Kết quả gửi lại mã
     */
    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@Valid @RequestBody ResendCodeRequest request) {
        try {
            EmailVerification verification = emailVerificationService.resendVerificationCode(request.getEmail());

            ResendCodeResponse response = ResendCodeResponse.success(
                    verification.getEmail(),
                    verification.getExpiresAt()
            );

            return ResponseEntity.ok(ApiResponse.builder()
                    .code("200")
                    .message("Verification code resent successfully!")
                    .data(response)
                    .build());

        } catch (AppException e) {
            // Sử dụng GlobalExceptionHandler để xử lý AppException
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during resend verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .code("500")
                            .message("Internal server error")
                            .build());
        }
    }


    /**
     * API lấy thông tin chi tiết verification
     * <p>
     * Endpoint: GET /api/email-verification/info?email=user@example.com
     *<p>
     * Response thành công:
     * {
     *   "code": "200",
     *   "message": "Verification info retrieved successfully",
     *   "result": {
     *     "email": "user@example.com",
     *     "verified": false,
     *     "attempts": 1,
     *     "maxAttempts": 3,
     *     "expiresAt": "2024-01-01T10:05:00",
     *     "canAttempt": true
     *   }
     * }
     * <p>
     * @param email - Email cần lấy thông tin
     * @return ResponseEntity - Thông tin verification
     */
    @GetMapping("/info")
    public ResponseEntity<?> getVerificationInfo(@RequestParam String email) {
        try {
            Optional<EmailVerification> verification = emailVerificationService.getVerificationInfo(email);

            if (verification.isEmpty()) {
                return ResponseEntity.notFound()
                        .build();
            }

            EmailVerification ver = verification.get();
            VerificationStatusResponse response = VerificationStatusResponse.fromVerification(
                    ver.getEmail(),
                    ver.isVerified(),
                    ver.getAttempts(),
                    ver.getMaxAttempts(),
                    ver.getExpiresAt(),
                    ver.canAttempt(),
                    ver.isExpired()
            );

            return ResponseEntity.ok(ApiResponse.builder()
                    .code("200")
                    .message("Verification info retrieved successfully")
                    .data(response)
                    .build());

        } catch (Exception e) {
            log.error("Unexpected error during info retrieval: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .code("500")
                            .message("Internal server error")
                            .build());
        }
    }

}
