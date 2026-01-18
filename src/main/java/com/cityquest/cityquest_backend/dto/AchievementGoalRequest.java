package com.cityquest.cityquest_backend.dto;

import lombok.Data;

@Data
public class AchievementGoalRequest {
    private Integer goal;
    private String month; // optional, defaults to current YYYY-MM
}
