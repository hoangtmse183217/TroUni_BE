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

    @Value("${app.mail.from-name:TroUni}")
    private String fromName;

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
     * Test gửi email đơn giản (Async)
     * <p>
     * @param toEmail - Email người nhận
     * @param subject - Tiêu đề
     * @param content - Nội dung
     */
    @Async("emailTaskExecutor")
    public void sendSimpleEmail(String toEmail, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("Simple email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send simple email to {}: {}", toEmail, e.getMessage());
            // Không throw exception để tránh rollback transaction
        }
    }
}
