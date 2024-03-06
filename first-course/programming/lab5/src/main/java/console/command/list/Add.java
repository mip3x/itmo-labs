package console.command.list;

import console.command.InformationManager;

public class Add extends Command {
    public Add(String name, String description, InformationManager informationManager) {
        super(name, description, informationManager);
    }

    @Override
    public String execute() {
        return "add was executed";
    } 
}
