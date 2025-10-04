package com.trouni.tro_uni.config;

import com.trouni.tro_uni.security.JwtAuthenticationFilter;
import com.trouni.tro_uni.security.CustomAuthenticationEntryPoint;
import com.trouni.tro_uni.security.CustomAccessDeniedHandler;
import com.trouni.tro_uni.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SecurityConfig - Cấu hình bảo mật cho ứng dụng
 * <p>
 * Chức năng chính:
 * - Cấu hình Spring Security
 * - Thiết lập JWT authentication
 * - Định nghĩa các endpoint public/private
 * - Cấu hình password encoder
 * - Thiết lập session management
 *
 * @author TroUni Team
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Dependencies injection
    private final JwtAuthenticationFilter jwtAuthenticationFilter;  // Filter xử lý JWT
    private final CustomUserDetailsService userDetailsService;      // Service load user details
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;  // Xử lý authentication errors
    private final CustomAccessDeniedHandler accessDeniedHandler;    // Xử lý authorization errors

    /**
     * Cấu hình Password Encoder
     * <p>
     * Sử dụng BCrypt để mã hóa password với salt tự động
     * - Strength mặc định là 10 (có thể tăng lên 12-14 cho bảo mật cao hơn)
     * - Tự động generate salt cho mỗi password
     *
     * @return PasswordEncoder - BCrypt encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cấu hình AuthenticationManager
     * <p>
     * AuthenticationManager được Spring Security sử dụng để xác thực user
     * - Tự động sử dụng UserDetailsService và PasswordEncoder
     * - Hỗ trợ nhiều loại authentication provider
     *
     * @param authConfig - AuthenticationConfiguration từ Spring
     * @return AuthenticationManager - Quản lý xác thực
     * @throws Exception - Khi có lỗi cấu hình
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    /**
     * Cấu hình CORS (Cross-Origin Resource Sharing)
     * <p>
     * Cho phép frontend từ localhost:5173 truy cập API
     * - Allowed origins: http://localhost:5173
     * - Allowed methods: GET, POST, PUT, DELETE, OPTIONS
     * - Allowed headers: Authorization, Content-Type
     * - Allow credentials: true
     *
     * @return CorsConfigurationSource - Cấu hình CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Cho phép origin từ frontend
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // Cho phép tất cả các HTTP methods
        configuration.addAllowedMethod("*");

        // Cho phép tất cả các headers
        configuration.addAllowedHeader("*");

        // Cho phép gửi credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Cấu hình cho tất cả paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Cấu hình Security Filter Chain
     * <p>
     * Định nghĩa các quy tắc bảo mật:
     * 1. Disable CSRF (vì sử dụng JWT)
     * 2. Thiết lập session stateless
     * 3. Định nghĩa các endpoint public/private
     * 4. Thêm JWT filter vào filter chain
     * 5. Cấu hình CORS
     *
     * @param http - HttpSecurity builder
     * @return SecurityFilterChain - Chain các filter bảo mật
     * @throws Exception - Khi có lỗi cấu hình
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF vì sử dụng JWT (Cross-Site Request Forgery)
                .csrf(AbstractHttpConfigurer::disable)

                // Cấu hình CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Cấu hình UserDetailsService
                .userDetailsService(userDetailsService)

                // Cấu hình session management - STATELESS vì dùng JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Cấu hình exception handling
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)  // Xử lý authentication errors
                        .accessDeniedHandler(accessDeniedHandler)            // Xử lý authorization errors
                )

                // Cấu hình authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Các endpoint public - không cần authentication
                        .requestMatchers(
                                "/auth/**",
                                "/public/**",
                                "/email-verification/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Tất cả các endpoint khác cần authentication
                        .anyRequest().authenticated()
                )
                // Thêm JWT filter trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * Ghi chú: Nếu sau này muốn sử dụng form login để test nhanh, có thể uncomment:
     *
     * .formLogin(form -> form
     *     .loginProcessingUrl("/login")
     *     .usernameParameter("usernameOrEmail")  // Tên field username
     *     .passwordParameter("password")         // Tên field password
     *     .permitAll()
     * )
     *
     * Điều này sẽ tạo ra form login mặc định tại /login
     */
}
