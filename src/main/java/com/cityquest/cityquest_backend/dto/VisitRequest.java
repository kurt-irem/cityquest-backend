package com.cityquest.cityquest_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitRequest {
    private Long placeId;
    private LocalDate visitDate;
    private String note;
    private Integer rating;
    private String image;
    private List<String> tags;
}
