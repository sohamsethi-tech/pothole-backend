package com.pothole.backend.controller;

import com.pothole.backend.dto.PotholeRequestDTO;
import com.pothole.backend.model.Pothole;
import com.pothole.backend.service.PotholeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller — all routes match what the React Native app calls:
 *
 *   POST   /api/detect-pothole   → save a new detection
 *   GET    /api/potholes         → fetch all (optional ?severity= filter)
 *   DELETE /api/potholes/{id}    → delete a record
 *   GET    /api/stats            → quick summary counts
 *   GET    /api/health           → simple health check
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")   // allow React Native / Expo dev client
public class PotholeController {

    private final PotholeService service;

    public PotholeController(PotholeService service) {
        this.service = service;
    }

    // ── POST /api/detect-pothole ───────────────────────────────────────────
    @PostMapping("/detect-pothole")
    public ResponseEntity<?> detectPothole(@RequestBody PotholeRequestDTO dto) {
        // Basic validation — lat/lng must be present
        if (dto.getLat() == null || dto.getLng() == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "lat and lng are required");
            return ResponseEntity.badRequest().body(err);
        }

        Pothole saved = service.save(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("id", saved.getId());
        response.put("severity", saved.getSeverity());
        response.put("message", "Pothole saved successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── GET /api/potholes ─────────────────────────────────────────────────
    // Optional query param: ?severity=Severe  (or Light / Medium)
    @GetMapping("/potholes")
    public ResponseEntity<List<Pothole>> getPotholes(
            @RequestParam(required = false) String severity) {

        List<Pothole> result = (severity != null && !severity.isBlank())
                ? service.getBySeverity(severity)
                : service.getAll();

        return ResponseEntity.ok(result);
    }

    // ── DELETE /api/potholes/{id} ─────────────────────────────────────────
    @DeleteMapping("/potholes/{id}")
    public ResponseEntity<?> deletePothole(@PathVariable Long id) {
        boolean deleted = service.delete(id);

        if (!deleted) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Pothole not found with id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Deleted pothole #" + id);
        return ResponseEntity.ok(response);
    }

    // ── GET /api/stats ────────────────────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        List<Pothole> all = service.getAll();

        long severe = all.stream().filter(p -> p.getSeverity() != null && p.getSeverity().contains("Severe")).count();
        long medium = all.stream().filter(p -> p.getSeverity() != null && p.getSeverity().contains("Medium")).count();
        long light  = all.stream().filter(p -> p.getSeverity() != null && p.getSeverity().contains("Light")).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total",  all.size());
        stats.put("severe", severe);
        stats.put("medium", medium);
        stats.put("light",  light);

        return ResponseEntity.ok(stats);
    }

    // ── GET /api/health ───────────────────────────────────────────────────
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> resp = new HashMap<>();
        resp.put("status", "UP");
        resp.put("service", "Pothole Detection Backend");
        return ResponseEntity.ok(resp);
    }
}
