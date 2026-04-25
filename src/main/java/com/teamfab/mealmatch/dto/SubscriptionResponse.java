package com.teamfab.mealmatch.dto;

import com.teamfab.mealmatch.enums.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SubscriptionResponse {
    private Long id;
    private String userEmail;
    private String mealPlanName;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;
}

