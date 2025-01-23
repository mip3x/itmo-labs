package ru.mip3x.lab4.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PointDTO {
    @JsonbProperty("x")
    @NotNull(message = "X coordinate is required")
    private Double x;

    @JsonbProperty("y")
    @NotNull(message = "Y coordinate is required")
    private Double y;

    @JsonbProperty("radius")
    @NotNull(message = "Radius is required")
    @Positive(message = "Radius must be positive")
    private Double radius;

    public PointDTO(Double x, Double y, Double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
}
