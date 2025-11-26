package ru.mip3x.dto;

import lombok.Data;

@Data
public class PersonDto {
    private int id;
    private String name;
    private CoordinatesDTO coordinates;
    private java.time.LocalDateTime creationDate;
    private String eyeColor;
    private String hairColor;
    private LocationDTO location;
    private Long height;
    private java.time.ZonedDateTime birthday;
    private Integer weight;
    private String nationality;
}
