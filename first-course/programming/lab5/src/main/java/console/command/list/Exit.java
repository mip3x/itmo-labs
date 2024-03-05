package console.command.list;

import console.command.InformationManager;

import java.util.List;

public class Exit extends Command {
    public Exit(String name, String description, InformationManager informationManager) {
        super(name, description, informationManager);
    }

    @Override
    public String execute() {
        System.exit(0);
        return "Выход из программы";
    }
}
