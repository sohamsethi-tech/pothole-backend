package com.pothole.backend;

import com.pothole.backend.model.Pothole;
import com.pothole.backend.service.PotholeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PotholeController {

    @Autowired
    private PotholeService potholeService;

    @GetMapping("/potholes")
    public ResponseEntity<List<Pothole>> getAllPotholes() {
        return ResponseEntity.ok(potholeService.getAllPotholes());
    }

    @PostMapping("/detect-pothole")
    public ResponseEntity<?> detectPothole(@RequestBody Pothole pothole) {
        Pothole saved = potholeService.savePothole(pothole);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/potholes")
    public ResponseEntity<?> savePothole(@RequestBody Pothole pothole) {
        Pothole saved = potholeService.savePothole(pothole);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/potholes/{id}")
    public ResponseEntity<?> deletePothole(@PathVariable Long id) {
        if (!potholeService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not found"));
        }
        potholeService.deletePothole(id);
        return ResponseEntity.ok(Map.of("message", "Deleted", "id", id));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}