package com.pothole.backend.service;

import com.pothole.backend.model.Pothole;
import com.pothole.backend.repository.PotholeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PotholeService {

    @Autowired
    private PotholeRepository potholeRepository;

    public Pothole savePothole(Pothole pothole) {
        return potholeRepository.save(pothole);
    }

    public List<Pothole> getAllPotholes() {
        return potholeRepository.findAll();
    }

    public void deletePothole(Long id) {
        potholeRepository.deleteById(id);
    }

    // ✅ FIX: Controller needs this to return 404 properly
    public boolean existsById(Long id) {
        return potholeRepository.existsById(id);
    }
}
