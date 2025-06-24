package ru.mip3x.lab4.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PointValidationServiceTest {
    private final PointValidationService svc = new PointValidationService();

    @Test
    void insideRectangle() {
        assertTrue(svc.isPointInArea(1.0, 0.5, 2.0));
    }

    @Test
    void outsideAllAreas() {
        assertFalse(svc.isPointInArea(3.0, 3.0, 2.0));
    }

    @Test
    void onCircleBoundary() {
        assertTrue(svc.isPointInArea(2.0, 0.0, 2.0));
    }
}

