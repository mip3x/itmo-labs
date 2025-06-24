package ru.mip3x.lab4.dto;

import lombok.Getter;
import ru.mip3x.lab4.db.model.Result;

import java.time.LocalDateTime;

/**
 * Data Transfer Object that represents the result of a point check
 * Used for transferring data from the backend to the client
 */
@Getter
public class ResultDTO {
    /**
     * X coordinate of the point
     */
    private final Double x;

    /**
     * Y coordinate of the point
     */
    private final Double y;

    /**
     * Radius value used for the check
     */
    private final Double r;

    /**
     * Result of the area check (true if the point is inside the area)
     */
    private final boolean result;

    /**
     * Timestamp when the result was generated
     */
    private final LocalDateTime sendTime;

    /**
     * Time taken to process the request, in milliseconds
     */
    private final long executionTime;

    /**
     * Constructs a ResultDTO from a Result entity
     *
     * @param result the Result entity containing point and check details
     */
    public ResultDTO(Result result) {
        this.x = result.getX();
        this.y = result.getY();
        this.r = result.getR();
        this.result = result.isResult();
        this.sendTime = result.getSendTime();
        this.executionTime = result.getExecutionTime();
    }
}
