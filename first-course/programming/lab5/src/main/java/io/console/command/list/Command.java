package io.console.command.list;

/**
 * Abstract command
 */
public abstract class Command {
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
}
