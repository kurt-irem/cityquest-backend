package com.cityquest.cityquest_backend.dto;

import java.time.LocalDate;
import java.util.List;

public class CollectionResponse {
    private Long id;
    private String title;
    private String description;
    private String theme;
    private String color;
    private LocalDate createdAt;
    private String createdByUsername;
    private Long createdByUserId;
    private List<PlaceResponse> places;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public Long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Long createdByUserId) { this.createdByUserId = createdByUserId; }

    public List<PlaceResponse> getPlaces() { return places; }
    public void setPlaces(List<PlaceResponse> places) { this.places = places; }
}
