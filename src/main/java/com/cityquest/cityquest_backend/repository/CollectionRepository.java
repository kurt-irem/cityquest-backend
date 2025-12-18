package com.cityquest.cityquest_backend.repository;

import com.cityquest.cityquest_backend.model.Collection;
import com.cityquest.cityquest_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByCreatedBy(User createdBy);
    Optional<Collection> findByIdAndCreatedBy(Long id, User createdBy);
}
