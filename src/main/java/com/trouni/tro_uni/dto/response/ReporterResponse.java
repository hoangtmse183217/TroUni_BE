package com.trouni.tro_uni.dto.response;

import com.trouni.tro_uni.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * ReporterResponse - DTO cho thông tin người báo cáo (giới hạn thông tin để bảo vệ privacy)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporterResponse {
    
    private UUID id;
    private String username;
    
    public static ReporterResponse fromUser(User user) {
        return ReporterResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
