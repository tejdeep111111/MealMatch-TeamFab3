package com.teamfab.mealmatch.controller;

import com.teamfab.mealmatch.dto.SubscriptionRequest;
import com.teamfab.mealmatch.dto.SubscriptionResponse;
import com.teamfab.mealmatch.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> subscribe(@Valid @RequestBody SubscriptionRequest request,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subscriptionService.subscribe(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> getMySubscriptions(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subscriptionService.getMySubscriptions(userDetails.getUsername()));
    }

    @PatchMapping("/{id}/pause")
    public ResponseEntity<SubscriptionResponse> pause(@PathVariable UUID id,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subscriptionService.pauseSubscription(id, userDetails.getUsername()));
    }

    @PatchMapping("/{id}/resume")
    public ResponseEntity<SubscriptionResponse> resume(@PathVariable UUID id,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subscriptionService.resumeSubscription(id, userDetails.getUsername()));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SubscriptionResponse> cancel(@PathVariable UUID id,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(id, userDetails.getUsername()));
    }
}
