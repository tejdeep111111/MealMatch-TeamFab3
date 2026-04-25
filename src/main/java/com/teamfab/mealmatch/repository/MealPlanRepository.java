package com.teamfab.mealmatch.repository;

import com.teamfab.mealmatch.entity.AppUser;
import com.teamfab.mealmatch.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    List<MealPlan> findByProvider(AppUser provider);
}

