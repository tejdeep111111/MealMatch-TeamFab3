package com.teamfab.mealmatch.service;

import com.teamfab.mealmatch.dto.SubscriptionRequest;
import com.teamfab.mealmatch.dto.SubscriptionResponse;
import com.teamfab.mealmatch.entity.MenuItem;
import com.teamfab.mealmatch.entity.Provider;
import com.teamfab.mealmatch.entity.Subscription;
import com.teamfab.mealmatch.entity.User;
import com.teamfab.mealmatch.exception.ResourceNotFoundException;
import com.teamfab.mealmatch.exception.UnauthorizedException;
import com.teamfab.mealmatch.repository.MenuItemRepository;
import com.teamfab.mealmatch.repository.ProviderRepository;
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
    private final ProviderRepository providerRepository;
    private final MenuItemRepository menuItemRepository;

    public SubscriptionResponse subscribe(SubscriptionRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Provider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        Subscription subscription = Subscription.builder()
                .user(user)
                .provider(provider)
                .menuItem(menuItem)
                .daysOfWeek(request.getDaysOfWeek())
                .deliveryTime(request.getDeliveryTime())
                .deliveryAddress(request.getDeliveryAddress())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status("ACTIVE")
                .build();

        return toResponse(subscriptionRepository.save(subscription));
    }

    public List<SubscriptionResponse> getMySubscriptions(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return subscriptionRepository.findByUser(user).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<SubscriptionResponse> getProviderSubscriptions(String providerEmail) {
        Provider provider = providerRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        return subscriptionRepository.findByProvider(provider).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public SubscriptionResponse pauseSubscription(String id, String userEmail) {
        return updateStatus(id, userEmail, "PAUSED");
    }

    public SubscriptionResponse resumeSubscription(String id, String userEmail) {
        return updateStatus(id, userEmail, "ACTIVE");
    }

    public SubscriptionResponse cancelSubscription(String id, String userEmail) {
        return updateStatus(id, userEmail, "CANCELLED");
    }

    private SubscriptionResponse updateStatus(String id, String userEmail, String status) {
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
                .userId(s.getUser().getId())
                .userEmail(s.getUser().getEmail())
                .providerId(s.getProvider().getId())
                .providerName(s.getProvider().getName())
                .menuItemId(s.getMenuItem().getId())
                .menuItemName(s.getMenuItem().getName())
                .daysOfWeek(s.getDaysOfWeek())
                .deliveryTime(s.getDeliveryTime())
                .deliveryAddress(s.getDeliveryAddress())
                .status(s.getStatus())
                .startDate(s.getStartDate())
                .endDate(s.getEndDate())
                .build();
    }
}
