package com.teamfab.mealmatch.service;

import com.teamfab.mealmatch.dto.ReviewRequest;
import com.teamfab.mealmatch.dto.ReviewResponse;
import com.teamfab.mealmatch.entity.Order;
import com.teamfab.mealmatch.entity.Review;
import com.teamfab.mealmatch.entity.User;
import com.teamfab.mealmatch.exception.ResourceNotFoundException;
import com.teamfab.mealmatch.exception.UnauthorizedException;
import com.teamfab.mealmatch.repository.OrderRepository;
import com.teamfab.mealmatch.repository.ReviewRepository;
import com.teamfab.mealmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public ReviewResponse createReview(ReviewRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("Not authorized to review this order");
        }

        Review review = Review.builder()
                .order(order)
                .user(user)
                .provider(order.getProvider())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return toResponse(reviewRepository.save(review));
    }

    public List<ReviewResponse> getReviewsByProvider(UUID providerId) {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getProvider().getId().equals(providerId))
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
                .orderId(r.getOrder().getId())
                .userId(r.getUser().getId())
                .providerId(r.getProvider().getId())
                .providerName(r.getProvider().getName())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
