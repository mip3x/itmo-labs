package io.console.command.list;

import io.console.InformationStorage;

import java.util.stream.Collectors;

/**
 * Returns history of called commands
 */
public class History extends Command {
    public History() {
        super("history", "Вывести историю вызовов");
    }

    @Override
    public String execute() {
        String result = InformationStorage.getHistory().stream()
                .limit(InformationStorage.getHistorySize())
                .map(command -> String.format("%s%n", command.getName()))
                .collect(Collectors.joining());
        return result.substring(0, result.length() - 1);
    }
}
