package com.teamfab.mealmatch.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class MenuItemResponse {
    private UUID id;
    private UUID providerId;
    private String providerName;
    private String name;
    private String mealType;
    private String dietaryTags;
    private BigDecimal price;
    private Boolean isAvailable;
}
