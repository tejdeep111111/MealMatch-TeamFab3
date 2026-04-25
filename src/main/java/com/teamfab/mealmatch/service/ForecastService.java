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
            BigDecimal projectedRevenue = item.getPrice()
                    .multiply(BigDecimal.valueOf(activeCount))
                    .multiply(BigDecimal.valueOf(7));

            return new ForecastResponse(item.getId(), item.getName(), activeCount, projectedRevenue);
        }).collect(Collectors.toList());
    }
}
