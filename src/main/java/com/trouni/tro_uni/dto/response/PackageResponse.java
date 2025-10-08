package com.trouni.tro_uni.dto.response;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PackageResponse {
    private UUID id;
    private String name;
    private BigDecimal price;
    private int durationDays;
    private String description;
    private List<String> features;
}
