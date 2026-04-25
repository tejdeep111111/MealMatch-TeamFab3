package com.teamfab.mealmatch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SubscriptionRequest {

    @NotNull
    private String providerId;

    @NotNull
    private String menuItemId;

    private String daysOfWeek;

    private String deliveryTime;

    private String deliveryAddress;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;
}
