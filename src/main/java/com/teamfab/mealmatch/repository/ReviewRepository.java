package com.teamfab.mealmatch.repository;

import com.teamfab.mealmatch.entity.Provider;
import com.teamfab.mealmatch.entity.Review;
import com.teamfab.mealmatch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByUser(User user);
    List<Review> findByProvider(Provider provider);
    List<Review> findByProviderId(String providerId);
}
