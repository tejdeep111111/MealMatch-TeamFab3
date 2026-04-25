package com.teamfab.mealmatch.repository;

import com.teamfab.mealmatch.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, String> {
    Optional<Provider> findByEmail(String email);
    boolean existsByEmail(String email);
}
