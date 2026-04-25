package com.teamfab.mealmatch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class SubscriptionRequest {

    @NotNull
    private UUID providerId;

    @NotNull
    private UUID menuItemId;

    private String daysOfWeek;

    private String deliveryTime;

    private String deliveryAddress;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;
}
