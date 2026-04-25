package com.teamfab.mealmatch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class OrderRequest {

    @NotNull
    private UUID subscriptionId;

    @NotNull
    private LocalDate scheduledDate;
}
