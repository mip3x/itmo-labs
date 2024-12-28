package ru.mip3x.lab3;

import jakarta.enterprise.context.SessionScoped;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Named("plotBean")
@SessionScoped
public class PlotBean implements Serializable {
    @Getter
    private Double x = 0.0;
    @Getter
    private Double y = 0.0;
    @Getter @Setter
    private Double clickX;
    @Getter @Setter
    private Double clickY;
    @Getter @Setter
    private Double displayX;
    @Getter @Setter
    private Double displayY;
    @Setter @Getter
    private Double radius = null;
    private boolean pointInArea;

    @Getter
    private List<ResultEntry> results = new ArrayList<>();

    @Inject
    private ServletContext servletContext;

    public void setX(Double x) {
        this.x = x;
        this.displayX = x;
        checkPoint();
    }

    public void setY(Double y) {
        this.y = y;
        this.displayY = y;
        checkPoint();
    }

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
            int pointX;
            int pointY;
            if (displayX == null) {
                pointX = (int) ((double) width / 2 + x * (graphRadiusPixels / (radius == null ? 1.0 : radius)));
            } else {
                pointX = (int) ((double) width / 2 + displayX * (graphRadiusPixels / (radius == null ? 1.0 : radius)));
            }
            if (displayY == null) {
                pointY = (int) ((double) height / 2 - y * (graphRadiusPixels / (radius == null ? 1.0 : radius)));
            } else {
                pointY = (int) ((double) height / 2 - displayY * (graphRadiusPixels / (radius == null ? 1.0 : radius)));
            }
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

    public void handleClick() {
        if (radius == null) {
            System.out.println("Radius is null, skipping result update.");
            return;
        }

        System.out.println("Start handleClick");
        int width = 500;
        int height = 500;

        double graphRadiusPixels = 0.75 * width / 2;
        LocalDateTime startTime = LocalDateTime.now();

        displayX = (clickX - (double) width / 2) / (graphRadiusPixels / (radius == null ? 1.0 : radius));
        displayY = ((double) height / 2 - clickY) / (graphRadiusPixels / (radius == null ? 1.0 : radius));

        checkPoint();

        LocalDateTime endTime = LocalDateTime.now();
        long executionTime = ChronoUnit.MILLIS.between(startTime, endTime);

        results.add(new ResultEntry(displayX, displayY, radius, pointInArea, startTime, executionTime));

        System.out.println("x=" + displayX + ", y=" + displayY);
    }

    public void processButtonClick() {
        if (radius == null || x == null || y == null) {
            System.out.println("Invalid parameters for point check.");
            pointInArea = false;
            return;
        }

        LocalDateTime startTime = LocalDateTime.now();

        displayX = x;
        displayY = y;

        checkPoint();

        LocalDateTime endTime = LocalDateTime.now();
        long executionTime = ChronoUnit.MILLIS.between(startTime, endTime);

        results.add(new ResultEntry(displayX, displayY, radius, pointInArea, startTime, executionTime));

        System.out.println("Check point from form: x=" + displayX + ", y=" + displayY + ", pointInArea=" + pointInArea);
    }

    public void checkPoint() {
        if (radius == null || displayX == null || displayY == null) {
            pointInArea = false;
            return;
        }

        boolean inRectangle = (displayX >= 0 && displayX <= radius && displayY >= 0 && displayY <= radius / 2);
        boolean inTriangle = (displayX <= 0 && displayY >= 0 && displayY <= radius / 2 + displayX / 2);
        boolean inCircle = (displayX >= 0 && displayY <= 0 && (displayX * displayX + displayY * displayY <= (radius / 2) * (radius / 2)));

        pointInArea = inRectangle || inTriangle || inCircle;
        System.out.println("Check point: x=" + displayX + ", y=" + displayY + ", inRectangle=" + inRectangle + ", inTriangle=" + inTriangle + ", inCircle=" + inCircle + ", pointInArea=" + pointInArea);
    }

    public void updateRadius() {
        if (radius != null) {
            radius = Math.round(radius * 10) / 10.0;
        }
        checkPoint();
    }

    @Getter
    @Setter
    public static class ResultEntry {
        private Double x;
        private Double y;
        private Double r;
        private boolean result;
        private LocalDateTime sendTime;
        private long executionTime;

        public ResultEntry(Double x, Double y, Double r, boolean result, LocalDateTime sendTime, long executionTime) {
            this.x = x;
            this.y = y;
            this.r = r;
            this.result = result;
            this.sendTime = sendTime;
            this.executionTime = executionTime;
        }
    }
}
