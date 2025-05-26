package ru.mip3x.lab4.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Entity class representing an application user
 * Each user has a username, password hash, and a list of result entries
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    /**
     * The unique identifier of the user
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The list of results associated with this user
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Result> results;

    /**
     * The unique username used for login
     */
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /**
     * The hashed password stored for authentication
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Default constructor required by JPA
     */
    public User() {}

    /**
     * Constructs a new user with the specified username and hashed password
     *
     * @param username     the username
     * @param passwordHash the hashed password
     */
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }
}
