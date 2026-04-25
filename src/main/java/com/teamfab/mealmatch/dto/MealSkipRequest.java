package com.teamfab.mealmatch.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MealSkipRequest {
    private LocalDate skipDate;
    private String reason;
}

