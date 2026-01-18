package com.cityquest.cityquest_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "achievement_goals", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "month_key"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "month_key", nullable = false, length = 7)
    private String monthKey; // format YYYY-MM

    @Column(nullable = false)
    @Builder.Default
    private Integer goal = 5;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
