package com.teamfab.mealmatch.service;

import com.teamfab.mealmatch.dto.MealPlanRequest;
import com.teamfab.mealmatch.dto.MealPlanResponse;
import com.teamfab.mealmatch.entity.AppUser;
import com.teamfab.mealmatch.entity.Dish;
import com.teamfab.mealmatch.entity.MealPlan;
import com.teamfab.mealmatch.enums.DietProfile;
import com.teamfab.mealmatch.exception.ResourceNotFoundException;
import com.teamfab.mealmatch.exception.UnauthorizedException;
import com.teamfab.mealmatch.repository.DishRepository;
import com.teamfab.mealmatch.repository.MealPlanRepository;
import com.teamfab.mealmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final UserRepository userRepository;
    private final DishRepository dishRepository;

    public MealPlanResponse createMealPlan(MealPlanRequest request, String providerEmail) {
        AppUser provider = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        Dish dish = dishRepository.findById(request.getDishId())
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found"));

        MealPlan plan = MealPlan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .frequency(request.getFrequency())
                .provider(provider)
                .dish(dish)
                .build();

        return toResponse(mealPlanRepository.save(plan));
    }

    public List<MealPlanResponse> getMealPlansByProvider(String providerEmail) {
        AppUser provider = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        return mealPlanRepository.findByProvider(provider).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<MealPlanResponse> getCompatibleMealPlans(String userEmail) {
        AppUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        DietProfile profile = user.getDietProfile();
        return mealPlanRepository.findAll().stream()
                .filter(plan -> profile == null || plan.getDish().getDietTags() == null
                        || plan.getDish().getDietTags().contains(profile.name()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MealPlanResponse updateMealPlan(Long id, MealPlanRequest request, String providerEmail) {
        MealPlan plan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal plan not found"));
        if (!plan.getProvider().getEmail().equals(providerEmail)) {
            throw new UnauthorizedException("Not authorized to update this meal plan");
        }
        Dish dish = dishRepository.findById(request.getDishId())
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found"));
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setFrequency(request.getFrequency());
        plan.setDish(dish);
        return toResponse(mealPlanRepository.save(plan));
    }

    public void deleteMealPlan(Long id, String providerEmail) {
        MealPlan plan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal plan not found"));
        if (!plan.getProvider().getEmail().equals(providerEmail)) {
            throw new UnauthorizedException("Not authorized to delete this meal plan");
        }
        mealPlanRepository.delete(plan);
    }

    private MealPlanResponse toResponse(MealPlan plan) {
        return MealPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .frequency(plan.getFrequency())
                .providerName(plan.getProvider().getName())
                .dishId(plan.getDish().getId())
                .dishName(plan.getDish().getName())
                .build();
    }
}

