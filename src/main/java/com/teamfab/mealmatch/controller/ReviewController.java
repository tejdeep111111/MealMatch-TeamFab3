package com.teamfab.mealmatch.controller;

import com.teamfab.mealmatch.dto.ReviewRequest;
import com.teamfab.mealmatch.dto.ReviewResponse;
import com.teamfab.mealmatch.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reviewService.createReview(request, userDetails.getUsername()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reviewService.getMyReviews(userDetails.getUsername()));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<ReviewResponse>> getProviderReviews(@PathVariable String providerId) {
        return ResponseEntity.ok(reviewService.getReviewsByProvider(providerId));
    }
}
