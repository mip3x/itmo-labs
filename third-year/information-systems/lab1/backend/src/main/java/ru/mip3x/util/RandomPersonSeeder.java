package ru.mip3x.util;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.mip3x.model.Color;
import ru.mip3x.model.Coordinates;
import ru.mip3x.model.Country;
import ru.mip3x.model.Location;
import ru.mip3x.model.Person;
import ru.mip3x.repository.CoordinatesRepository;
import ru.mip3x.repository.LocationRepository;
import ru.mip3x.service.PersonService;

/**
 * Generates random Person entities on application start when property seed.persons-count is set.
 * Example: SEED_PERSONS_COUNT=50 (or -Dseed.persons-count=50) will create 50 random people.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "seed", name = "persons-count")
public class RandomPersonSeeder implements CommandLineRunner {

    private static final int DEFAULT_START_YEAR = 1950;
    private static final int DEFAULT_END_YEAR = 2010;
    private static final String[] MALE_FIRST_NAMES = {
            "Ivan", "Sergey", "Pavel", "Alexey", "Roman",
            "Viktor", "Anton", "Lev", "Andrey", "Maksim"
    };
    private static final String[] FEMALE_FIRST_NAMES = {
            "Anna", "Maria", "Olga", "Daria", "Irina",
            "Nina", "Sofia", "Elena", "Alina", "Tatiana"
    };
    private static final String[] MALE_LAST_NAMES = {
            "Petrov", "Sidorov", "Smirnov", "Kuznetsov", "Orlov",
            "Morozov", "Fedorov", "Lebedev", "Belov", "Karpov"
    };
    private static final String[] FEMALE_LAST_NAMES = {
            "Petrova", "Sidorova", "Smirnova", "Kuznetsova", "Orlova",
            "Morozova", "Fedorova", "Lebedeva", "Belova", "Karpova"
    };
    private static final String[] LOCATION_NAMES = {
            "Moscow", "St. Petersburg", "Barcelona", "Bangkok", "Helsinki",
            "Dubai", "Singapore", "Tokyo", "Madrid", "Lisbon",
            "Vienna", "Prague", "Warsaw", "Budapest", "Tallinn"
    };

    private final PersonService personService;
    private final CoordinatesRepository coordinatesRepository;
    private final LocationRepository locationRepository;

    @Value("${seed.persons-count}")
    private int count;

    @Value("${seed.reuse-existing:true}")
    private boolean reuseExisting;

    private final Random random = new Random();

    @Override
    public void run(String... args) {
        log.info("Seeding {} random persons (reuseExisting={})", count, reuseExisting);

        final List<Coordinates> coordPool = reuseExisting
                ? new ArrayList<>(coordinatesRepository.findAll())
                : new ArrayList<>();
        final List<Location> locPool = reuseExisting
                ? new ArrayList<>(locationRepository.findAll())
                : new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Person person = buildRandomPerson(coordPool, locPool);
            Person saved = personService.savePerson(person);

            // add freshly created coords/locations to pool for possible reuse in next iterations
            if (saved.getCoordinates() != null && saved.getCoordinates().getId() != null) {
                coordPool.add(saved.getCoordinates());
            }
            if (saved.getLocation() != null && saved.getLocation().getId() != null) {
                locPool.add(saved.getLocation());
            }
        }

        log.info("Seeding done");
    }

    private Person buildRandomPerson(List<Coordinates> coordPool, List<Location> locPool) {
        Person p = new Person();
        p.setName(randomName());
        p.setEyeColor(pickEnum(Color.values()));
        p.setHairColor(pickEnum(Color.values()));
        p.setNationality(pickEnum(Country.values()));
        p.setWeight(random.nextInt(120) + 50); // 50..169

        // 20% chance height is null
        p.setHeight(random.nextDouble() < 0.2 ? null : (long) (random.nextInt(70) + 130)); // 130..199

        p.setBirthday(randomBirthday());

        p.setCoordinates(pickCoordinates(coordPool));
        p.setLocation(pickLocation(locPool));

        return p;
    }

    private Coordinates pickCoordinates(List<Coordinates> coordPool) {
        boolean reuse = reuseExisting && !coordPool.isEmpty() && random.nextBoolean();
        if (reuse) {
            return coordPool.get(random.nextInt(coordPool.size()));
        }

        Coordinates c = new Coordinates();
        c.setX(random.nextDouble() * 800 - 100); // range -100..700 (> -860 constraint)
        c.setY((float) (random.nextDouble() * 300)); // 0..300 (<= 396 constraint)
        return c;
    }

    private Location pickLocation(List<Location> locPool) {
        boolean reuse = reuseExisting && !locPool.isEmpty() && random.nextBoolean();
        if (reuse) {
            return locPool.get(random.nextInt(locPool.size()));
        }

        Location l = new Location();
        l.setX((float) (random.nextDouble() * 1000 - 200)); // -200..800
        l.setY(random.nextInt(800) - 200); // -200..599
        l.setName(randomLocationName());
        return l;
    }

    private ZonedDateTime randomBirthday() {
        int startDay = (int) LocalDate.of(DEFAULT_START_YEAR, 1, 1).toEpochDay();
        int endDay = (int) LocalDate.of(DEFAULT_END_YEAR, 12, 31).toEpochDay();
        long randomDay = startDay + random.nextInt(endDay - startDay + 1);
        return LocalDate.ofEpochDay(randomDay).atStartOfDay(ZoneOffset.UTC);
    }

    private <T> T pickEnum(T[] values) {
        return values[random.nextInt(values.length)];
    }

    private String randomName() {
        boolean female = random.nextBoolean();
        String first = female
                ? FEMALE_FIRST_NAMES[random.nextInt(FEMALE_FIRST_NAMES.length)]
                : MALE_FIRST_NAMES[random.nextInt(MALE_FIRST_NAMES.length)];
        String last = female
                ? FEMALE_LAST_NAMES[random.nextInt(FEMALE_LAST_NAMES.length)]
                : MALE_LAST_NAMES[random.nextInt(MALE_LAST_NAMES.length)];
        return first + " " + last;
    }

    private String randomLocationName() {
        return LOCATION_NAMES[random.nextInt(LOCATION_NAMES.length)];
    }
}
