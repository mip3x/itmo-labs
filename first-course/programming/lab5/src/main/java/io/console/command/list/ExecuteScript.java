package io.console.command.list;

import io.console.ConsoleManager;
import io.console.InformationStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Executes script
 */
public class ExecuteScript extends Command {
    private static final Logger executeScriptCommandLogger = LogManager.getLogger();
    public ExecuteScript() {
        super("execute_script file_name", "считать и исполнить скрипт из указанного файла");
    }

    @Override
    public String execute() {
        String filePath;
        try {
            filePath = InformationStorage.getReceivedArguments().get(0);
            File file = new File(filePath);

            if (!file.exists()){
                executeScriptCommandLogger.trace("File does not exist!");
                throw new Exception();
            }

            try {
                FileReader fileReader = new FileReader(filePath);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                String line;
                List<String> commandsToExecute = new ArrayList<>();

                while ((line = bufferedReader.readLine()) != null) {
                    commandsToExecute.add(line.trim());
                }

                commandsToExecute.forEach(ConsoleManager.getInstance()::process);
                executeScriptCommandLogger.trace("Commands from script have been executed");
                return "";
            }
            catch (IOException exception) {
                executeScriptCommandLogger.error("Error reading file: insufficient access rights!");
                return "Ошибка при чтении файла! Недостаточно прав доступа!";
            }

        }
        catch (Exception exception) {
            executeScriptCommandLogger.error("Enter valid path to the file!");
            return "Введите валидный путь к файлу!";
        }
    }
}
