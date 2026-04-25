package com.teamfab.mealmatch.service;

import com.teamfab.mealmatch.dto.ProviderResponse;
import com.teamfab.mealmatch.entity.Provider;
import com.teamfab.mealmatch.exception.ResourceNotFoundException;
import com.teamfab.mealmatch.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderRepository providerRepository;

    public List<ProviderResponse> getAllActiveProviders() {
        return providerRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProviderResponse getProviderByEmail(String email) {
        Provider provider = providerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        return toResponse(provider);
    }

    private ProviderResponse toResponse(Provider p) {
        return ProviderResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .email(p.getEmail())
                .phone(p.getPhone())
                .location(p.getLocation())
                .cuisineType(p.getCuisineType())
                .rating(p.getRating())
                .isActive(p.getIsActive())
                .build();
    }
}
