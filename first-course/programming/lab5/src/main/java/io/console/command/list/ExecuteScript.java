package io.console.command.list;

import exception.RecursionException;
import io.console.ConsoleManager;
import io.console.InformationStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExecuteScript extends Command {
    public ExecuteScript() {
        super("execute_script file_name", "считать и исполнить скрипт из указанного файла");
    }

    @Override
    public String execute() {
        String filePath;
        try {
            filePath = InformationStorage.getReceivedArguments().get(0);
            File file = new File(filePath);

            if (!file.exists()) throw new Exception();

            try {
                FileReader fileReader = new FileReader(filePath);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                String line;
                List<String> commandsToExecute = new ArrayList<>();

                while ((line = bufferedReader.readLine()) != null) {
                    commandsToExecute.add(line.trim());
                }

                commandsToExecute.forEach(ConsoleManager.getInstance()::process);
                return "";
            }
            catch (IOException exception) {
                return "Ошибка при чтении файла! Недостаточно прав доступа!";
            }

        }
        catch (Exception exception) {
            return "Введите валидный путь к файлу!";
        }
    }
}
