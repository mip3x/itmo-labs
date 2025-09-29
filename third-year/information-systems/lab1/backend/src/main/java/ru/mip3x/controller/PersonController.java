package ru.mip3x.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ru.mip3x.dto.PersonDTO;
import ru.mip3x.mapper.PersonMapper;
import ru.mip3x.model.Person;
import ru.mip3x.service.PersonService;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/persons")
@AllArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public List<PersonDTO> findAllPersons() {
        return personService.findAllPersons()
                            .stream()
                            .map(PersonMapper::toDTO)
                            .toList();
    }

    @PostMapping
    public ResponseEntity<PersonDTO> createPerson(@Valid @RequestBody Person person) {
        Person savedPerson = personService.savePerson(person);
        return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(PersonMapper.toDTO(savedPerson));
    }

    @GetMapping("/{id}")
    public PersonDTO findById(@PathVariable int id) {
        return PersonMapper.toDTO(personService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable int id, @Valid @RequestBody Person person) {
        Person updatedPerson = personService.updatePerson(id, person);
        return ResponseEntity.ok(PersonMapper.toDTO(updatedPerson));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable int id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }
}
