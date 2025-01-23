package ru.mip3x.lab4;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Startup
@Singleton
@Named
public class TimeBean implements Serializable {
    private String currentDateTime;

    public TimeBean() {
        updateTime();
    }

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void updateTime() {
        this.currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }
}
