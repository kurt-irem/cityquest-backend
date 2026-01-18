package com.cityquest.cityquest_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AchievementSummaryResponse {
    private String month; // YYYY-MM
    private Integer goal;
    private Long uniquePlaces;
    private Map<String, Long> categories;
}
