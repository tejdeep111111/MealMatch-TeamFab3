package com.teamfab.mealmatch.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_skips",
       uniqueConstraints = @UniqueConstraint(columnNames = {"subscription_id", "skip_date"},
                                             name = "uq_skip_per_subscription_date"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealSkip {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @Column(name = "skip_date", nullable = false)
    private LocalDate skipDate;

    @Column(name = "reason", length = 300)
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

