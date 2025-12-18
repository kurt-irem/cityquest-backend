package com.cityquest.cityquest_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceRequest {
    private String name;
    private String address;
    private String category;
    private Double latitude;
    private Double longitude;
    private String googlePlaceId;
    private String googleMapsUrl;
}
