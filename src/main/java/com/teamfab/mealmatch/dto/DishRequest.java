package com.teamfab.mealmatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DishRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Double price;

    private String dietTags;
}

