package com.cityquest.cityquest_backend.repository;

import com.cityquest.cityquest_backend.model.Place;
import com.cityquest.cityquest_backend.model.User;
import com.cityquest.cityquest_backend.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByPlace(Place place);
    List<Visit> findByPlaceId(Long placeId);
    List<Visit> findByUser(User user);
    List<Visit> findByUserId(Long userId);
}
