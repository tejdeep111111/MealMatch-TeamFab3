package com.teamfab.mealmatch.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private String id;
    private String userId;
    private String providerId;
    private String providerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
