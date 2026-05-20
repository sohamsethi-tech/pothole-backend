package com.pothole.backend.service;

import com.pothole.backend.dto.PotholeRequestDTO;
import com.pothole.backend.model.Pothole;
import com.pothole.backend.repository.PotholeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PotholeServiceImpl implements PotholeService {

    private final PotholeRepository repository;
    private final DetectionService detectionService;

    public PotholeServiceImpl(PotholeRepository repository, DetectionService detectionService) {
        this.repository = repository;
        this.detectionService = detectionService;
    }

    @Override
    public Pothole save(PotholeRequestDTO dto) {
        Pothole pothole = new Pothole();
        pothole.setLat(dto.getLat());
        pothole.setLng(dto.getLng());
        
        // Set severity with automatic calculation if not provided
        String severity = dto.getSeverity();
        if (severity == null || severity.isBlank()) {
            severity = detectionService.calculateSeverity(dto.getAccelZ(), dto.getSpeed());
        }
        pothole.setSeverity(severity);
        
        pothole.setSpeed(dto.getSpeed());
        pothole.setAccelZ(dto.getAccelZ());
        pothole.setPhotoUri(dto.getPhotoUri());
        
        // Calculate and save confidence score and impact description
        int confidence = detectionService.calculateConfidence(dto.getAccelZ(), dto.getSpeed());
        String impact = detectionService.getImpactDescription(severity, dto.getAccelZ());
        
        pothole.setConfidenceScore(confidence);
        pothole.setImpactDescription(impact);
        
        return repository.save(pothole);
    }

    @Override
    public List<Pothole> getAll() {
        return repository.findAllByOrderByDetectedAtDesc();
    }

    @Override
    public List<Pothole> getAll(String severity) {
        if (severity != null && !severity.isBlank()) {
            return getBySeverity(severity);
        }
        return getAll();
    }

    @Override
    public List<Pothole> getBySeverity(String severity) {
        return repository.findBySeverityContainingIgnoreCaseOrderByDetectedAtDesc(severity);
    }

    @Override
    public boolean delete(Long id) {
        Optional<Pothole> pothole = repository.findById(id);
        if (pothole.isPresent()) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    // Additional methods for test compatibility
    @Override
    public List<Pothole> getAllPotholes() {
        return getAll();
    }

    @Override
    public Pothole savePothole(Pothole pothole) {
        return repository.save(pothole);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public void deletePothole(Long id) {
        repository.deleteById(id);
    }
}

