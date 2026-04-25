package com.teamfab.mealmatch.controller;

import com.teamfab.mealmatch.dto.ProviderResponse;
import com.teamfab.mealmatch.entity.User;
import com.teamfab.mealmatch.exception.ResourceNotFoundException;
import com.teamfab.mealmatch.repository.UserRepository;
import com.teamfab.mealmatch.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final ProviderService providerService;

    /**
     * Update the authenticated user's dietary tags.
     * Body: { "dietaryTags": "DIABETIC_CONTROL,LOW_GI" }
     */
    @PutMapping("/dietary-tags")
    public ResponseEntity<Map<String, String>> updateDietaryTags(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setDietaryTags(body.get("dietaryTags"));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("dietaryTags", user.getDietaryTags() != null ? user.getDietaryTags() : ""));
    }

    /**
     * Get the authenticated user's dietary tags.
     */
    @GetMapping("/dietary-tags")
    public ResponseEntity<Map<String, String>> getDietaryTags(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseEntity.ok(Map.of("dietaryTags", user.getDietaryTags() != null ? user.getDietaryTags() : ""));
    }

    /**
     * List all active providers (public-ish, but behind auth for app users).
     */
    @GetMapping("/providers")
    public ResponseEntity<List<ProviderResponse>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllActiveProviders());
    }

    /**
     * Get a single provider by ID.
     */
    @GetMapping("/providers/{id}")
    public ResponseEntity<ProviderResponse> getProvider(@PathVariable UUID id) {
        List<ProviderResponse> all = providerService.getAllActiveProviders();
        ProviderResponse provider = all.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        return ResponseEntity.ok(provider);
    }
}

