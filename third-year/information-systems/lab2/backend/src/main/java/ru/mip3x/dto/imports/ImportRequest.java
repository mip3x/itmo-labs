package ru.mip3x.dto.imports;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.mip3x.model.Person;

@Getter
@Setter
public class ImportRequest {
    @NotNull
    @NotEmpty
    @Valid
    private List<Person> persons;
}
