package com.teamfab.mealmatch.controller;

import com.teamfab.mealmatch.dto.MealPlanResponse;
import com.teamfab.mealmatch.service.MealPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class DishController {

    private final MealPlanService mealPlanService;

    @GetMapping("/compatible")
    public ResponseEntity<List<MealPlanResponse>> getCompatibleMeals(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(mealPlanService.getCompatibleMealPlans(userDetails.getUsername()));
    }
}

