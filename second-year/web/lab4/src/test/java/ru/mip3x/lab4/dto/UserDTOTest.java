package ru.mip3x.lab4.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {
    @Test
    void gettersAndSetters() {
        UserDTO dto = new UserDTO();
        dto.setUsername("alice");
        dto.setPassword("secret");

        assertEquals("alice", dto.getUsername());
        assertEquals("secret", dto.getPassword());

        UserDTO dto2 = new UserDTO("bob","p@ss");
        assertEquals("bob", dto2.getUsername());
        assertEquals("p@ss", dto2.getPassword());
    }
}
