package console.command.list;

import console.InformationStorage;

public class Exit extends Command {
    public Exit() {
        super("exit", "Выйти из программы");
    }

    @Override
    public String execute() {
        System.exit(0);
        return "Выход из программы";
    }
}
