package com.pothole.backend.service;

import com.pothole.backend.dto.PotholeRequestDTO;
import org.springframework.stereotype.Service;

/**
 * Service for analyzing pothole detection based on sensor data
 */
@Service
public class DetectionService {

    // Acceleration thresholds for detection (in m/s²)
    private static final double SEVERE_THRESHOLD = 15.0;    // High impact
    private static final double MEDIUM_THRESHOLD = 8.0;     // Medium impact
    private static final double LIGHT_THRESHOLD = 3.0;      // Minor bump

    // Speed-based thresholds (in km/h)
    private static final double HIGH_SPEED = 60.0;
    private static final double MEDIUM_SPEED = 40.0;

    /**
     * Calculate severity based on acceleration and speed
     */
    public String calculateSeverity(Double accelZ, Double speed) {
        if (accelZ == null) {
            return "Light";
        }

        // Normalize acceleration (absolute value for impact analysis)
        double absAccel = Math.abs(accelZ);

        // Speed factor: higher speed means more impact for same acceleration
        double speedFactor = 1.0;
        if (speed != null && speed > 0) {
            if (speed >= HIGH_SPEED) {
                speedFactor = 1.3;
            } else if (speed >= MEDIUM_SPEED) {
                speedFactor = 1.15;
            }
        }

        double adjustedAccel = absAccel * speedFactor;

        // Determine severity
        if (adjustedAccel >= SEVERE_THRESHOLD) {
            return "Severe";
        } else if (adjustedAccel >= MEDIUM_THRESHOLD) {
            return "Medium";
        } else if (adjustedAccel >= LIGHT_THRESHOLD) {
            return "Light";
        } else {
            return "Light";
        }
    }

    /**
     * Validate if detection is accurate enough
     */
    public boolean isValidDetection(PotholeRequestDTO dto) {
        // Must have coordinates
        if (dto.getLat() == null || dto.getLng() == null) {
            return false;
        }

        // Must have significant acceleration or user-specified severity
        if (dto.getAccelZ() == null || Math.abs(dto.getAccelZ()) < LIGHT_THRESHOLD) {
            // Allow if user explicitly specified, otherwise questionable
            return dto.getSeverity() != null && !dto.getSeverity().isBlank();
        }

        return true;
    }

    /**
     * Calculate confidence score (0-100)
     */
    public int calculateConfidence(Double accelZ, Double speed) {
        if (accelZ == null) {
            return 50;
        }

        double absAccel = Math.abs(accelZ);

        // Base confidence from acceleration magnitude
        int confidence = (int) Math.min(100, (absAccel / SEVERE_THRESHOLD) * 100);

        // Boost if speed is available and consistent
        if (speed != null && speed > 0) {
            confidence = Math.min(100, confidence + 10);
        }

        return Math.max(50, confidence);
    }

    /**
     * Get impact level description
     */
    public String getImpactDescription(String severity, Double accelZ) {
        return switch (severity) {
            case "Severe" -> String.format("High impact pothole detected (%.2f m/s²) - Immediate attention needed", Math.abs(accelZ != null ? accelZ : 0));
            case "Medium" -> String.format("Medium impact pothole detected (%.2f m/s²) - Should be repaired soon", Math.abs(accelZ != null ? accelZ : 0));
            case "Light" -> String.format("Minor road irregularity detected (%.2f m/s²) - Monitor", Math.abs(accelZ != null ? accelZ : 0));
            default -> "Pothole detected";
        };
    }
}

