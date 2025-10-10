package com.trouni.tro_uni.controller;

import com.trouni.tro_uni.dto.common.ApiResponse;
import com.trouni.tro_uni.dto.request.ResendCodeRequest;
import com.trouni.tro_uni.dto.response.ResendCodeResponse;
import com.trouni.tro_uni.dto.request.VerifyEmailRequest;
import com.trouni.tro_uni.dto.response.VerifyEmailResponse;
import com.trouni.tro_uni.dto.response.VerificationStatusResponse;
import com.trouni.tro_uni.entity.EmailVerification;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * EmailVerificationController - Controller xử lý xác thực email
 */
@Slf4j
@RestController
@RequestMapping("/email-verification")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;


    /**
     * API xác thực email với mã 6 số

     * Endpoint: POST /api/email-verification/verify

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

     * Endpoint: POST /api/email-verification/resend

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

     * Endpoint: GET /api/email-verification/info?email=user@example.com

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
