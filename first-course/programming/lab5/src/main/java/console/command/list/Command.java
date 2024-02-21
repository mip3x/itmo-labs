package console.command.list; 

public abstract class Command {
    private String name;
    private String description;

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
