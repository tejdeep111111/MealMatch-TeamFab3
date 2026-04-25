package com.teamfab.mealmatch.repository;

import com.teamfab.mealmatch.entity.Provider;
import com.teamfab.mealmatch.entity.Subscription;
import com.teamfab.mealmatch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    List<Subscription> findByUser(User user);
    List<Subscription> findByProvider(Provider provider);
    List<Subscription> findByStatus(String status);
    List<Subscription> findByProviderAndStatus(Provider provider, String status);
}
