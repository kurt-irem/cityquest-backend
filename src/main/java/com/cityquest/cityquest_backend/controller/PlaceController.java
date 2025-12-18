package com.cityquest.cityquest_backend.controller;

import com.cityquest.cityquest_backend.dto.PlaceRequest;
import com.cityquest.cityquest_backend.dto.PlaceResponse;
import com.cityquest.cityquest_backend.model.Place;
import com.cityquest.cityquest_backend.model.User;
import com.cityquest.cityquest_backend.repository.PlaceRepository;
import com.cityquest.cityquest_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserRepository userRepository;

    // GET all places (PUBLIC - visible to everyone)
    @GetMapping
    public ResponseEntity<List<PlaceResponse>> getAllPlaces() {
        List<PlaceResponse> places = placeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(places);
    }

    // GET all places created by current user
    @GetMapping("/my-places")
    public ResponseEntity<List<PlaceResponse>> getMyPlaces(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<PlaceResponse> places = placeRepository.findByCreatedBy(user).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(places);
    }

    // GET places by category (PUBLIC)
    @GetMapping("/category/{category}")
    public ResponseEntity<List<PlaceResponse>> getPlacesByCategory(@PathVariable String category) {
        List<PlaceResponse> places = placeRepository.findByCategory(category).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(places);
    }

    // GET places by search (PUBLIC)
    @GetMapping("/search")
    public ResponseEntity<List<PlaceResponse>> searchPlaces(@RequestParam String name) {
        List<PlaceResponse> places = placeRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(places);
    }

    // GET single place (PUBLIC)
    @GetMapping("/{id}")
    public ResponseEntity<PlaceResponse> getPlaceById(@PathVariable Long id) {
        return placeRepository.findById(id)
                .map(place -> ResponseEntity.ok(toResponse(place)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST create new place (AUTHENTICATED)
    @PostMapping
    public ResponseEntity<PlaceResponse> createPlace(
            @RequestBody PlaceRequest request,
            Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Place place = Place.builder()
                .name(request.getName())
                .address(request.getAddress())
                .category(request.getCategory())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .googlePlaceId(request.getGooglePlaceId())
                .googleMapsUrl(request.getGoogleMapsUrl())
                .createdBy(user)
                .build();

        Place saved = placeRepository.save(place);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    // PUT update place (only creator can update)
    @PutMapping("/{id}")
    public ResponseEntity<PlaceResponse> updatePlace(
            @PathVariable Long id,
            @RequestBody PlaceRequest request,
            Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return placeRepository.findByIdAndCreatedBy(id, user)
                .map(place -> {
                    place.setName(request.getName());
                    place.setAddress(request.getAddress());
                    place.setCategory(request.getCategory());
                    place.setLatitude(request.getLatitude());
                    place.setLongitude(request.getLongitude());
                    place.setGooglePlaceId(request.getGooglePlaceId());
                    place.setGoogleMapsUrl(request.getGoogleMapsUrl());
                    Place updated = placeRepository.save(place);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    // DELETE place (only creator can delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(
            @PathVariable Long id,
            Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return placeRepository.findByIdAndCreatedBy(id, user)
                .map(place -> {
                    placeRepository.delete(place);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    // Helper methods
    private User getUserFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    private PlaceResponse toResponse(Place place) {
        return PlaceResponse.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .category(place.getCategory())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .googlePlaceId(place.getGooglePlaceId())
                .googleMapsUrl(place.getGoogleMapsUrl())
                .createdByUsername(place.getCreatedBy().getUsername())
                .createdByUserId(place.getCreatedBy().getId())
                .createdAt(place.getCreatedAt())
                .updatedAt(place.getUpdatedAt())
                .build();
    }
}
