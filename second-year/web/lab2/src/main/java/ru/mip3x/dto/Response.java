package ru.mip3x.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Response {
    private final int x;
    private final double y;
    private final int radius;
    private final boolean hit;
    private final String time;
    private final long executionTime;

    public Response(int x, double y, int radius, boolean hit, String time, long executionTime) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.hit = hit;
        this.time = time;
        this.executionTime = executionTime;
    }

    public int getX() { return x; }
    public double getY() { return y; }
}
