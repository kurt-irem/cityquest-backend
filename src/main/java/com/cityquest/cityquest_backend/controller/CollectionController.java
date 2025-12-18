package com.cityquest.cityquest_backend.controller;

import com.cityquest.cityquest_backend.dto.CollectionRequest;
import com.cityquest.cityquest_backend.dto.CollectionResponse;
import com.cityquest.cityquest_backend.dto.PlaceResponse;
import com.cityquest.cityquest_backend.model.Collection;
import com.cityquest.cityquest_backend.model.Place;
import com.cityquest.cityquest_backend.model.User;
import com.cityquest.cityquest_backend.repository.CollectionRepository;
import com.cityquest.cityquest_backend.repository.PlaceRepository;
import com.cityquest.cityquest_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserRepository userRepository;

    // PUBLIC: List all collections
    @GetMapping
    public ResponseEntity<List<CollectionResponse>> getAllCollections() {
        List<CollectionResponse> resp = collectionRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    // PUBLIC: Single collection by id
    @GetMapping("/{id}")
    public ResponseEntity<CollectionResponse> getCollection(@PathVariable Long id) {
        return collectionRepository.findById(id)
            .map(c -> ResponseEntity.ok(toResponse(c)))
            .orElse(ResponseEntity.notFound().build());
    }

    // AUTH: User's own collections
    @GetMapping("/my-collections")
    public ResponseEntity<List<CollectionResponse>> getMyCollections(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<CollectionResponse> resp = collectionRepository.findByCreatedBy(user).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    // AUTH: Create
    @PostMapping
    public ResponseEntity<CollectionResponse> createCollection(@RequestBody CollectionRequest request,
                                                               Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Collection c = new Collection();
        c.setTitle(request.getTitle());
        c.setDescription(request.getDescription());
        c.setTheme(request.getTheme());
        c.setColor(request.getColor());
        c.setCreatedBy(user);

        // Optional: initialize places
        if (request.getPlaceIds() != null && !request.getPlaceIds().isEmpty()) {
            List<Place> places = placeRepository.findAllById(request.getPlaceIds());
            c.setPlaces(new ArrayList<>(places));
        }

        Collection saved = collectionRepository.save(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    // AUTH + OWNER: Update (including replacing places)
    @PutMapping("/{id}")
    public ResponseEntity<CollectionResponse> updateCollection(@PathVariable Long id,
                                                               @RequestBody CollectionRequest request,
                                                               Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Collection> opt = collectionRepository.findByIdAndCreatedBy(id, user);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Collection c = opt.get();
        c.setTitle(request.getTitle());
        c.setDescription(request.getDescription());
        c.setTheme(request.getTheme());
        c.setColor(request.getColor());

        if (request.getPlaceIds() != null) {
            List<Place> places = placeRepository.findAllById(request.getPlaceIds());
            c.setPlaces(new ArrayList<>(places));
        }

        Collection saved = collectionRepository.save(c);
        return ResponseEntity.ok(toResponse(saved));
    }

    // AUTH + OWNER: Delete collection
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Collection> opt = collectionRepository.findByIdAndCreatedBy(id, user);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        collectionRepository.delete(opt.get());
        return ResponseEntity.noContent().build();
    }

    // AUTH + OWNER: Add place to collection
    @PostMapping("/{id}/places/{placeId}")
    public ResponseEntity<CollectionResponse> addPlaceToCollection(@PathVariable Long id,
                                                                   @PathVariable Long placeId,
                                                                   Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Collection> opt = collectionRepository.findByIdAndCreatedBy(id, user);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        Collection c = opt.get();

        Optional<Place> placeOpt = placeRepository.findById(placeId);
        if (placeOpt.isEmpty()) return ResponseEntity.notFound().build();

        Place p = placeOpt.get();
        if (c.getPlaces().stream().noneMatch(pl -> pl.getId().equals(p.getId()))) {
            c.getPlaces().add(p);
        }
        Collection saved = collectionRepository.save(c);
        return ResponseEntity.ok(toResponse(saved));
    }

    // AUTH + OWNER: Remove place from collection
    @DeleteMapping("/{id}/places/{placeId}")
    public ResponseEntity<CollectionResponse> removePlaceFromCollection(@PathVariable Long id,
                                                                        @PathVariable Long placeId,
                                                                        Authentication authentication) {
        User user = getUserFromAuth(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Collection> opt = collectionRepository.findByIdAndCreatedBy(id, user);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        Collection c = opt.get();

        c.setPlaces(c.getPlaces().stream()
            .filter(pl -> !pl.getId().equals(placeId))
            .collect(Collectors.toList()));

        Collection saved = collectionRepository.save(c);
        return ResponseEntity.ok(toResponse(saved));
    }

    // Helpers
    private CollectionResponse toResponse(Collection c) {
        List<PlaceResponse> placeResponses = c.getPlaces() == null ? List.of() : c.getPlaces().stream()
            .map(this::toPlaceResponse)
            .collect(Collectors.toList());

        CollectionResponse resp = new CollectionResponse();
        resp.setId(c.getId());
        resp.setTitle(c.getTitle());
        resp.setDescription(c.getDescription());
        resp.setTheme(c.getTheme());
        resp.setColor(c.getColor());
        resp.setCreatedAt(c.getCreatedAt());
        resp.setCreatedByUsername(c.getCreatedBy() != null ? c.getCreatedBy().getUsername() : null);
        resp.setCreatedByUserId(c.getCreatedBy() != null ? c.getCreatedBy().getId() : null);
        resp.setPlaces(placeResponses);
        return resp;
    }

    private PlaceResponse toPlaceResponse(Place p) {
        PlaceResponse pr = new PlaceResponse();
        pr.setId(p.getId());
        pr.setName(p.getName());
        pr.setAddress(p.getAddress());
        pr.setCategory(p.getCategory());
        pr.setLatitude(p.getLatitude());
        pr.setLongitude(p.getLongitude());
        pr.setGooglePlaceId(p.getGooglePlaceId());
        pr.setGoogleMapsUrl(p.getGoogleMapsUrl());
        pr.setCreatedByUsername(p.getCreatedBy() != null ? p.getCreatedBy().getUsername() : null);
        pr.setCreatedByUserId(p.getCreatedBy() != null ? p.getCreatedBy().getId() : null);
        pr.setCreatedAt(p.getCreatedAt());
        pr.setUpdatedAt(p.getUpdatedAt());
        return pr;
    }

    private User getUserFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }
}
