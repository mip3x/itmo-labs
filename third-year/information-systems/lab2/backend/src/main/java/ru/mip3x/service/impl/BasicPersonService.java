package ru.mip3x.service.impl;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.LongSupplier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.mip3x.exception.ResourceNotFoundException;
import ru.mip3x.model.Color;
import ru.mip3x.model.Coordinates;
import ru.mip3x.model.Location;
import ru.mip3x.model.Person;
import ru.mip3x.repository.CoordinatesRepository;
import ru.mip3x.repository.LocationRepository;
import ru.mip3x.repository.PersonRepository;
import ru.mip3x.service.PersonService;

@Slf4j
@Service
@AllArgsConstructor
public class BasicPersonService implements PersonService {
    private final PersonRepository personRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final LocationRepository locationRepository;

    @Override
    public Page<Person> findAllPersons(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Person savePerson(Person person) {
        if (person.getCoordinates() == null) {
            throw new IllegalArgumentException("Coordinates must be provided");
        }

        Coordinates coordinates = person.getCoordinates();
        if (coordinates.getId() != null) {
            final Long coordinatesId = coordinates.getId(); 
            coordinates = coordinatesRepository.findById(coordinatesId)
                                               .orElseThrow(() -> new IllegalArgumentException(String.format("Coordinates %d not found", coordinatesId)));
        } else {
            coordinates = coordinatesRepository.save(coordinates);
        }
        person.setCoordinates(coordinates);

        if (person.getLocation() == null) {
            throw new IllegalArgumentException("Location must be provided");
        }
        Location location = person.getLocation();
        if (location.getId() != null) {
            final Long locationId = location.getId();
            location = locationRepository.findById(locationId)
                                         .orElseThrow(() -> new IllegalArgumentException(String.format("Location %d not found", locationId)));
        } else {
            location = locationRepository.save(location);
        }
        person.setLocation(location);

        ensureUniqueConstraints(person);

        return personRepository.save(person);
    }

    @Override
    public Person findById(int id) {
        return personRepository.findPersonById(id);
    }

    @Override
    @Transactional
    public Person updatePerson(int id, Person incomingPerson) {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person " + id + " not found"));

        mergePerson(existingPerson, incomingPerson);

        ensureUniqueConstraints(existingPerson, id);

        return personRepository.save(existingPerson);
    }

    @Override
    public void deletePerson(int id) {
        if (!personRepository.existsById(id))
            throw new ResourceNotFoundException("Person " + id + " not found");

        personRepository.deleteById(id);
    }

    @Override
    public long sumHeight() {
        return personRepository.sumHeight();
    }

    @Override
    public long countWeightLessThan(int weight) {
        return personRepository.countByWeightLessThan(weight);
    }

    @Override
    public long countFiltered(Integer maxWeight, Color hairColor, Color eyeColor) {
        if (maxWeight == null && hairColor == null && eyeColor == null) {
            return countAll();
        }
        return personRepository.countFiltered(maxWeight, hairColor, eyeColor);
    }

    @Override
    public long countAll() {
        return personRepository.count();
    }

    @Override
    public long countByHairColor(Color hairColor) {
        return personRepository.countByHairColor(hairColor);
    }

    @Override
    public long countByEyeColor(Color eyeColor) {
        return personRepository.countByEyeColor(eyeColor);
    }

    @Override
    public List<Person> findBirthdayBefore(ZonedDateTime date) {
        return personRepository.findByBirthdayBefore(date);
    }

    @Override
    public Page<Person> findBirthdayBefore(ZonedDateTime date, Pageable pageable) {
        return personRepository.findByBirthdayBefore(date, pageable);
    }

    @Override
    public double hairColorShare(Color hairColor) {
        return share(() -> personRepository.countByHairColor(hairColor));
    }

    @Override
    public double eyeColorShare(Color eyeColor) {
        return share(() -> personRepository.countByEyeColor(eyeColor));
    }

    private double share(LongSupplier countFn) {
        long total = personRepository.count();
        if (total == 0) return 0.0;
        long matched = countFn.getAsLong();
        return (matched * 100.0) / total;
    }

    private void ensureUniqueConstraints(Person person, Integer selfId) {
        if (person.getName() != null && person.getBirthday() != null) {
            boolean duplicates = selfId == null
                    ? personRepository.existsByNameAndBirthday(person.getName(), person.getBirthday())
                    : personRepository.existsByNameAndBirthdayAndIdNot(person.getName(), person.getBirthday(), selfId);
            if (duplicates) {
                throw new IllegalArgumentException("Person with the same name and birthday already exists");
            }
        }

        Coordinates coords = person.getCoordinates();
        if (coords != null && coords.getY() != null) {
            boolean coordsTaken = selfId == null
                    ? personRepository.existsByCoordinatesXAndCoordinatesY(coords.getX(), coords.getY())
                    : personRepository.existsByCoordinatesXAndCoordinatesYAndIdNot(coords.getX(), coords.getY(), selfId);
            if (coordsTaken) {
                throw new IllegalArgumentException("Coordinates are already used by another person");
            }
        }
    }

    private void ensureUniqueConstraints(Person person) {
        ensureUniqueConstraints(person, null);
    }

    private void mergePerson(Person target, Person incoming) {
        if (incoming.getName() != null) {
            target.setName(incoming.getName());
        }

        if (incoming.getCoordinates() != null) {
            Coordinates coordinates = incoming.getCoordinates();
            if (coordinates.getId() != null) {
                Coordinates persisted = coordinatesRepository.findById(coordinates.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("Coordinates %d not found", coordinates.getId())));
                target.setCoordinates(persisted);
            } else {
                target.setCoordinates(coordinatesRepository.save(coordinates));
            }
        }

        if (incoming.getEyeColor() != null) {
            target.setEyeColor(incoming.getEyeColor());
        }

        if (incoming.getHairColor() != null) {
            target.setHairColor(incoming.getHairColor());
        }

        if (incoming.getLocation() != null) {
            Location location = incoming.getLocation();
            if (location.getId() != null) {
                Location persisted = locationRepository.findById(location.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("Location %d not found", location.getId())));
                target.setLocation(persisted);
            } else {
                target.setLocation(locationRepository.save(location));
            }
        }

        if (incoming.getHeight() != null) {
            target.setHeight(incoming.getHeight());
        }

        if (incoming.getBirthday() != null) {
            target.setBirthday(incoming.getBirthday());
        }

        if (incoming.getWeight() != null) {
            target.setWeight(incoming.getWeight());
        }

        if (incoming.getNationality() != null) {
            target.setNationality(incoming.getNationality());
        }
    }
}
