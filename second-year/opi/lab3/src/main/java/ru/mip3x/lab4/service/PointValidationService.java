package ru.mip3x.lab4.service;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Service for validating whether a point falls within a defined area
 */
@ApplicationScoped
public class PointValidationService {
    /**
     * Checks if a point (x, y) with a given radius r falls inside a composite area
     * The area consists of:
     * <ul>
     *     <li>A rectangle in the first quadrant: x in [0, r], y in [0, r/2]</li>
     *     <li>A triangle in the second quadrant: x ≤ 0, y ≥ 0, y ≤ r/2 + x/2</li>
     *     <li>A quarter-circle in the fourth quadrant: x ≥ 0, y ≤ 0, x² + y² ≤ r²</li>
     * </ul>
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param r the radius/parameter defining the area
     * @return {@code true} if the point lies within the area; {@code false} otherwise
     */
    public boolean isPointInArea(double x, double y, double r) {
        boolean inRectangle = (x >= 0 && x <= r && y >= 0 && y <= r / 2);
        boolean inTriangle = (x <= 0 && y >= 0 && y <= r / 2 + x / 2);
        boolean inCircle = (x >= 0 && y <= 0 && (x * x + y * y <= r * r));

        return inRectangle || inTriangle || inCircle;
    }
}
