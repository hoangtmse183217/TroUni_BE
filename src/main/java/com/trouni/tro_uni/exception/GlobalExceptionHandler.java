package com.trouni.tro_uni.exception;

import com.trouni.tro_uni.dto.ApiResponse;
import com.trouni.tro_uni.exception.errorcode.GeneralErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import io.jsonwebtoken.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.Objects;

/**
 * GlobalExceptionHandler - Xử lý exception toàn cục cho ứng dụng
 * 
 * Chức năng chính:
 * - Bắt và xử lý tất cả exception trong ứng dụng
 * - Chuyển đổi exception thành response format chuẩn
 * - Log lỗi để debug
 * - Trả về response phù hợp cho từng loại lỗi
 * 
 * @author TroUni Team
 * @version 1.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý AppException - Exception tùy chỉnh của ứng dụng
     * 
     * AppException chứa ErrorCode với thông tin chi tiết về lỗi
     * Sử dụng ErrorCode để xác định status code và message phù hợp
     * 
     * @param exception - AppException cần xử lý
     * @return ResponseEntity<ApiResponse<?>> - Response với thông tin lỗi
     */
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(ApiResponse.builder()
                        .code(exception.getErrorCode())
                        .message(exception.getErrorMessage())
                        .build());
    }

    /**
     * Xử lý AccessDeniedException - Khi user không có quyền truy cập
     * 
     * Thường xảy ra khi:
     * - User chưa đăng nhập
     * - User không có role phù hợp
     * - Token hết hạn hoặc không hợp lệ
     * 
     * @return ResponseEntity<ApiResponse<?>> - Response 403 Forbidden
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> handlingAccessDeniedException() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.builder()
                        .code("ACCESS_DENIED")
                        .message("Access denied")
                        .build()
                );
    }

    /**
     * Xử lý MethodArgumentNotValidException - Lỗi validation input
     * 
     * Thường xảy ra khi:
     * - @Valid annotation validation fail
     * - Required field bị thiếu
     * - Format dữ liệu không đúng
     * 
     * @param exception - MethodArgumentNotValidException
     * @return ResponseEntity<ApiResponse<?>> - Response 400 Bad Request
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingValidation(MethodArgumentNotValidException exception) {
        // Lấy message từ @NotBlank, @Email, etc.
        String fieldError = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();

        return ResponseEntity.badRequest()
                .body(ApiResponse.builder()
                        .code("VALIDATION_ERROR")
                        .message(fieldError != null ? fieldError : "Validation failed")
                        .build());
    }

    /**
     * Xử lý SQLException - Lỗi database
     * 
     * Thường xảy ra khi:
     * - Connection database bị lỗi
     * - Query SQL không hợp lệ
     * - Constraint violation
     * - Timeout
     * 
     * @param exception - SQLException
     * @return ResponseEntity<ApiResponse<?>> - Response 400 Bad Request
     */
    @ExceptionHandler(value = SQLException.class)
    ResponseEntity<ApiResponse<?>> handlingSQLServerException(SQLException exception) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.builder()
                        .code(exception.getSQLState())
                        .message(exception.getMessage())
                        .build());
    }

    /**
     * Xử lý JwtException - Lỗi JWT token
     * 
     * Thường xảy ra khi:
     * - Token không hợp lệ
     * - Token hết hạn
     * - Signature không đúng
     * - Token bị malformed
     * 
     * @param ex - JwtException
     * @return ResponseEntity<String> - Response 401 Unauthorized
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwtException(JwtException ex) {
        log.error("JWT Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalid");
    }

    /**
     * Xử lý Exception - Catch-all cho các exception khác
     * 
     * Xử lý tất cả exception không được handle bởi các method trên
     * Trả về generic error response để tránh expose internal error
     * 
     * @return ResponseEntity<ApiResponse<?>> - Response 500 Internal Server Error
     */
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingException() {
        return ResponseEntity.internalServerError()
                .body(ApiResponse.builder()
                        .code("UNCATEGORIZED_EXCEPTION")
                        .message("Uncategorized exception")
                        .build());
    }
}