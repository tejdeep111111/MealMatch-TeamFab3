package com.teamfab.mealmatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ForecastResponse {
    private String mealPlanName;
    private Long activeSubscriptions;
    private Double projectedRevenue;
}

