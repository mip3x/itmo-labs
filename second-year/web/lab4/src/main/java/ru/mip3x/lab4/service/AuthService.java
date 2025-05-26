package ru.mip3x.lab4.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;
import ru.mip3x.lab4.db.model.User;
import ru.mip3x.lab4.db.repository.UserRepository;
import ru.mip3x.lab4.dto.UserDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service responsible for user authentication and session management
 */
@ApplicationScoped
public class AuthService {
    /** Repository for interacting with user entities in the database */
    private final UserRepository userRepository = new UserRepository();

     /**
     * In-memory map of session IDs to usernames
     */
    private final Map<String, String> sessions = new HashMap<>();

    /**
     * Registers a new user with a hashed password and creates a session
     *
     * @param userDTO user data including username and password
     * @return generated session ID
     * @throws IllegalArgumentException if username already exists
     */
    public String register(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()) != null)
            throw new IllegalArgumentException("Username already exists");

        String hashedPassword = BCrypt.hashpw(userDTO.getPassword(), BCrypt.gensalt());
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPasswordHash(hashedPassword);

        userRepository.save(user);

        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, userDTO.getUsername());
        return sessionId;
    }

     /**
     * Logs in a user if credentials are correct and creates a session
     *
     * @param username user's username
     * @param password user's password
     * @return generated session ID
     * @throws IllegalArgumentException if credentials are invalid
     */
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !BCrypt.checkpw(password, user.getPasswordHash()))
            throw new IllegalArgumentException("Invalid username or password");

        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, username);
        return sessionId;
    }

    /**
     * Retrieves the username associated with the given session ID
     *
     * @param sessionId session ID
     * @return the username, or {@code null} if not found
     */
    public String getUsernameFromSession(String sessionId) {
        System.out.println(sessions);
        System.out.println(sessions.get(sessionId));
        return sessions.get(sessionId);
    }

     /**
     * Logs out the user by removing the session
     *
     * @param sessionId session ID to remove
     */
    public void logout(String sessionId) {
        sessions.remove(sessionId);
    }
}
