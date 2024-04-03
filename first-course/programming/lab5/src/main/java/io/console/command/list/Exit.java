package io.console.command.list;

/**
 * Exits from program
 */
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
