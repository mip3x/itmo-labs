package console.command.list;

import console.command.InformationManager;

import java.util.List;

public class Help extends Command {
    private final List<Command> commandsList;

    public Help(String name, String description, InformationManager informationManager) {
        super(name, description, informationManager);
        this.commandsList = informationManager.getCommandsList();
    }

    @Override
    public String execute() {
        String result = "";
        for (Command command: commandsList) {
            result += command.getName() + ": " + command.getDescription() + "\n";
        }
        return result.substring(0, result.length() - 1);
    } 
}
