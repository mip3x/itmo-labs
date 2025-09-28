package ru.mip3x.service.impl;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Primary
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<Person> findAllPersons() {
        return personRepository.findAll();
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
            person.setCoordinates(coordinates);
        }

        if (person.getLocation() == null) {
            throw new IllegalArgumentException("Location must be provided");
        }
        Location location = person.getLocation();
        if (location.getId() != null) {
            final Long locationId = location.getId();
            location = locationRepository.findById(locationId)
                                         .orElseThrow(() -> new IllegalArgumentException("Location " + locationId + " not found"));
            person.setLocation(location);
        }

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
        
        if (incomingPerson.getName() != null)
            existingPerson.setName(incomingPerson.getName());

        if (incomingPerson.getCoordinates() != null) {
            Coordinates coordinates = incomingPerson.getCoordinates();

            if (coordinates.getId() != null) {
                existingPerson.setCoordinates(coordinatesRepository.findById(coordinates.getId())
                              .orElseThrow(() -> new ResourceNotFoundException("Coordinates not found")));

            } else {
                existingPerson.setCoordinates(coordinatesRepository.save(coordinates));
            }
        }

        if (incomingPerson.getEyeColor() != null)
            existingPerson.setEyeColor(incomingPerson.getEyeColor());

        if (incomingPerson.getHairColor() != null)
            existingPerson.setHairColor(incomingPerson.getHairColor());
        
        if (incomingPerson.getLocation() != null) {
            Location location = incomingPerson.getLocation();

            if (location.getId() != null) {
                existingPerson.setLocation(locationRepository.findById(location.getId())
                              .orElseThrow(() -> new ResourceNotFoundException("Location not found")));
            } else {
                existingPerson.setLocation(locationRepository.save(location));
            }
        }

        if (incomingPerson.getHeight() != null)
            existingPerson.setHeight(incomingPerson.getHeight());

        if (incomingPerson.getBirthday() != null)
            existingPerson.setBirthday(incomingPerson.getBirthday());

        if (incomingPerson.getWeight() != null)
            existingPerson.setWeight(incomingPerson.getWeight());

        if (incomingPerson.getNationality() != null)
            existingPerson.setNationality(incomingPerson.getNationality());

        return personRepository.save(existingPerson);
    }

    @Override
    public void deletePerson(int id) {
        if (!personRepository.existsById(id))
            throw new ResourceNotFoundException("Person " + id + " not found");

        personRepository.deleteById(id);
    }
}
