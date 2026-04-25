package com.teamfab.mealmatch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DishResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String dietTags;
    private String providerName;
}

