package com.teamfab.mealmatch.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID id;
    private UUID subscriptionId;
    private UUID userId;
    private UUID providerId;
    private String providerName;
    private LocalDate scheduledDate;
    private String status;
    private BigDecimal price;
}
