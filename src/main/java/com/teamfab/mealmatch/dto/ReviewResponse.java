package com.teamfab.mealmatch.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ReviewResponse {
    private UUID id;
    private String userId;
    private String providerId;
    private String providerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
