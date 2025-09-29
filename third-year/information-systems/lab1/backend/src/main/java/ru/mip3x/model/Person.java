package ru.mip3x.model;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY,
               optional = false,
               cascade = {CascadeType.MERGE}
    )
    @JoinColumn(name = "coordinates_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Coordinates coordinates;

    @Column(nullable = false, updatable = false, name = "creation_date")
    private java.time.LocalDateTime creationDate;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Color eyeColor;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Color hairColor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY,
               optional = false,
               cascade = {CascadeType.MERGE}
    )
    @JoinColumn(name = "location_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Location location;

    @Positive
    private Long height;

    @NotNull
    @Column(nullable = false, columnDefinition = "TIMESTAMPTZ")
    private java.time.ZonedDateTime birthday;

    @NotNull
    @Column(nullable = false)
    @Positive
    private Integer weight;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Country nationality;

    @PrePersist
    void onCreate() {
        if (creationDate == null)
            creationDate = LocalDateTime.now();
    }
}
