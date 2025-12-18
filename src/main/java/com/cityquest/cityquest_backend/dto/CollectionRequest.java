package com.cityquest.cityquest_backend.dto;

import java.util.List;

public class CollectionRequest {
    private String title;
    private String description;
    private String theme;
    private String color;
    private List<Long> placeIds; // optional: place IDs to initially add to the collection

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public List<Long> getPlaceIds() { return placeIds; }
    public void setPlaceIds(List<Long> placeIds) { this.placeIds = placeIds; }
}
