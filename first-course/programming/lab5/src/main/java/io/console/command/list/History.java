package io.console.command.list;

import io.console.InformationStorage;

import java.util.List;
import java.util.stream.Collectors;

public class History extends Command {
    public History() {
        super("history", "Вывести историю вызовов");
    }

    @Override
    public String execute() {
        return InformationStorage.getHistory().stream().limit(InformationStorage.getHistorySize())
                .map(command -> String.format("%s%n", command.getName())).collect(Collectors.joining());
    }
}
