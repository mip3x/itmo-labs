package console.command.list;

import java.util.List;

public class Help extends Command {
    private final List<Command> commandsList;

    public Help(String name, String description, List<Command> commandsList) {
        super(name, description);
        this.commandsList = commandsList;
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
