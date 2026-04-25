package com.teamfab.mealmatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ForecastResponse {
    private UUID menuItemId;
    private String menuItemName;
    private Long activeSubscriptions;
    private BigDecimal projectedWeeklyRevenue;
}
