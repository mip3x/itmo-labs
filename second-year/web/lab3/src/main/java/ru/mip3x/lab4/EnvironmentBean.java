package ru.mip3x.lab3;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@ApplicationScoped
public class EnvironmentBean implements Serializable {
    private String fullName;
    private String groupNumber;
    private String variant;

    public EnvironmentBean() {
        this.fullName = System.getenv("USER_FULL_NAME");
        this.groupNumber = System.getenv("USER_GROUP");
        this.variant = System.getenv("USER_VARIANT");

        if (fullName == null) fullName = "Иванов Иван Иванович";
        if (groupNumber == null) groupNumber = "P1488";
        if (variant == null) variant = "666";
    }

    public String getFullName() {
        return fullName;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public String getVariant() {
        return variant;
    }
}
