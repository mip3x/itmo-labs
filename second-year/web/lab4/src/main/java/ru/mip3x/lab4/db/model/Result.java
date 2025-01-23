package ru.mip3x.lab4.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Getter
@Setter
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "x", nullable = false)
    private Double x;

    @Column(name = "y", nullable = false)
    private Double y;

    @Column(name = "r", nullable = false)
    private Double r;

    @Column(name = "result", nullable = false)
    private boolean result;

    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;

    @Column(name = "execution_time", nullable = false)
    private long executionTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Result() {}

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