package com.teamfab.mealmatch.controller;

import com.teamfab.mealmatch.dto.MenuItemResponse;
import com.teamfab.mealmatch.entity.User;
import com.teamfab.mealmatch.repository.UserRepository;
import com.teamfab.mealmatch.service.MenuItemService;
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

    private final MenuItemService menuItemService;
    private final UserRepository userRepository;

    @GetMapping("/compatible")
    public ResponseEntity<List<MenuItemResponse>> getCompatibleMeals(
            @AuthenticationPrincipal UserDetails userDetails) {
        String tags = null;
        if (userDetails != null) {
            tags = userRepository.findByEmail(userDetails.getUsername())
                    .map(User::getDietaryTags).orElse(null);
        }
        return ResponseEntity.ok(menuItemService.getCompatibleMenuItems(tags));
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemService.getAllAvailableMenuItems());
    }
}
