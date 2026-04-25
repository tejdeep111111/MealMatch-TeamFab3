package com.teamfab.mealmatch.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MenuItemResponse {
    private String id;
    private String providerId;
    private String providerName;
    private String name;
    private String mealType;
    private String dietaryTags;
    private BigDecimal price;
    private Boolean isAvailable;
}
