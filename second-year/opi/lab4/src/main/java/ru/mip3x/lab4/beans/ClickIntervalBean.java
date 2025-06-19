package ru.mip3x.lab4.beans;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class ClickIntervalBean implements ClickIntervalMBean {
    private volatile long lastClickTime = -1;
    private final AtomicLong totalIntervals = new AtomicLong();
    private final AtomicLong countIntervals = new AtomicLong();

    @Override
    public double getAverageIntervalMillis() {
        long intervals = countIntervals.get();
        return intervals == 0 ? 0 : (double) totalIntervals.get() / intervals;
    }

    public void recordClick() {
        long now = System.currentTimeMillis();
        if (lastClickTime > 0) {
            long delta = now - lastClickTime;
            totalIntervals.addAndGet(delta);
            countIntervals.incrementAndGet();
        }
        lastClickTime = now;

        System.out.println("AverageIntervalMillis: " + getAverageIntervalMillis());
    }
}
