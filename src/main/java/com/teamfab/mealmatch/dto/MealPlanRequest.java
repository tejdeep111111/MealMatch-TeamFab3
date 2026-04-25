package com.teamfab.mealmatch.dto;

import com.teamfab.mealmatch.enums.PlanFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MealPlanRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private PlanFrequency frequency;

    @NotNull
    private Long dishId;
}

