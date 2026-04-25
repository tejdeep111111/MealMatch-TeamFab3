package com.teamfab.mealmatch.repository;

import com.teamfab.mealmatch.entity.Order;
import com.teamfab.mealmatch.entity.Provider;
import com.teamfab.mealmatch.entity.Subscription;
import com.teamfab.mealmatch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUser(User user);
    List<Order> findByProvider(Provider provider);
    List<Order> findBySubscription(Subscription subscription);
    List<Order> findByProviderEmail(String email);
}
