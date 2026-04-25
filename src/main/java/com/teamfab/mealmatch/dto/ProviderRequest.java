package com.teamfab.mealmatch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProviderRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    private String phone;

    private String location;

    private String cuisineType;
}
