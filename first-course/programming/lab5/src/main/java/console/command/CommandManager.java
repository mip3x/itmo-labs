package console.command;

public class CommandManager {
    public CommandValidator commandValidator;

    public CommandManager(CommandValidator commandValidator) {
        this.commandValidator = commandValidator;
    }
}
