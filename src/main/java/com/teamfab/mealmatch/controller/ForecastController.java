package com.teamfab.mealmatch.controller;

import com.teamfab.mealmatch.dto.ForecastResponse;
import com.teamfab.mealmatch.service.ForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;

    @GetMapping
    public ResponseEntity<List<ForecastResponse>> getForecast(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(forecastService.getForecast(userDetails.getUsername()));
    }
}

