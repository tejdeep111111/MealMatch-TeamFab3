package com.teamfab.mealmatch.controller;

import com.teamfab.mealmatch.dto.MealSkipRequest;
import com.teamfab.mealmatch.dto.MealSkipResponse;
import com.teamfab.mealmatch.dto.SubscriptionRequest;
import com.teamfab.mealmatch.dto.SubscriptionResponse;
import com.teamfab.mealmatch.service.MealSkipService;
import com.teamfab.mealmatch.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final MealSkipService mealSkipService;

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
    public ResponseEntity<SubscriptionResponse> pause(@PathVariable String id,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subscriptionService.pauseSubscription(id, userDetails.getUsername()));
    }

    @PatchMapping("/{id}/resume")
    public ResponseEntity<SubscriptionResponse> resume(@PathVariable String id,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subscriptionService.resumeSubscription(id, userDetails.getUsername()));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SubscriptionResponse> cancel(@PathVariable String id,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(id, userDetails.getUsername()));
    }

    /** Android app: skip one delivery date without affecting the overall subscription */
    @PostMapping("/{id}/skip")
    public ResponseEntity<MealSkipResponse> skipMeal(@PathVariable String id,
                                                     @RequestBody MealSkipRequest request,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(mealSkipService.skipMeal(id, request, userDetails.getUsername()));
    }

    /** Android app: un-skip a previously skipped date */
    @DeleteMapping("/skips/{skipId}")
    public ResponseEntity<Void> cancelSkip(@PathVariable String skipId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        mealSkipService.cancelSkip(skipId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    /** Android app: list all skips for one of my subscriptions */
    @GetMapping("/{id}/skips")
    public ResponseEntity<List<MealSkipResponse>> getSkips(@PathVariable String id,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(mealSkipService.getSkipsForSubscription(id, userDetails.getUsername()));
    }
}
