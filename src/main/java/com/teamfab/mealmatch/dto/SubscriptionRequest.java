package com.teamfab.mealmatch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SubscriptionRequest {

    @NotNull
    private Long mealPlanId;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;
}

