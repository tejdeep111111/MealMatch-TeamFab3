package com.teamfab.mealmatch.service;

import com.teamfab.mealmatch.dto.SubscriptionRequest;
import com.teamfab.mealmatch.dto.SubscriptionResponse;
import com.teamfab.mealmatch.entity.AppUser;
import com.teamfab.mealmatch.entity.MealPlan;
import com.teamfab.mealmatch.entity.Subscription;
import com.teamfab.mealmatch.enums.SubscriptionStatus;
import com.teamfab.mealmatch.exception.ResourceNotFoundException;
import com.teamfab.mealmatch.exception.UnauthorizedException;
import com.teamfab.mealmatch.repository.MealPlanRepository;
import com.teamfab.mealmatch.repository.SubscriptionRepository;
import com.teamfab.mealmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final MealPlanRepository mealPlanRepository;

    public SubscriptionResponse subscribe(SubscriptionRequest request, String userEmail) {
        AppUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        MealPlan mealPlan = mealPlanRepository.findById(request.getMealPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Meal plan not found"));

        Subscription subscription = Subscription.builder()
                .user(user)
                .mealPlan(mealPlan)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(SubscriptionStatus.ACTIVE)
                .build();

        return toResponse(subscriptionRepository.save(subscription));
    }

    public List<SubscriptionResponse> getMySubscriptions(String userEmail) {
        AppUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return subscriptionRepository.findByUser(user).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public SubscriptionResponse pauseSubscription(Long id, String userEmail) {
        return updateStatus(id, userEmail, SubscriptionStatus.PAUSED);
    }

    public SubscriptionResponse resumeSubscription(Long id, String userEmail) {
        return updateStatus(id, userEmail, SubscriptionStatus.ACTIVE);
    }

    public SubscriptionResponse cancelSubscription(Long id, String userEmail) {
        return updateStatus(id, userEmail, SubscriptionStatus.CANCELLED);
    }

    private SubscriptionResponse updateStatus(Long id, String userEmail, SubscriptionStatus status) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
        if (!subscription.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("Not authorized to modify this subscription");
        }
        subscription.setStatus(status);
        return toResponse(subscriptionRepository.save(subscription));
    }

    private SubscriptionResponse toResponse(Subscription s) {
        return SubscriptionResponse.builder()
                .id(s.getId())
                .userEmail(s.getUser().getEmail())
                .mealPlanName(s.getMealPlan().getName())
                .startDate(s.getStartDate())
                .endDate(s.getEndDate())
                .status(s.getStatus())
                .build();
    }
}

