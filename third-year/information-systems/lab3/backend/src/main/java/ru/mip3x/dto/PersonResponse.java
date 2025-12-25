package ru.mip3x.dto;

public record PersonResponse(
        int id,
        String name,
        CoordinatesResponse coordinates,
        java.time.LocalDateTime creationDate,
        String eyeColor,
        String hairColor,
        LocationResponse location,
        Long height,
        java.time.ZonedDateTime birthday,
        Integer weight,
        String nationality) {
}
