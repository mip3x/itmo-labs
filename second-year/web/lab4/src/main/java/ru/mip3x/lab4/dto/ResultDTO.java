package ru.mip3x.lab4.dto;

import lombok.Getter;
import ru.mip3x.lab4.db.model.Result;

import java.time.LocalDateTime;

@Getter
public class ResultDTO {
    private final Double x;
    private final Double y;
    private final Double r;
    private final boolean result;
    private final LocalDateTime sendTime;
    private final long executionTime;

    public ResultDTO(Result result) {
        this.x = result.getX();
        this.y = result.getY();
        this.r = result.getR();
        this.result = result.isResult();
        this.sendTime = result.getSendTime();
        this.executionTime = result.getExecutionTime();
    }
}
