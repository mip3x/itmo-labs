package ru.mip3x.controller;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ru.mip3x.model.Color;
import ru.mip3x.dto.PersonDTO;
import ru.mip3x.mapper.PersonMapper;
import ru.mip3x.model.Person;
import ru.mip3x.service.PersonService;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/people")
@AllArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public List<PersonDTO> findAllPersons(@RequestParam(value = "birthday_before", required = false) String birthdayBefore) {
        if (birthdayBefore != null && !birthdayBefore.isBlank()) {
            try {
                ZonedDateTime date = ZonedDateTime.parse(birthdayBefore);
                return personService.findBirthdayBefore(date).stream()
                                    .map(PersonMapper::toDto)
                                    .toList();
            } catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format, expected ISO date", e);
            }
        }

        return personService.findAllPersons()
                            .stream()
                            .map(PersonMapper::toDto)
                            .toList();
    }

    @GetMapping("/paged")
    public Page<PersonDTO> findAllPersonsPaged(Pageable pageable) {
        return personService.findAllPersons(pageable)
                            .map(PersonMapper::toDto);
    }

    @PostMapping
    public ResponseEntity<PersonDTO> createPerson(@Valid @RequestBody Person person) {
        Person savedPerson = personService.savePerson(person);
        return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(PersonMapper.toDto(savedPerson));
    }

    @GetMapping("/{id}")
    public PersonDTO findById(@PathVariable int id) {
        return PersonMapper.toDto(personService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable int id, @Valid @RequestBody Person person) {
        Person updatedPerson = personService.updatePerson(id, person);
        return ResponseEntity.ok(PersonMapper.toDto(updatedPerson));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable int id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/height/sum")
    public long sumHeight() {
        return personService.sumHeight();
    }

    @GetMapping("/count")
    public long count(@RequestParam(value = "max_weight", required = false) Integer maxWeight,
                      @RequestParam(value = "hair_color", required = false) Color hairColor,
                      @RequestParam(value = "eye_color", required = false) Color eyeColor) {
        int provided = (maxWeight != null ? 1 : 0) + (hairColor != null ? 1 : 0) + (eyeColor != null ? 1 : 0);
        if (provided > 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specify only one filter parameter");
        }

        if (maxWeight != null) {
            return personService.countWeightLessThan(maxWeight);
        }
        if (hairColor != null) {
            return personService.countByHairColor(hairColor);
        }
        if (eyeColor != null) {
            return personService.countByEyeColor(eyeColor);
        }

        return personService.countAll();
    }

    @GetMapping("/stats/hair/share")
    public double hairShare(@RequestParam("color") Color color) {
        return personService.hairColorShare(color);
    }

    @GetMapping("/stats/eye/share")
    public double eyeShare(@RequestParam("color") Color color) {
        return personService.eyeColorShare(color);
    }
}
