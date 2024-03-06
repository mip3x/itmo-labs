package console.command.list;

import console.command.InformationManager;

import java.util.List;

public class Head extends Command {
    public Head(String name, String description, InformationManager informationManager) {
        super(name, description, informationManager);
    }

    @Override
    public String execute() {
        return "Head was executed";
    } 
}
