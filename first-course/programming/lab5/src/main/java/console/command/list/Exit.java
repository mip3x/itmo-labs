package console.command.list;

import java.util.List;

public class Exit extends Command {
    public Exit(String name, String description) {
        super(name, description);
    }

    @Override
    public String execute() {
        System.exit(0);
        return "Выход из программы";
    }
}
