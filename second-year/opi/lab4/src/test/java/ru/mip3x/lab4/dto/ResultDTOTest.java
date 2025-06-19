package ru.mip3x.lab4.dto;

import org.junit.jupiter.api.Test;
import ru.mip3x.lab4.db.model.Result;
import ru.mip3x.lab4.db.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ResultDTOTest {
    @Test
    void mappingFromResult() {
        User user = new User("u", "h");
        Result r = new Result(1.1, 2.2, 3.3, true,
                              LocalDateTime.of(2025,5,27,12,0),
                              123L, user);

        ResultDTO dto = new ResultDTO(r);

        assertEquals(1.1, dto.getX());
        assertEquals(2.2, dto.getY());
        assertEquals(3.3, dto.getR());
        assertTrue(dto.isResult());
        assertEquals(LocalDateTime.of(2025,5,27,12,0), dto.getSendTime());
        assertEquals(123L, dto.getExecutionTime());
    }
}
