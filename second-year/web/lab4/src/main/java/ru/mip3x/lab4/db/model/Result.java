package ru.mip3x.lab4.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity class representing the result of a point check operation
 * Each result belongs to a specific user and contains information
 * about the point, radius, result of the check, and timing metadata
 */
@Entity
@Table(name = "results")
@Getter
@Setter
public class Result {
    /**
     * The unique identifier for the result
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The X coordinate of the checked point
     */
    @Column(name = "x", nullable = false)
    private Double x;

    /**
     * The Y coordinate of the checked point
     */
    @Column(name = "y", nullable = false)
    private Double y;

    /**
     * The radius used for area checking
     */
    @Column(name = "r", nullable = false)
    private Double r;

    /**
     * The result of the area check (true if inside, false otherwise)
     */
    @Column(name = "result", nullable = false)
    private boolean result;

     /**
     * The time when the check request was sent
     */
    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;

    /**
     * The execution time of the check operation in milliseconds
     */
    @Column(name = "execution_time", nullable = false)
    private long executionTime;

    /**
     * The user who performed the check
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Default constructor required by JPA
     */
    public Result() {}

    /**
     * Constructs a new result with the given parameters
     *
     * @param x             X coordinate
     * @param y             Y coordinate
     * @param r             Radius
     * @param result        Check result
     * @param sendTime      Time of sending
     * @param executionTime Execution time in milliseconds
     * @param user          User who performed the check
     */
    public Result(Double x, Double y, Double r, boolean result, LocalDateTime sendTime, long executionTime, User user) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.result = result;
        this.sendTime = sendTime;
        this.executionTime = executionTime;
        this.user = user;
    }
}
