package com.teamfab.mealmatch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProviderResponse {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String location;
    private String cuisineType;
    private Double rating;
    private Boolean isActive;
}
