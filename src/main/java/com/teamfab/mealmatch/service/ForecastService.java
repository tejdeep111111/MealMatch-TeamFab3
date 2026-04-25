package com.teamfab.mealmatch.service;

import com.teamfab.mealmatch.dto.ForecastResponse;
import com.teamfab.mealmatch.entity.MenuItem;
import com.teamfab.mealmatch.entity.Provider;
import com.teamfab.mealmatch.entity.Subscription;
import com.teamfab.mealmatch.exception.ResourceNotFoundException;
import com.teamfab.mealmatch.repository.MenuItemRepository;
import com.teamfab.mealmatch.repository.ProviderRepository;
import com.teamfab.mealmatch.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForecastService {

    private final MenuItemRepository menuItemRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ProviderRepository providerRepository;

    public List<ForecastResponse> getForecast(String providerEmail) {
        Provider provider = providerRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        List<MenuItem> items = menuItemRepository.findByProvider(provider);

        return items.stream().map(item -> {
            List<Subscription> activeSubs = subscriptionRepository.findByProviderAndStatus(provider, "ACTIVE")
                    .stream()
                    .filter(s -> s.getMenuItem().getId().equals(item.getId()))
                    .collect(Collectors.toList());

            long activeCount = activeSubs.size();
            
            // Calculate total deliveries per week across all subscriptions
            long totalDeliveriesPerWeek = activeSubs.stream()
                    .mapToLong(this::countDeliveryDaysPerWeek)
                    .sum();
            
            BigDecimal projectedRevenue = item.getPrice()
                    .multiply(BigDecimal.valueOf(totalDeliveriesPerWeek));

            return new ForecastResponse(item.getId(), item.getName(), activeCount, projectedRevenue);
        }).collect(Collectors.toList());
    }
    
    /**
     * Count the number of delivery days per week for a subscription.
     * If daysOfWeek is null or empty, assume daily delivery (7 days).
     */
    private long countDeliveryDaysPerWeek(Subscription subscription) {
        String daysOfWeek = subscription.getDaysOfWeek();
        if (daysOfWeek == null || daysOfWeek.trim().isEmpty()) {
            return 7; // Default to daily if not specified
        }
        // Count comma-separated days (e.g., "MON,WED,FRI" = 3 days)
        return daysOfWeek.split(",").length;
    }
}
