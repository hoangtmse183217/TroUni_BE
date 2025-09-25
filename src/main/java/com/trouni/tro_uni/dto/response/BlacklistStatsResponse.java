package com.trouni.tro_uni.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BlacklistStatsResponse - DTO cho response thống kê blacklist
 * <p>
 * Chức năng chính:
 * - Trả về thống kê blacklist tokens
 * - Sử dụng trong API blacklist stats
 * - Chứa thông tin về số lượng tokens
 * <p>
 * @author TroUni Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistStatsResponse {
    
    private long totalBlacklisted;
    private long expiredTokens;
    private long activeTokens;
    
    /**
     * Tạo BlacklistStatsResponse từ thống kê
     * <p>
     * @param totalBlacklisted - Tổng số tokens bị blacklist
     * @param expiredTokens - Số tokens đã hết hạn
     * @param activeTokens - Số tokens còn hoạt động
     * @return BlacklistStatsResponse - Response DTO
     */
    public static BlacklistStatsResponse fromStats(long totalBlacklisted, long expiredTokens, long activeTokens) {
        return BlacklistStatsResponse.builder()
                .totalBlacklisted(totalBlacklisted)
                .expiredTokens(expiredTokens)
                .activeTokens(activeTokens)
                .build();
    }
}
