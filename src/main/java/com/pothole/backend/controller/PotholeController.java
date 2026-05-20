package com.pothole.backend.controller;

import com.pothole.backend.dto.DetectionResponseDTO;
import com.pothole.backend.dto.PotholeRequestDTO;
import com.pothole.backend.model.Pothole;
import com.pothole.backend.service.DetectionService;
import com.pothole.backend.service.PotholeService;
import jakarta.validation.Valid;
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
    private final DetectionService detectionService;

    public PotholeController(PotholeService service, DetectionService detectionService) {
        this.service = service;
        this.detectionService = detectionService;
    }

    // ── POST /api/detect-pothole ───────────────────────────────────────────
    /**
     * Detect and save a pothole using sensor data
     */
    @PostMapping("/detect-pothole")
    public ResponseEntity<?> detectPothole(@Valid @RequestBody PotholeRequestDTO dto) {
        try {
            // Validate detection data
            if (!detectionService.isValidDetection(dto)) {
                DetectionResponseDTO error = new DetectionResponseDTO();
                error.setSuccess(false);
                error.setError("Insufficient sensor data for pothole detection. Acceleration required.");
                return ResponseEntity.badRequest().body(error);
            }

            // Calculate severity based on acceleration and speed
            String calculatedSeverity = dto.getSeverity() != null && !dto.getSeverity().isBlank()
                    ? dto.getSeverity()
                    : detectionService.calculateSeverity(dto.getAccelZ(), dto.getSpeed());

            // Update DTO with calculated severity
            dto.setSeverity(calculatedSeverity);

            // Save pothole
            Pothole saved = service.save(dto);

            // Build response
            DetectionResponseDTO response = new DetectionResponseDTO();
            response.setSuccess(true);
            response.setId(saved.getId());
            response.setSeverity(calculatedSeverity);
            response.setImpactDescription(saved.getImpactDescription());
            response.setConfidenceScore(saved.getConfidenceScore());
            response.setLat(saved.getLat());
            response.setLng(saved.getLng());
            response.setSpeed(saved.getSpeed());
            response.setMessage("Pothole detected and saved successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            DetectionResponseDTO error = new DetectionResponseDTO();
            error.setSuccess(false);
            error.setError("Error processing detection: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
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

        // Calculate average speed impact
        double avgSpeed = all.stream()
                .filter(p -> p.getSpeed() != null)
                .mapToDouble(Pothole::getSpeed)
                .average()
                .orElse(0.0);

        // Calculate average acceleration magnitude
        double avgAccel = all.stream()
                .filter(p -> p.getAccelZ() != null)
                .mapToDouble(p -> Math.abs(p.getAccelZ()))
                .average()
                .orElse(0.0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("total",  all.size());
        stats.put("severe", severe);
        stats.put("medium", medium);
        stats.put("light",  light);
        stats.put("averageSpeed", String.format("%.2f km/h", avgSpeed));
        stats.put("averageAcceleration", String.format("%.2f m/s²", avgAccel));

        return ResponseEntity.ok(stats);
    }

    // ── GET /api/health ───────────────────────────────────────────────────
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> resp = new HashMap<>();
        resp.put("status", "UP");
        resp.put("service", "Pothole Detection Backend");
        resp.put("version", "1.0.0");
        resp.put("detection", "Enabled with speed analysis");
        return ResponseEntity.ok(resp);
    }
}
