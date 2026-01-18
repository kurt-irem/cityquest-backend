package com.cityquest.cityquest_backend.repository;

import com.cityquest.cityquest_backend.model.AchievementGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AchievementGoalRepository extends JpaRepository<AchievementGoal, Long> {
    Optional<AchievementGoal> findByUserIdAndMonthKey(Long userId, String monthKey);
}
