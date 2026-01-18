package com.cityquest.cityquest_backend.controller;

import com.cityquest.cityquest_backend.dto.AchievementGoalRequest;
import com.cityquest.cityquest_backend.dto.AchievementSummaryResponse;
import com.cityquest.cityquest_backend.model.AchievementGoal;
import com.cityquest.cityquest_backend.model.User;
import com.cityquest.cityquest_backend.repository.AchievementGoalRepository;
import com.cityquest.cityquest_backend.repository.UserRepository;
import com.cityquest.cityquest_backend.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private static final int DEFAULT_GOAL = 5;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private AchievementGoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(Authentication authentication,
                                        @RequestParam(value = "month", required = false) String monthParam) {
        User user = getUserFromAuth(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        YearMonth ym = parseMonthOrNow(monthParam);
        int year = ym.getYear();
        int month = ym.getMonthValue();
        String monthKey = ym.toString();

        Long uniquePlaces = visitRepository.countDistinctPlacesByUserAndMonth(user.getId(), year, month);
        if (uniquePlaces == null) uniquePlaces = 0L;

        Map<String, Long> categoryCounts = new HashMap<>();
        for (Object[] row : visitRepository.countDistinctPlacesByCategoryAndMonth(user.getId(), year, month)) {
            String cat = (String) row[0];
            Long count = (Long) row[1];
            categoryCounts.put(cat, count);
        }

        int goal = goalRepository.findByUserIdAndMonthKey(user.getId(), monthKey)
                .map(AchievementGoal::getGoal)
                .orElse(DEFAULT_GOAL);

        AchievementSummaryResponse response = AchievementSummaryResponse.builder()
                .month(monthKey)
                .goal(goal)
                .uniquePlaces(uniquePlaces)
                .categories(categoryCounts)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/goal")
    public ResponseEntity<?> upsertGoal(@RequestBody AchievementGoalRequest request,
                                        Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (request.getGoal() == null || request.getGoal() < 1) {
            return ResponseEntity.badRequest().body("goal must be >= 1");
        }

        YearMonth ym = parseMonthOrNow(request.getMonth());
        String monthKey = ym.toString();

        AchievementGoal goal = goalRepository.findByUserIdAndMonthKey(user.getId(), monthKey)
                .orElse(AchievementGoal.builder()
                        .user(user)
                        .monthKey(monthKey)
                        .build());
        goal.setGoal(request.getGoal());
        goalRepository.save(goal);

        return ResponseEntity.noContent().build();
    }

    private YearMonth parseMonthOrNow(String monthParam) {
        if (monthParam == null || monthParam.isBlank()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(monthParam);
        } catch (Exception e) {
            return YearMonth.now();
        }
    }

    private User getUserFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }
}
