package com.teamfab.mealmatch.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MealSkipResponse {
    private String id;
    private String subscriptionId;
    private String userEmail;
    private String menuItemName;
    private String deliveryTime;
    private String daysOfWeek;
    private LocalDate skipDate;
    private String reason;
    private LocalDateTime createdAt;
}

