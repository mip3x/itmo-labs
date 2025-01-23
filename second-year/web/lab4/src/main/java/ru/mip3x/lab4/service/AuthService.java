package ru.mip3x.lab4.service;

import org.mindrot.jbcrypt.BCrypt;
import ru.mip3x.lab4.db.model.User;
import ru.mip3x.lab4.db.repository.UserRepository;
import ru.mip3x.lab4.dto.UserDTO;

public class AuthService {
    private final UserRepository userRepository = new UserRepository();

    public void register(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()) != null) throw new IllegalArgumentException("Username already exists");

        String hashedPassword = BCrypt.hashpw(userDTO.getPassword(), BCrypt.gensalt());
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPasswordHash(hashedPassword);

        userRepository.save(user);
    }

    public UserDTO login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !BCrypt.checkpw(password, user.getPasswordHash())) throw new IllegalArgumentException("Invalid username or password");

        return new UserDTO(user.getUsername(), null);
    }
}
