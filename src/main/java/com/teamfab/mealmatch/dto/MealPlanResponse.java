package com.teamfab.mealmatch.dto;

import com.teamfab.mealmatch.enums.PlanFrequency;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MealPlanResponse {
    private Long id;
    private String name;
    private String description;
    private PlanFrequency frequency;
    private String providerName;
    private Long dishId;
    private String dishName;
}

