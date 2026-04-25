package com.teamfab.mealmatch.controller;

import com.teamfab.mealmatch.dto.DishRequest;
import com.teamfab.mealmatch.dto.DishResponse;
import com.teamfab.mealmatch.dto.MealPlanRequest;
import com.teamfab.mealmatch.dto.MealPlanResponse;
import com.teamfab.mealmatch.service.DishService;
import com.teamfab.mealmatch.service.MealPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final DishService dishService;
    private final MealPlanService mealPlanService;

    // Dish endpoints
    @PostMapping("/dishes")
    public ResponseEntity<DishResponse> createDish(@Valid @RequestBody DishRequest request,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dishService.createDish(request, userDetails.getUsername()));
    }

    @GetMapping("/dishes")
    public ResponseEntity<List<DishResponse>> getMyDishes(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dishService.getDishesByProvider(userDetails.getUsername()));
    }

    @PutMapping("/dishes/{id}")
    public ResponseEntity<DishResponse> updateDish(@PathVariable Long id,
                                                   @Valid @RequestBody DishRequest request,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dishService.updateDish(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/dishes/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        dishService.deleteDish(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // Meal plan endpoints
    @PostMapping("/meal-plans")
    public ResponseEntity<MealPlanResponse> createMealPlan(@Valid @RequestBody MealPlanRequest request,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(mealPlanService.createMealPlan(request, userDetails.getUsername()));
    }

    @GetMapping("/meal-plans")
    public ResponseEntity<List<MealPlanResponse>> getMyMealPlans(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(mealPlanService.getMealPlansByProvider(userDetails.getUsername()));
    }

    @PutMapping("/meal-plans/{id}")
    public ResponseEntity<MealPlanResponse> updateMealPlan(@PathVariable Long id,
                                                           @Valid @RequestBody MealPlanRequest request,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(mealPlanService.updateMealPlan(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/meal-plans/{id}")
    public ResponseEntity<Void> deleteMealPlan(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        mealPlanService.deleteMealPlan(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}

