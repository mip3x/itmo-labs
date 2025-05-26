package ru.mip3x.lab4.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object representing a point with coordinates and radius
 * Used for client-server communication in JSON format
 */
@Getter
@Setter
@NoArgsConstructor
public class PointDTO {
    /**
     * X coordinate of the point
     */
    @JsonbProperty("x")
    @NotNull(message = "X coordinate is required")
    private Double x;

    /**
     * Y coordinate of the point
     */
    @JsonbProperty("y")
    @NotNull(message = "Y coordinate is required")
    private Double y;

    /**
     * Radius associated with the point. Must be positive
     */
    @JsonbProperty("radius")
    @NotNull(message = "Radius is required")
    @Positive(message = "Radius must be positive")
    private Double radius;

    /**
     * Constructs a PointDTO with the given coordinates and radius
     *
     * @param x      X coordinate
     * @param y      Y coordinate
     * @param radius Radius value (must be positive)
     */
    public PointDTO(Double x, Double y, Double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
}
