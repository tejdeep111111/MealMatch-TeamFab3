package com.teamfab.mealmatch.service;

import com.teamfab.mealmatch.dto.MealSkipRequest;
import com.teamfab.mealmatch.dto.MealSkipResponse;
import com.teamfab.mealmatch.entity.MealSkip;
import com.teamfab.mealmatch.entity.Provider;
import com.teamfab.mealmatch.entity.Subscription;
import com.teamfab.mealmatch.exception.ResourceNotFoundException;
import com.teamfab.mealmatch.exception.UnauthorizedException;
import com.teamfab.mealmatch.repository.MealSkipRepository;
import com.teamfab.mealmatch.repository.ProviderRepository;
import com.teamfab.mealmatch.repository.SubscriptionRepository;
import com.teamfab.mealmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealSkipService {

    private final MealSkipRepository mealSkipRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;

    /**
     * Called by the Android app: skip one delivery date for an ACTIVE subscription.
     * A skip is valid only if skipDate falls within the subscription's daysOfWeek schedule.
     */
    public MealSkipResponse skipMeal(String subscriptionId, MealSkipRequest request, String userEmail) {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        if (!subscription.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("Not authorized to skip this subscription");
        }

        if (!"ACTIVE".equals(subscription.getStatus())) {
            throw new IllegalStateException("Only ACTIVE subscriptions can be skipped");
        }

        LocalDate skipDate = request.getSkipDate();
        if (skipDate == null) {
            throw new IllegalArgumentException("skipDate is required");
        }

        if (skipDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot skip a past date");
        }

        if (mealSkipRepository.existsBySubscriptionAndSkipDate(subscription, skipDate)) {
            throw new IllegalStateException("This date is already skipped for this subscription");
        }

        MealSkip skip = MealSkip.builder()
                .subscription(subscription)
                .skipDate(skipDate)
                .reason(request.getReason())
                .build();

        return toResponse(mealSkipRepository.save(skip));
    }

    /**
     * Cancel a previously registered skip (un-skip).
     */
    public void cancelSkip(String skipId, String userEmail) {
        MealSkip skip = mealSkipRepository.findById(skipId)
                .orElseThrow(() -> new ResourceNotFoundException("Skip not found"));

        if (!skip.getSubscription().getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("Not authorized to remove this skip");
        }

        mealSkipRepository.delete(skip);
    }

    /**
     * Get all skips for a specific subscription (user-facing).
     */
    public List<MealSkipResponse> getSkipsForSubscription(String subscriptionId, String userEmail) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        if (!subscription.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("Not authorized");
        }

        return mealSkipRepository.findBySubscription(subscription).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all skips across all of the provider's subscriptions (provider dashboard).
     */
    public List<MealSkipResponse> getSkipsForProvider(String providerEmail) {
        Provider provider = providerRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        return mealSkipRepository.findByProvider(provider).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get skips for the provider in a date window (used by the prep-plan endpoint).
     */
    public List<MealSkipResponse> getSkipsForProviderInRange(String providerEmail, LocalDate from, LocalDate to) {
        Provider provider = providerRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        return mealSkipRepository.findByProviderAndSkipDateBetween(provider, from, to).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MealSkipResponse toResponse(MealSkip ms) {
        Subscription s = ms.getSubscription();
        return MealSkipResponse.builder()
                .id(ms.getId())
                .subscriptionId(s.getId())
                .userEmail(s.getUser().getEmail())
                .menuItemName(s.getMenuItem().getName())
                .deliveryTime(s.getDeliveryTime())
                .daysOfWeek(s.getDaysOfWeek())
                .skipDate(ms.getSkipDate())
                .reason(ms.getReason())
                .createdAt(ms.getCreatedAt())
                .build();
    }
}

