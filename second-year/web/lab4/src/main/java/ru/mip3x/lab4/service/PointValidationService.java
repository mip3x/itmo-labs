package ru.mip3x.lab4.service;

import jakarta.ejb.Singleton;

@Singleton
public class PointValidationService {
    public boolean isPointInArea(double x, double y, double r) {
        boolean inRectangle = (x >= 0 && x <= r && y >= 0 && y <= r / 2);
        boolean inTriangle = (x <= 0 && y >= 0 && y <= r / 2 + x / 2);
        boolean inCircle = (x >= 0 && y <= 0 && (x * x + y * y <= (r / 2) * (r / 2)));

        return inRectangle || inTriangle || inCircle;
    }
}