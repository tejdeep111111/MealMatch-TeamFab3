package com.teamfab.mealmatch.service;

import com.teamfab.mealmatch.dto.DishRequest;
import com.teamfab.mealmatch.dto.DishResponse;
import com.teamfab.mealmatch.entity.AppUser;
import com.teamfab.mealmatch.entity.Dish;
import com.teamfab.mealmatch.exception.ResourceNotFoundException;
import com.teamfab.mealmatch.exception.UnauthorizedException;
import com.teamfab.mealmatch.repository.DishRepository;
import com.teamfab.mealmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DishService {

    private final DishRepository dishRepository;
    private final UserRepository userRepository;

    public DishResponse createDish(DishRequest request, String providerEmail) {
        AppUser provider = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        Dish dish = Dish.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .dietTags(request.getDietTags())
                .provider(provider)
                .build();

        return toResponse(dishRepository.save(dish));
    }

    public List<DishResponse> getAllDishes() {
        return dishRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<DishResponse> getDishesByProvider(String providerEmail) {
        AppUser provider = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        return dishRepository.findByProvider(provider).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public DishResponse updateDish(Long id, DishRequest request, String providerEmail) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found"));
        if (!dish.getProvider().getEmail().equals(providerEmail)) {
            throw new UnauthorizedException("Not authorized to update this dish");
        }
        dish.setName(request.getName());
        dish.setDescription(request.getDescription());
        dish.setPrice(request.getPrice());
        dish.setDietTags(request.getDietTags());
        return toResponse(dishRepository.save(dish));
    }

    public void deleteDish(Long id, String providerEmail) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found"));
        if (!dish.getProvider().getEmail().equals(providerEmail)) {
            throw new UnauthorizedException("Not authorized to delete this dish");
        }
        dishRepository.delete(dish);
    }

    private DishResponse toResponse(Dish dish) {
        return DishResponse.builder()
                .id(dish.getId())
                .name(dish.getName())
                .description(dish.getDescription())
                .price(dish.getPrice())
                .dietTags(dish.getDietTags())
                .providerName(dish.getProvider().getName())
                .build();
    }
}

