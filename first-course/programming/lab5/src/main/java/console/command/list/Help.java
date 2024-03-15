package console.command.list;

import console.InformationStorage;

import java.util.stream.Collectors;

public class Help extends Command {
    public Help() {
        super("help", "Вывести список всех команд");
    }

    @Override
    public String execute() {
        String result = InformationStorage.getCommandsList().stream()
                .map(command -> String.format("    %-12s%-4s%s%n", command.getName(), "->", command.getDescription()))
                .collect(Collectors.joining());
        return result.substring(0, result.length() - 1);
    }
}