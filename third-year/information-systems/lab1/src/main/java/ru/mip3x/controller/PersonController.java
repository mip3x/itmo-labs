package ru.mip3x.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import ru.mip3x.dto.PersonDTO;
import ru.mip3x.mapper.PersonMapper;
import ru.mip3x.model.Person;
import ru.mip3x.service.PersonService;
import org.springframework.web.bind.annotation.PostMapping;


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
    public String savePerson(@RequestBody Person person) {
        personService.savePerson(person);
        return "Person successfully saved";
    }

    @GetMapping("/{id}")
    public PersonDTO findById(@PathVariable int id) {
        return PersonMapper.toDTO(personService.findById(id));
    }

    @PutMapping("/{id}")
    public PersonDTO updatePerson(@PathVariable int id, @RequestBody Person person) {
        return PersonMapper.toDTO(personService.updatePerson(id, person));
    }

    @DeleteMapping("/{id}")
    public String deletePerson(@PathVariable int id) {
        personService.deletePerson(id);
        return "Person successfully deleted";
    }
}
