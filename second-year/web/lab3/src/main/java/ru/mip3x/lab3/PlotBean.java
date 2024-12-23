package ru.mip3x.lab3;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;

@Named("plotBean")
@ApplicationScoped
public class PlotBean implements Serializable {
    private double x;
    private double y;
    private double radius = 1.0;
    private boolean pointInArea;

    public InputStream getImage() {
        try {
            int width = 400;
            int height = 400;
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufferedImage.createGraphics();

            // background
            g2d.setColor(new Color(255, 240, 220));
            g2d.fillRect(0, 0, width, height);

            // axes
            g2d.setColor(Color.BLACK);
            g2d.drawLine(width / 2, 0, width / 2, height); // Oy
            g2d.drawLine(0, height / 2, width, height / 2); // Ox

            // radius signs
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            int rPixels = (int) (radius * 50);
            g2d.drawString("R", width / 2 + 5, height / 2 - rPixels - 5);
            g2d.drawString("-R", width / 2 + 5, height / 2 + rPixels + 15);
            g2d.drawString("R", width / 2 + rPixels - 5, height / 2 - 5);
            g2d.drawString("-R", width / 2 - rPixels - 20, height / 2 - 5);

            g2d.setColor(Color.BLUE);
            // rectangle
            g2d.fillRect(width / 2, height / 2 - rPixels, rPixels / 2, rPixels);
            // triangle
            Polygon triangle = new Polygon();
            triangle.addPoint(width / 2, height / 2);
            triangle.addPoint(width / 2 - rPixels / 2, height / 2);
            triangle.addPoint(width / 2, height / 2 - rPixels);
            g2d.fillPolygon(triangle);
            // 1/4 of circle
            g2d.fillArc(width / 2 - rPixels / 2, height / 2 - rPixels / 2, rPixels, rPixels, 0, -90);

            // example dot
            int pointX = (int) (width / 2 + x * 50);
            int pointY = (int) (height / 2 - y * 50);
            g2d.setColor(pointInArea ? Color.GREEN : Color.RED);
            g2d.fillOval(pointX - 5, pointY - 5, 10, 10);

            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void handleClick(int clickX, int clickY) {
        int width = 400;
        int height = 400;

        x = (clickX - width / 2) / 50.0;
        y = (height / 2 - clickY) / 50.0;
        checkPoint();
    }

    public void checkPoint() {
        pointInArea = (x >= 0 && y >= 0 && y <= radius - x)
                || (x <= 0 && y >= 0 && x >= -radius / 2 && y <= radius)
                || (x >= 0 && y <= 0 && x * x + y * y <= (radius / 2) * (radius / 2));
    }

    public void updateRadius() {
        checkPoint();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
