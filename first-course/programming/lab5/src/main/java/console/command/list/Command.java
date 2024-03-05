package console.command.list; 

import console.command.InformationManager;

public abstract class Command {
    private String name;
    private String description;
    protected InformationManager informationManager;

    public Command(String name, String description, InformationManager informationManager) {
        this.name = name;
        this.description = description;
        this.informationManager = informationManager;
    }

    public abstract String execute();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
