package com.teamfab.mealmatch.repository;

import com.teamfab.mealmatch.entity.AppUser;
import com.teamfab.mealmatch.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findByProvider(AppUser provider);
}

