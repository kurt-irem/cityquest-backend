package com.cityquest.cityquest_backend.repository;

import com.cityquest.cityquest_backend.model.Place;
import com.cityquest.cityquest_backend.model.User;
import com.cityquest.cityquest_backend.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByPlace(Place place);
    List<Visit> findByPlaceId(Long placeId);
    List<Visit> findByUser(User user);
    List<Visit> findByUserId(Long userId);

    @Query("SELECT COUNT(DISTINCT v.place.id) FROM Visit v WHERE v.user.id = :userId AND v.visitDate IS NOT NULL AND EXTRACT(YEAR FROM v.visitDate) = :year AND EXTRACT(MONTH FROM v.visitDate) = :month")
    Long countDistinctPlacesByUserAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT COALESCE(v.place.category, 'Other'), COUNT(DISTINCT v.place.id) FROM Visit v WHERE v.user.id = :userId AND v.visitDate IS NOT NULL AND EXTRACT(YEAR FROM v.visitDate) = :year AND EXTRACT(MONTH FROM v.visitDate) = :month GROUP BY COALESCE(v.place.category, 'Other')")
    List<Object[]> countDistinctPlacesByCategoryAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);
}
