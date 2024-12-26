package ru.mip3x.lab3;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

@Named("plotBean")
@ApplicationScoped
public class PlotBean implements Serializable {
    @Setter
    @Getter
    private Double x = 0.0;
    @Setter
    @Getter
    private Double y = 0.0;
    @Setter
    @Getter
    private Double radius = null;
    private boolean pointInArea;
    @Inject
    private ServletContext servletContext;

    public StreamedContent getImage() {
        try {
            int width = 500;
            int height = 500;
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufferedImage.createGraphics();

            // Anti-aliasing for smoother graphics
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background circle filling slightly more than the area
            int pixelSize = 10;
            int centerX = width / 2;
            int centerY = height / 2;
            int backgroundRadiusPixels = (int) (1.10 * Math.min(width, height) / 2);

            g2d.setColor(new Color(255, 255, 255));
            for (int y = -backgroundRadiusPixels; y <= backgroundRadiusPixels; y += pixelSize) {
                for (int x = -backgroundRadiusPixels; x <= backgroundRadiusPixels; x += pixelSize) {
                    if (x * x + y * y <= backgroundRadiusPixels * backgroundRadiusPixels) {
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
            int graphRadiusPixels = (int) (0.75 * Math.min(width, height) / 2);
            if (radius == null) {
                g2d.drawString("R", width / 2 + 5, height / 2 - graphRadiusPixels + 5);
                g2d.drawString("-R", width / 2 + 5, height / 2 + graphRadiusPixels - 5);
                g2d.drawString("R", width / 2 + graphRadiusPixels - 10, height / 2 + 15);
                g2d.drawString("-R", width / 2 - graphRadiusPixels - 20, height / 2 + 15);

                g2d.drawString("R/2", width / 2 + 5, height / 2 - graphRadiusPixels / 2 + 5);
                g2d.drawString("-R/2", width / 2 + 5, height / 2 + graphRadiusPixels / 2 - 5);
                g2d.drawString("R/2", width / 2 + graphRadiusPixels / 2 - 10, height / 2 + 15);
                g2d.drawString("-R/2", width / 2 - graphRadiusPixels / 2 - 25, height / 2 + 15);
            } else {
                g2d.drawString(String.format("%.1f", radius), width / 2 + 5, height / 2 - graphRadiusPixels + 5);
                g2d.drawString(String.format("-%.1f", radius), width / 2 + 5, height / 2 + graphRadiusPixels - 5);
                g2d.drawString(String.format("%.1f", radius), width / 2 + graphRadiusPixels - 10, height / 2 + 15);
                g2d.drawString(String.format("-%.1f", radius), width / 2 - graphRadiusPixels - 20, height / 2 + 15);

                g2d.drawString(String.format("%.1f", radius / 2), width / 2 + 5, height / 2 - graphRadiusPixels / 2 + 5);
                g2d.drawString(String.format("-%.1f", radius / 2), width / 2 + 5, height / 2 + graphRadiusPixels / 2 - 5);
                g2d.drawString(String.format("%.1f", radius / 2), width / 2 + graphRadiusPixels / 2 - 10, height / 2 + 15);
                g2d.drawString(String.format("-%.1f", radius / 2), width / 2 - graphRadiusPixels / 2 - 25, height / 2 + 15);
            }

            // Graph shapes (triangle, square, and quarter-circle)
            g2d.setColor(new Color(0, 0, 255, 150));

            // Rectangle
            g2d.fillRect(centerX, centerY - graphRadiusPixels / 2, graphRadiusPixels, graphRadiusPixels / 2);

            // Triangle
            Polygon triangle = new Polygon();
            triangle.addPoint(centerX, centerY);
            triangle.addPoint(centerX - graphRadiusPixels, centerY);
            triangle.addPoint(centerX, centerY - graphRadiusPixels / 2);
            g2d.fillPolygon(triangle);

            // Quarter-circle
            g2d.fillArc(centerX - graphRadiusPixels / 2, centerY - graphRadiusPixels / 2, graphRadiusPixels, graphRadiusPixels, 0, -90);

            // Example dot
            int pointX = (int) (width / 2 + x * (graphRadiusPixels / (radius == null ? 1.0 : radius)));
            int pointY = (int) (height / 2 - y * (graphRadiusPixels / (radius == null ? 1.0 : radius)));
            g2d.setColor(pointInArea ? Color.GREEN : Color.RED);
            g2d.fillOval(pointX - 5, pointY - 5, 10, 10);

            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());

            return DefaultStreamedContent.builder()
                    .contentType("image/png")
                    .stream(() -> is)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void handleClick(int clickX, int clickY) {
        int width = 500;
        int height = 500;

        double graphRadiusPixels = 0.75 * Math.min(width, height) / 2;
        x = (clickX - width / 2) / (graphRadiusPixels / (radius == null ? 1.0 : radius));
        y = (height / 2 - clickY) / (graphRadiusPixels / (radius == null ? 1.0 : radius));
        checkPoint();
    }

    public void checkPoint() {
        if (radius == null) {
            pointInArea = false;
            return;
        }
        pointInArea = (x >= 0 && y >= 0 && y <= radius - x)
                || (x <= 0 && y >= 0 && x >= -radius / 2 && y <= radius)
                || (x >= 0 && y <= 0 && x * x + y * y <= (radius / 2) * (radius / 2));
    }

    public void updateRadius() {
        if (radius != null) {
            radius = Math.round(radius * 10) / 10.0;
        }
        checkPoint();
    }
}
