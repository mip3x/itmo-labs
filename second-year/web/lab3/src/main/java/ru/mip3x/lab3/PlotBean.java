package ru.mip3x.lab3;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.File;

@Named("plotBean")
@ApplicationScoped
public class PlotBean implements Serializable {
    @Setter
    @Getter
    private double x;
    @Setter
    @Getter
    private double y;
    @Setter
    @Getter
    private double radius = 1.0;
    private boolean pointInArea;
    @Inject
    private ServletContext servletContext;

    public InputStream getImage() {
        try {
            int width = 500;
            int height = 500;
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufferedImage.createGraphics();

            // Anti-aliasing for smoother graphics
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background
            int pixelSize = 10;
            int centerX = width / 2;
            int centerY = height / 2;
            int radius = (int) (1.15 * Math.min(width, height) / 2); // Adjust to fit within the square

            g2d.setColor(new Color(255, 255, 255));
            for (int y = -radius; y <= radius; y += pixelSize) {
                for (int x = -radius; x <= radius; x += pixelSize) {
                    if (x * x + y * y <= radius * radius) {
                        g2d.fillRect(centerX + x, centerY + y, pixelSize, pixelSize);
                    }
                }
            }

            // Load custom font
            Font customFont;
            try {
                String fontPath = servletContext.getRealPath("/static/assets/assets/Monocraft.otf");
                customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
                customFont = customFont.deriveFont(16f);
            } catch (Exception e) {
                customFont = new Font("Arial", Font.PLAIN, 16);
            }
            g2d.setFont(customFont);

            // Axes
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(width / 2, 0, width / 2, height); // Oy
            g2d.drawLine(0, height / 2, width, height / 2); // Ox

            // Arrowheads for axes
            g2d.fillPolygon(new int[]{width / 2, width / 2 - 5, width / 2 + 5}, new int[]{0, 10, 10}, 3); // Oy arrow
            g2d.fillPolygon(new int[]{width, width - 10, width - 10}, new int[]{height / 2, height / 2 - 5, height / 2 + 5}, 3); // Ox arrow

            // Labels
            int rPixels = (int) (0.75 * Math.min(width, height) / 2);
            g2d.drawString("R", width / 2 + 5, height / 2 - rPixels + 5);
            g2d.drawString("-R", width / 2 + 5, height / 2 + rPixels - 5);
            g2d.drawString("R", width / 2 + rPixels - 10, height / 2 + 15);
            g2d.drawString("-R", width / 2 - rPixels - 20, height / 2 + 15);
            g2d.drawString("R/2", width / 2 + 5, height / 2 - rPixels / 2 + 5);
            g2d.drawString("-R/2", width / 2 + 5, height / 2 + rPixels / 2 - 5);
            g2d.drawString("R/2", width / 2 + rPixels / 2 - 10, height / 2 + 15);
            g2d.drawString("-R/2", width / 2 - rPixels / 2 - 25, height / 2 + 15);

            // Axis labels
            g2d.drawString("X", width - 20, height / 2 - 10);
            g2d.drawString("Y", width / 2 + 10, 20);

            // Shapes (filled region)
            g2d.setColor(new Color(0, 0, 255, 150));

            // Rectangle
            g2d.fillRect(width / 2, height / 2 - rPixels / 2, rPixels, rPixels / 2);

            // Triangle
            Polygon triangle = new Polygon();
            triangle.addPoint(width / 2, height / 2);
            triangle.addPoint(width / 2 - rPixels, height / 2);
            triangle.addPoint(width / 2, height / 2 - rPixels / 2);
            g2d.fillPolygon(triangle);

            // Quarter-circle
            g2d.fillArc(width / 2 - rPixels / 2, height / 2 - rPixels / 2, rPixels, rPixels, 0, -90);

            // Example dot
            int pointX = (int) (width / 2 + x * (rPixels / radius));
            int pointY = (int) (height / 2 - y * (rPixels / radius));
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
        int width = 500;
        int height = 500;

        double rPixels = 0.75 * Math.min(width, height) / 2;
        x = (clickX - width / 2) / (rPixels / radius);
        y = (height / 2 - clickY) / (rPixels / radius);
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
}
