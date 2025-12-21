package ru.mip3x.service.impl;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.mip3x.model.Color;
import ru.mip3x.exception.ResourceNotFoundException;
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
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<Person> findAllPersons() {
        return personRepository.findAll();
    }

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
                                               .orElseThrow(() -> new IllegalArgumentException("Coordinates " + coordinatesId + " not found"));
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
                                         .orElseThrow(() -> new IllegalArgumentException("Location " + locationId + " not found"));
        } else {
            location = locationRepository.save(location);
        }
        person.setLocation(location);

        return personRepository.save(person);
    }

    @Override
    public Person findById(int id) {
        return personRepository.findPersonById(id);
    }

    @Override
    public Person updatePerson(int id, Person incomingPerson) {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person " + id + " not found"));

        mergePerson(existingPerson, incomingPerson);

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
    public double hairColorShare(Color hairColor) {
        long total = personRepository.count();
        if (total == 0) return 0.0;
        long matched = personRepository.countByHairColor(hairColor);
        return (matched * 100.0) / total;
    }

    @Override
    public double eyeColorShare(Color eyeColor) {
        long total = personRepository.count();
        if (total == 0) return 0.0;
        long matched = personRepository.countByEyeColor(eyeColor);
        return (matched * 100.0) / total;
    }

    private void mergePerson(Person target, Person incoming) {
        if (incoming.getName() != null) {
            target.setName(incoming.getName());
        }

        if (incoming.getCoordinates() != null) {
            Coordinates coordinates = incoming.getCoordinates();
            if (coordinates.getId() != null) {
                Coordinates persisted = coordinatesRepository.findById(coordinates.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Coordinates not found"));
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
                        .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
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
