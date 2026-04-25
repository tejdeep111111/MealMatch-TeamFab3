package com.teamfab.mealmatch.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SubscriptionResponse {
    private String id;
    private String userId;
    private String userEmail;
    private String providerId;
    private String providerName;
    private String menuItemId;
    private String menuItemName;
    private String daysOfWeek;
    private String deliveryTime;
    private String deliveryAddress;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
