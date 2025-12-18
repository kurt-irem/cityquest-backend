package com.cityquest.cityquest_backend.repository;

import com.cityquest.cityquest_backend.model.Place;
import com.cityquest.cityquest_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    
    // Public access - all users can see all places
    List<Place> findAll();
    
    Optional<Place> findById(Long id);
    
    // Filter by creator
    List<Place> findByCreatedBy(User user);
    
    // For edit/delete authorization
    Optional<Place> findByIdAndCreatedBy(Long id, User user);
    
    // Search by category (public)
    List<Place> findByCategory(String category);
    
    // Search by name (public)
    List<Place> findByNameContainingIgnoreCase(String name);
}
