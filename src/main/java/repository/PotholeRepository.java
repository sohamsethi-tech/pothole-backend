package com.pothole.backend.repository;

import com.pothole.backend.model.Pothole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PotholeRepository extends JpaRepository<Pothole, Long> {

    // Latest first
    List<Pothole> findAllByOrderByDetectedAtDesc();

    // Filter by severity keyword (e.g. "Severe", "Medium", "Light")
    List<Pothole> findBySeverityContainingIgnoreCaseOrderByDetectedAtDesc(String severity);
}
