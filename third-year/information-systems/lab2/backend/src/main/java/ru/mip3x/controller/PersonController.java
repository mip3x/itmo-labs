package ru.mip3x.controller;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ru.mip3x.model.Color;
import ru.mip3x.dto.PersonResponse;
import ru.mip3x.mapper.PersonMapper;
import ru.mip3x.model.Person;
import ru.mip3x.service.PersonService;
import ru.mip3x.service.ImportService;
import ru.mip3x.dto.imports.ImportOperationDto;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/people")
@AllArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final ImportService importService;

    @GetMapping("/paged")
    public Page<PersonResponse> findAllPersonsPaged(Pageable pageable,
                                                    @RequestParam(value = "birthday_before", required = false) String birthdayBefore) {
        if (birthdayBefore != null && !birthdayBefore.isBlank()) {
            try {
                ZonedDateTime date = ZonedDateTime.parse(birthdayBefore);
                return personService.findBirthdayBefore(date, pageable)
                                    .map(PersonMapper::toDTO);
            } catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format, expected ISO date", e);
            }
        }

        return personService.findAllPersons(pageable)
                            .map(PersonMapper::toDTO);
    }

    @PostMapping
    public ResponseEntity<PersonResponse> createPerson(@Valid @RequestBody Person person) {
        Person savedPerson = personService.savePerson(person);
        return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(PersonMapper.toDTO(savedPerson));
    }

    @GetMapping("/{id}")
    public PersonResponse findById(@PathVariable int id) {
        return PersonMapper.toDTO(personService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PersonResponse> updatePerson(@PathVariable int id, @Valid @RequestBody Person person) {
        Person updatedPerson = personService.updatePerson(id, person);
        return ResponseEntity.ok(PersonMapper.toDTO(updatedPerson));
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
        return personService.countFiltered(maxWeight, hairColor, eyeColor);
    }

    @GetMapping("/stats/hair/share")
    public double hairShare(@RequestParam("color") Color color) {
        return personService.hairColorShare(color);
    }

    @GetMapping("/stats/eye/share")
    public double eyeShare(@RequestParam("color") Color color) {
        return personService.eyeColorShare(color);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportOperationDto> importPeople(@RequestPart("file") MultipartFile file) {
        ImportOperationDto response = importService.importFromFile(file);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/import/history")
    public Page<ImportOperationDto> importHistory(Pageable pageable) {
        return importService.history(pageable);
    }
}
