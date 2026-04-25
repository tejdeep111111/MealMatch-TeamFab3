package com.teamfab.mealmatch.repository;

import com.teamfab.mealmatch.entity.MenuItem;
import com.teamfab.mealmatch.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByProvider(Provider provider);
    List<MenuItem> findByIsAvailableTrue();
    List<MenuItem> findByProviderAndIsAvailableTrue(Provider provider);
}
