package com.teamfab.mealmatch.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class SubscriptionResponse {
    private UUID id;
    private UUID userId;
    private String userEmail;
    private UUID providerId;
    private String providerName;
    private UUID menuItemId;
    private String menuItemName;
    private String daysOfWeek;
    private String deliveryTime;
    private String deliveryAddress;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
