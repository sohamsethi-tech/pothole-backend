package com.pothole.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "pothole")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pothole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double lat;
    private double lng;
    private String severity;
    private double speed;
    private double accelZ;
    private LocalDateTime detectedAt;

    @Transient
    private String timestamp;

    @PrePersist
    public void prePersist() {
        if (detectedAt == null) {
            if (timestamp != null && !timestamp.isBlank()) {
                try {
                    OffsetDateTime odt = OffsetDateTime.parse(timestamp);
                    detectedAt = odt.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
                } catch (Exception e) {
                    detectedAt = LocalDateTime.now(ZoneOffset.UTC);
                }
            } else {
                detectedAt = LocalDateTime.now(ZoneOffset.UTC);
            }
        }
    }

    public Pothole() {}

    public Long getId() { return id; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public double getAccelZ() { return accelZ; }
    public void setAccelZ(double accelZ) { this.accelZ = accelZ; }
    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime d) { this.detectedAt = d; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String t) { this.timestamp = t; }
}