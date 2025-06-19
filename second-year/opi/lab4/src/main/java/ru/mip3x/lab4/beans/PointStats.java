package ru.mip3x.lab4.beans;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import ru.mip3x.lab4.dto.PointDTO;
import ru.mip3x.lab4.service.PointValidationService;
import jakarta.inject.Inject;

@ApplicationScoped
public class PointStats extends NotificationBroadcasterSupport implements PointStatsMBean {
    private final AtomicLong totalPoints = new AtomicLong();
    private final AtomicLong outOfBounds = new AtomicLong();
    private int sequenceNumber = 1;

    @Inject
    private PointValidationService validationService;

    @Override
    public long getTotalPoints() {
        return totalPoints.get();
    }

    @Override
    public long getOutOfBoundsPoints() {
        return outOfBounds.get();
    }

    public void recordPoint(PointDTO dto) {
        double x = dto.getX();
        double y = dto.getY();
        double r = dto.getRadius();

        totalPoints.incrementAndGet();
        if (!validationService.isPointInArea(x, y, r)) {
            outOfBounds.incrementAndGet();
            Notification notification = new Notification(
                "ru.mip3x.point.outOfBounds",
                "lab4.beans:type=PointStats",
                sequenceNumber++,
                System.currentTimeMillis(),
                "Point out of bounds: (" + x + ", " + y + "), r=" + r
            );
            sendNotification(notification);

            System.out.println("PointStatsBean Notification: " + notification.getType() + " - " + notification.getMessage());
        }
    }
}

