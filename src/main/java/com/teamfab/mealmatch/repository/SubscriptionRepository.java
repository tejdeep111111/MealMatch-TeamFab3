package com.teamfab.mealmatch.repository;

import com.teamfab.mealmatch.entity.AppUser;
import com.teamfab.mealmatch.entity.MealPlan;
import com.teamfab.mealmatch.entity.Subscription;
import com.teamfab.mealmatch.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUser(AppUser user);
    List<Subscription> findByMealPlan(MealPlan mealPlan);
    List<Subscription> findByStatus(SubscriptionStatus status);
}

