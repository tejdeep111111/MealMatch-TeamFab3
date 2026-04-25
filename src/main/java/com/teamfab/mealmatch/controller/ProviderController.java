package com.teamfab.mealmatch.controller;

import com.teamfab.mealmatch.dto.MenuItemRequest;
import com.teamfab.mealmatch.dto.MenuItemResponse;
import com.teamfab.mealmatch.dto.ProviderResponse;
import com.teamfab.mealmatch.service.MenuItemService;
import com.teamfab.mealmatch.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final MenuItemService menuItemService;
    private final ProviderService providerService;

    @GetMapping("/me")
    public ResponseEntity<ProviderResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(providerService.getProviderByEmail(userDetails.getUsername()));
    }

    @PostMapping("/menu-items")
    public ResponseEntity<MenuItemResponse> createMenuItem(@Valid @RequestBody MenuItemRequest request,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(menuItemService.createMenuItem(request, userDetails.getUsername()));
    }

    @GetMapping("/menu-items")
    public ResponseEntity<List<MenuItemResponse>> getMyMenuItems(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(menuItemService.getMenuItemsByProvider(userDetails.getUsername()));
    }

    @PutMapping("/menu-items/{id}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(@PathVariable UUID id,
                                                           @Valid @RequestBody MenuItemRequest request,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(menuItemService.updateMenuItem(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/menu-items/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable UUID id,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        menuItemService.deleteMenuItem(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
