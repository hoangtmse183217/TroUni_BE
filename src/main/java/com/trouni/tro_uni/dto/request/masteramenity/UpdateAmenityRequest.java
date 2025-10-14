package com.trouni.tro_uni.dto.request.masteramenity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateAmenityRequest {
    String name;
    String icon;
    boolean active;

    public Boolean getActive() {
        return active;
    }
}
