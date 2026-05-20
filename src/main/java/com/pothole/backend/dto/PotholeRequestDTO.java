package com.pothole.backend.dto;

import jakarta.validation.constraints.*;

public class PotholeRequestDTO {

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double lat;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double lng;

    @NotBlank(message = "Severity is required")
    private String severity;

    @DecimalMin(value = "0.0", message = "Speed cannot be negative")
    @DecimalMax(value = "300.0", message = "Speed should not exceed 300 km/h")
    private Double speed;

    @NotNull(message = "Z-axis acceleration is required for detection")
    private Double accelZ;

    private String photoUri;

    // ── Constructors ────────────────────────────────────────────────────

    public PotholeRequestDTO() {
    }

    public PotholeRequestDTO(Double lat, Double lng, String severity) {
        this.lat = lat;
        this.lng = lng;
        this.severity = severity;
    }

    // ── Getters & Setters ──────────────────────────────────────────────────

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public Double getSpeed() { return speed; }
    public void setSpeed(Double speed) { this.speed = speed; }

    public Double getAccelZ() { return accelZ; }
    public void setAccelZ(Double accelZ) { this.accelZ = accelZ; }

    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
}

