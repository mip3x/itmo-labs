CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(30) UNIQUE,
    hashed_password VARCHAR(30)
);

CREATE TYPE form_of_education AS ENUM (
    'DISTANCE_EDUCATION',
    'FULL_TIME_EDUCATION',
    'EVENING_CLASSES'
);

CREATE TYPE semester AS ENUM (
    'SECOND',
    'THIRD',
    'SIXTH',
    'EIGHTH'
);

CREATE TYPE color AS ENUM (
    'GREEN',
    'RED',
    'YELLOW',
    'WHITE',
    'BROWN'
);

CREATE TABLE locations (
    id SERIAL UNIQUE,
    x DOUBLE PRECISION NOT NULL,
    y DOUBLE PRECISION,
    z INTEGER NOT NULL,
    name text NOT NULL,
    PRIMARY KEY (x, y, z)
);

CREATE TABLE persons (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    weight BIGINT,
    passport_id TEXT UNIQUE NOT NULL,
    eye_color color,
    location_id INTEGER REFERENCES locations(id),
    CHECK (length(name) > 0),
    CHECK (weight > 0),
    CHECK (length(passport_id) > 4)
);

CREATE TABLE coordinates (
    id SERIAL UNIQUE,
    x BIGINT,
    y DOUBLE PRECISION,
    PRIMARY KEY (x, y),
    CHECK (x <= 224)
);

CREATE TABLE study_groups (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    coordinates_id INTEGER REFERENCES coordinates(id) NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    students_count BIGINT,
    should_be_expelled BIGINT,
    form_of_education form_of_education NOT NULL,
    semester semester NOT NULL,
    admin_id INTEGER REFERENCES persons(id) NOT NULL,
    CHECK (length(name) > 0)
);