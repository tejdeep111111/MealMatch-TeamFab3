package com.teamfab.mealmatch.service;

import com.teamfab.mealmatch.dto.ReviewRequest;
import com.teamfab.mealmatch.dto.ReviewResponse;
import com.teamfab.mealmatch.entity.Provider;
import com.teamfab.mealmatch.entity.Review;
import com.teamfab.mealmatch.entity.User;
import com.teamfab.mealmatch.exception.ResourceNotFoundException;
import com.teamfab.mealmatch.repository.ProviderRepository;
import com.teamfab.mealmatch.repository.ReviewRepository;
import com.teamfab.mealmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;

    public ReviewResponse createReview(ReviewRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Provider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        Review review = Review.builder()
                .user(user)
                .provider(provider)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return toResponse(reviewRepository.save(review));
    }

    public List<ReviewResponse> getReviewsByProvider(String providerId) {
        return reviewRepository.findByProviderId(providerId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<ReviewResponse> getMyReviews(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return reviewRepository.findByUser(user).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    private ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .providerId(r.getProvider().getId())
                .providerName(r.getProvider().getName())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
