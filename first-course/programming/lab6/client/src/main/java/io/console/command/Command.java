package io.console.command;

import java.io.Serializable;

/**
 * Abstract command
 */
public abstract class Command implements Serializable {
    private final String name;
    private final String description;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract String execute();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    @Override
    public boolean equals(Object obj) {
        if (this.name.equals(((Command)obj).getName())) return true;
        return false;
    }
}
