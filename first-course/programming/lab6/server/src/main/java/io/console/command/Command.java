package io.console.command;

import collection.CollectionManager;

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

    public abstract String execute(CollectionManager collectionManager);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
