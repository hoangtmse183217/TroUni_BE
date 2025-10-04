package com.trouni.tro_uni.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "TroUni API",
                version = "1.0.0",
                description = "API Documentation for TroUni Application"
        ),
        // Áp dụng yêu cầu bảo mật này cho TẤT CẢ các endpoint
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth", // Tên của security scheme, phải khớp với tên trong @SecurityRequirement
        type = SecuritySchemeType.HTTP, // Loại bảo mật là HTTP
        scheme = "bearer", // Scheme là "bearer" cho JWT
        bearerFormat = "JWT", // Định dạng của token
        in = SecuritySchemeIn.HEADER, // Token được gửi trong Header
        description = "JWT Authorization header using the Bearer scheme. Enter 'Bearer' [space] and then your token."
)
public class OpenApiConfig {
}