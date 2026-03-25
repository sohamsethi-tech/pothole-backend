package com.pothole.backend.repository;

import com.pothole.backend.model.Pothole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PotholeRepository extends JpaRepository<Pothole, Long> {
}
