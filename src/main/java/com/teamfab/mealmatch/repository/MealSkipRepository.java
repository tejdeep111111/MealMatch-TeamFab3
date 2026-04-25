package com.teamfab.mealmatch.repository;

import com.teamfab.mealmatch.entity.MealSkip;
import com.teamfab.mealmatch.entity.Provider;
import com.teamfab.mealmatch.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealSkipRepository extends JpaRepository<MealSkip, String> {

    List<MealSkip> findBySubscription(Subscription subscription);

    Optional<MealSkip> findBySubscriptionAndSkipDate(Subscription subscription, LocalDate skipDate);

    boolean existsBySubscriptionAndSkipDate(Subscription subscription, LocalDate skipDate);

    /** All skips for a given provider — used by the provider dashboard */
    @Query("SELECT ms FROM MealSkip ms WHERE ms.subscription.provider = :provider ORDER BY ms.skipDate DESC")
    List<MealSkip> findByProvider(@Param("provider") Provider provider);

    /** Skips in a date range for a provider — used for the 3-day prep plan */
    @Query("SELECT ms FROM MealSkip ms WHERE ms.subscription.provider = :provider AND ms.skipDate BETWEEN :from AND :to")
    List<MealSkip> findByProviderAndSkipDateBetween(@Param("provider") Provider provider,
                                                    @Param("from") LocalDate from,
                                                    @Param("to") LocalDate to);
}

