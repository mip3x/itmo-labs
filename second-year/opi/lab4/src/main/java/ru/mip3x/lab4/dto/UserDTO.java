package ru.mip3x.lab4.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for user credentials
 * Used for transferring username and password in authentication requests
 */
@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
     /**
     * The username of the user
     */
    @JsonbProperty("username")
    private String username;

    /*
     * The password of the user
     */
    @JsonbProperty("password")
    private String password;

    /**
     * Constructs a new UserDTO with the given username and password
     *
     * @param username the user's username
     * @param password the user's password
     */
    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
