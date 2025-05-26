package ru.mip3x.lab4.beans;

import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A bean that provides the current date and time
 * Used for displaying a timestamp in the UI
 */
@Startup
@ApplicationScoped
@Named
public class TimeBean implements Serializable {
    private String currentDateTime;

    /**
     * Initializes the time bean and sets the current time
     */
    public TimeBean() {
        updateTime();
    }

     /**
     * Returns the formatted current date and time
     *
     * @return a formatted timestamp
     */
    public String getCurrentDateTime() {
        return currentDateTime;
    }

     /**
     * Updates the current time to now
     */
    public void updateTime() {
        this.currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }
}
