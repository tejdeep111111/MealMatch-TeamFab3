package com.teamfab.mealmatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemRequest {

    @NotBlank
    private String name;

    private String mealType;

    private String dietaryTags;

    @NotNull
    private BigDecimal price;

    private Boolean isAvailable = true;
}
