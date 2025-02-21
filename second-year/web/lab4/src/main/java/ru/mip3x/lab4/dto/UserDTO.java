package ru.mip3x.lab4.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    @JsonbProperty("username")
    private String username;

    @JsonbProperty("password")
    private String password;

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
