package com.teamfab.mealmatch.service;

import com.teamfab.mealmatch.dto.MenuItemRequest;
import com.teamfab.mealmatch.dto.MenuItemResponse;
import com.teamfab.mealmatch.entity.MenuItem;
import com.teamfab.mealmatch.entity.Provider;
import com.teamfab.mealmatch.exception.ResourceNotFoundException;
import com.teamfab.mealmatch.exception.UnauthorizedException;
import com.teamfab.mealmatch.repository.MenuItemRepository;
import com.teamfab.mealmatch.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final ProviderRepository providerRepository;

    public MenuItemResponse createMenuItem(MenuItemRequest request, String providerEmail) {
        Provider provider = providerRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        MenuItem item = MenuItem.builder()
                .provider(provider)
                .name(request.getName())
                .mealType(request.getMealType())
                .dietaryTags(request.getDietaryTags())
                .price(request.getPrice())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .build();

        return toResponse(menuItemRepository.save(item));
    }

    public List<MenuItemResponse> getMenuItemsByProvider(String providerEmail) {
        Provider provider = providerRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        return menuItemRepository.findByProvider(provider).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<MenuItemResponse> getAllAvailableMenuItems() {
        return menuItemRepository.findByIsAvailableTrue().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<MenuItemResponse> getCompatibleMenuItems(String userDietaryTags) {
        return menuItemRepository.findByIsAvailableTrue().stream()
                .filter(item -> {
                    if (userDietaryTags == null || userDietaryTags.isBlank()) return true;
                    if (item.getDietaryTags() == null) return false;
                    for (String tag : userDietaryTags.split(",")) {
                        if (item.getDietaryTags().contains(tag.trim())) return true;
                    }
                    return false;
                })
                .map(this::toResponse).collect(Collectors.toList());
    }

    public MenuItemResponse updateMenuItem(UUID id, MenuItemRequest request, String providerEmail) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
        if (!item.getProvider().getEmail().equals(providerEmail)) {
            throw new UnauthorizedException("Not authorized to update this menu item");
        }
        item.setName(request.getName());
        item.setMealType(request.getMealType());
        item.setDietaryTags(request.getDietaryTags());
        item.setPrice(request.getPrice());
        if (request.getIsAvailable() != null) {
            item.setIsAvailable(request.getIsAvailable());
        }
        return toResponse(menuItemRepository.save(item));
    }

    public void deleteMenuItem(UUID id, String providerEmail) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
        if (!item.getProvider().getEmail().equals(providerEmail)) {
            throw new UnauthorizedException("Not authorized to delete this menu item");
        }
        menuItemRepository.delete(item);
    }

    private MenuItemResponse toResponse(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .providerId(item.getProvider().getId())
                .providerName(item.getProvider().getName())
                .name(item.getName())
                .mealType(item.getMealType())
                .dietaryTags(item.getDietaryTags())
                .price(item.getPrice())
                .isAvailable(item.getIsAvailable())
                .build();
    }
}
