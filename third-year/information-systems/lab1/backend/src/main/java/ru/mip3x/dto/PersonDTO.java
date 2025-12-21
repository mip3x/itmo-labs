package ru.mip3x.dto;

public record PersonDTO(
        int id,
        String name,
        CoordinatesDTO coordinates,
        java.time.LocalDateTime creationDate,
        String eyeColor,
        String hairColor,
        LocationDTO location,
        Long height,
        java.time.ZonedDateTime birthday,
        Integer weight,
        String nationality) {
}
