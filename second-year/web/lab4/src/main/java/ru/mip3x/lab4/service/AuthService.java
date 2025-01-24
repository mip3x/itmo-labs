package ru.mip3x.lab4.service;

import org.mindrot.jbcrypt.BCrypt;
import ru.mip3x.lab4.db.model.User;
import ru.mip3x.lab4.db.repository.UserRepository;
import ru.mip3x.lab4.dto.UserDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthService {
    private final UserRepository userRepository = new UserRepository();
    private final Map<String, String> sessions = new HashMap<>();

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

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !BCrypt.checkpw(password, user.getPasswordHash()))
            throw new IllegalArgumentException("Invalid username or password");

        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, username);
        return sessionId;
    }

    public String getUsernameFromSession(String sessionId) {
        System.out.println(sessions);
        return sessions.get(sessionId);
    }

    public void logout(String sessionId) {
        sessions.remove(sessionId);
    }
}
