package com.cityquest.cityquest_backend.controller;

import com.cityquest.cityquest_backend.dto.VisitRequest;
import com.cityquest.cityquest_backend.dto.VisitResponse;
import com.cityquest.cityquest_backend.model.Place;
import com.cityquest.cityquest_backend.model.User;
import com.cityquest.cityquest_backend.model.Visit;
import com.cityquest.cityquest_backend.repository.PlaceRepository;
import com.cityquest.cityquest_backend.repository.UserRepository;
import com.cityquest.cityquest_backend.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserRepository userRepository;

    // AUTH: User's own visits
    @GetMapping("/my-visits")
    public ResponseEntity<List<VisitResponse>> getMyVisits(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<VisitResponse> visits = visitRepository.findByUser(user).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(visits);
    }

    // PUBLIC: All visits for a place
    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<VisitResponse>> getVisitsByPlace(@PathVariable Long placeId) {
        List<VisitResponse> visits = visitRepository.findByPlaceId(placeId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(visits);
    }

    // PUBLIC: Single visit by id
    @GetMapping("/{id}")
    public ResponseEntity<VisitResponse> getVisitById(@PathVariable Long id) {
        return visitRepository.findById(id)
                .map(v -> ResponseEntity.ok(toResponse(v)))
                .orElse(ResponseEntity.notFound().build());
    }

    // AUTH: Create visit
    @PostMapping
    public ResponseEntity<?> createVisit(@RequestBody VisitRequest request, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (request.getPlaceId() == null) {
            return ResponseEntity.badRequest().body("placeId is required");
        }

        Optional<Place> placeOpt = placeRepository.findById(request.getPlaceId());
        if (placeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Place not found");
        }

        Visit visit = Visit.builder()
                .place(placeOpt.get())
                .user(user)
                .visitDate(request.getVisitDate())
                .note(request.getNote())
                .rating(request.getRating())
                .image(request.getImage())
                .tags(request.getTags() != null ? request.getTags() : List.of())
                .build();

        Visit saved = visitRepository.save(visit);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    // AUTH + OWNER: Update visit
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVisit(@PathVariable Long id, @RequestBody VisitRequest request, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Visit visit = visitRepository.findById(id).orElse(null);
        if (visit == null) {
            return ResponseEntity.notFound().build();
        }
        if (!visit.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (request.getVisitDate() != null) visit.setVisitDate(request.getVisitDate());
        visit.setNote(request.getNote());
        visit.setRating(request.getRating());
        visit.setImage(request.getImage());
        if (request.getTags() != null) visit.setTags(request.getTags());

        Visit saved = visitRepository.save(visit);
        return ResponseEntity.ok(toResponse(saved));
    }

    // AUTH + OWNER: Delete visit
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVisit(@PathVariable Long id, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Visit visit = visitRepository.findById(id).orElse(null);
        if (visit == null) {
            return ResponseEntity.notFound().build();
        }
        if (!visit.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        visitRepository.delete(visit);
        return ResponseEntity.noContent().build();
    }

    private VisitResponse toResponse(Visit v) {
        return VisitResponse.builder()
                .id(v.getId())
                .placeId(v.getPlace().getId())
                .placeName(v.getPlace().getName())
                .visitDate(v.getVisitDate())
                .note(v.getNote())
                .rating(v.getRating())
                .image(v.getImage())
                .tags(v.getTags())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }

    private User getUserFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }
}
