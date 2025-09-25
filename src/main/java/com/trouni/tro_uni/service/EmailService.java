package com.trouni.tro_uni.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * EmailService - Service gửi email
 * <p>
 * Chức năng chính:
 * - Gửi email verification code
 * - Gửi email thông báo
 * - Template email đẹp
 * - Error handling cho email
 * <p>
 * @author TroUni Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@trouni.com}")
    private String fromEmail;

    /**
     * Gửi email verification code (Async)
     * <p>
     * @param toEmail - Email người nhận
     * @param verificationCode - Mã xác thực 6 số
     * @param username - Tên người dùng
     */
    @Async("emailTaskExecutor")
    public void sendVerificationEmail(String toEmail, String verificationCode, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("🔐 Xác thực email - TroUni");
            
            String emailBody = buildVerificationEmailBody(username, verificationCode);
            message.setText(emailBody);
            
            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
            // Không throw exception để tránh rollback transaction
            // Chỉ log lỗi và tiếp tục
        }
    }

    /**
     * Gửi email chào mừng sau khi verify thành công (Async)
     * <p>
     * @param toEmail - Email người nhận
     * @param username - Tên người dùng
     */
    @Async("emailTaskExecutor")
    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("🎉 Chào mừng đến với TroUni!");
            
            String emailBody = buildWelcomeEmailBody(username);
            message.setText(emailBody);
            
            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
            // Không throw exception vì đây là email thông báo, không quan trọng
        }
    }

    /**
     * Xây dựng nội dung email verification
     * <p>
     * @param username - Tên người dùng
     * @param verificationCode - Mã xác thực
     * @return String - Nội dung email
     */
    private String buildVerificationEmailBody(String username, String verificationCode) {
        return String.format("""
            🏠 Chào mừng bạn đến với TroUni!
            
            Xin chào %s,
            
            Cảm ơn bạn đã đăng ký tài khoản tại TroUni - nền tảng tìm kiếm phòng trọ hàng đầu!
            
            📧 Để hoàn tất đăng ký, vui lòng sử dụng mã xác thực sau:
            
            🔐 MÃ XÁC THỰC: %s
            
            ⏰ Mã này có hiệu lực trong 5 phút.
            
            📱 Nhập mã này vào ứng dụng để xác thực email của bạn.
            
            ⚠️ Lưu ý:
            - Không chia sẻ mã này với bất kỳ ai
            - Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này
            - Mã chỉ có thể sử dụng 1 lần
            
            🚀 Sau khi xác thực, bạn có thể:
            - Đăng nhập vào tài khoản
            - Tìm kiếm phòng trọ phù hợp
            - Liên hệ với chủ trọ
            - Quản lý hồ sơ cá nhân
            
            Chúc bạn có trải nghiệm tuyệt vời với TroUni!
            
            ---
            Trân trọng,
            Đội ngũ TroUni
            📧 Email: %s
            🌐 Website: https://trouni.com
            """, username, verificationCode, fromEmail);
    }

    /**
     * Xây dựng nội dung email chào mừng
     * <p>
     * @param username - Tên người dùng
     * @return String - Nội dung email
     */
    private String buildWelcomeEmailBody(String username) {
        return String.format("""
            🎉 Chào mừng bạn đến với TroUni!
            
            Xin chào %s,
            
            🎊 Chúc mừng! Email của bạn đã được xác thực thành công.
            
            Tài khoản TroUni của bạn đã sẵn sàng sử dụng!
            
            🚀 Bạn có thể bắt đầu:
            - Tìm kiếm phòng trọ phù hợp
            - Lọc theo giá, vị trí, tiện ích
            - Liên hệ trực tiếp với chủ trọ
            - Lưu các phòng trọ yêu thích
            - Đánh giá và nhận xét
            
            💡 Mẹo sử dụng:
            - Cập nhật thông tin cá nhân đầy đủ
            - Thêm ảnh đại diện để tăng độ tin cậy
            - Sử dụng bộ lọc để tìm phòng phù hợp
            - Liên hệ sớm với chủ trọ để có cơ hội tốt nhất
            
            🔔 Thông báo:
            - Chúng tôi sẽ gửi thông báo về phòng trọ mới phù hợp
            - Cập nhật về các ưu đãi và khuyến mãi
            - Tin tức và mẹo tìm phòng trọ
            
            Cảm ơn bạn đã tin tưởng TroUni!
            
            ---
            Trân trọng,
            Đội ngũ TroUni
            📧 Email: %s
            🌐 Website: https://trouni.com
            """, username, fromEmail);
    }

    /**
     * Gửi email reset password (Async)
     * <p>
     * @param toEmail - Email người nhận
     * @param resetToken - Token reset password
     * @param username - Tên người dùng
     */
    @Async("emailTaskExecutor")
    public void sendPasswordResetEmail(String toEmail, String resetToken, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("🔑 Reset Password - TroUni");
            
            String emailBody = buildPasswordResetEmailBody(username, resetToken);
            message.setText(emailBody);
            
            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            // Không throw exception để tránh rollback transaction
        }
    }

    /**
     * Xây dựng nội dung email reset password
     * <p>
     * @param username - Tên người dùng
     * @param resetToken - Token reset password
     * @return String - Nội dung email
     */
    private String buildPasswordResetEmailBody(String username, String resetToken) {
        return String.format("""
            🎯 Xin chào %s!
            
            Chúng tôi nhận được yêu cầu reset mật khẩu cho tài khoản TroUni của bạn.
            
            📝 Mã reset password của bạn là: %s
            
            ⏰ Mã này có hiệu lực trong 15 phút.
            
            🔒 Nếu bạn không yêu cầu reset mật khẩu, vui lòng bỏ qua email này.
            
            💡 Để bảo mật tài khoản:
            - Không chia sẻ mã này với bất kỳ ai
            - Sử dụng mật khẩu mạnh
            - Đăng xuất khỏi các thiết bị không tin cậy
            
            Chúc bạn có trải nghiệm tốt với TroUni!
            
            ---
            Trân trọng,
            Đội ngũ TroUni
            Email: %s
            """, username, resetToken, fromEmail);
    }

}
