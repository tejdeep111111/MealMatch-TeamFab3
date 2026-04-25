package com.teamfab.mealmatch.service;

import com.teamfab.mealmatch.dto.ForecastResponse;
import com.teamfab.mealmatch.entity.AppUser;
import com.teamfab.mealmatch.entity.MealPlan;
import com.teamfab.mealmatch.entity.Subscription;
import com.teamfab.mealmatch.enums.SubscriptionStatus;
import com.teamfab.mealmatch.exception.ResourceNotFoundException;
import com.teamfab.mealmatch.repository.MealPlanRepository;
import com.teamfab.mealmatch.repository.SubscriptionRepository;
import com.teamfab.mealmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForecastService {

    private final MealPlanRepository mealPlanRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public List<ForecastResponse> getForecast(String providerEmail) {
        AppUser provider = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        List<MealPlan> plans = mealPlanRepository.findByProvider(provider);

        return plans.stream().map(plan -> {
            List<Subscription> activeSubs = subscriptionRepository.findByMealPlan(plan).stream()
                    .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                    .collect(Collectors.toList());

            long activeCount = activeSubs.size();
            double projectedRevenue = activeCount * plan.getDish().getPrice() * 7; // 7-day forecast

            return new ForecastResponse(plan.getName(), activeCount, projectedRevenue);
        }).collect(Collectors.toList());
    }
}

