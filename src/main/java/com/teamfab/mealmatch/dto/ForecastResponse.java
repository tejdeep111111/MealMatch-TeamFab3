package com.teamfab.mealmatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ForecastResponse {
    private String menuItemId;
    private String menuItemName;
    private Long activeSubscriptions;
    private BigDecimal projectedWeeklyRevenue;
}
