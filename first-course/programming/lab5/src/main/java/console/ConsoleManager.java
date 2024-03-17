package console;

import collection.data.StudyGroup;
import console.command.list.Command;
import exception.InvalidInputException;
import exception.InvalidTypeCastException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.function.Function;

public class ConsoleManager {
    private static ConsoleManager instance = null;
    private final ConsoleHandler consoleHandler;
    private final InformationStorage informationStorage;

    public static ConsoleManager getInstance(InformationStorage informationStorage) {
        if (instance == null) instance = new ConsoleManager(informationStorage);
        return instance;
    }

    public ConsoleManager(InformationStorage informationStorage) {
        this.informationStorage = informationStorage;
        this.consoleHandler = new ConsoleHandler();
    }

    public void init() {
        String inputLine;

        while ((inputLine = consoleHandler.receive(true)) != null) {
            process(inputLine);
        }
    }

    private void process(String inputLine) {
        try {
            String[] tokens = inputLine.trim().split(" ");

            if (tokens[0].isBlank()) return;

            for (Command command: InformationStorage.getCommandsList()) {
                if (command.getName().equals(tokens[0])) {

                    if (InformationStorage.getCommandsRequestingInput().contains(command)) {
                        informationStorage.setReceivedStudyGroup(inputElement());
                    }

                    String output = command.execute();
                    informationStorage.addToHistory(command);
                    consoleHandler.sendWithNewLine(output);
                    return;
                }
            }
            throw new InvalidInputException("Команда не распознана!");
        } catch (Exception exception) {
            consoleHandler.sendWithNewLine(exception.getMessage());
        }
    }

    private StudyGroup inputElement() throws NoSuchMethodException {
        StudyGroup studyGroup = new StudyGroup();

        while (InputField("имя",
                studyGroup,
                studyGroup.getClass().getMethod("setName", String.class),
                String::toString,
                String.class));

        while (InputField("количество студентов",
                studyGroup,
                studyGroup.getClass().getMethod("setStudentsCount", Long.class),
                Long::parseLong,
                Long.class));

        return studyGroup;
    }

    private <T> boolean InputField(String fieldName, StudyGroup studyGroup, Method method, Function<String, T> converter, Class<T> type) {
        try {
            String userInput = consoleHandler.receive(MessageFormat.format("> Введите {0}", fieldName));
            method.invoke(studyGroup, castField(userInput, converter, type));
            return false;
        }
        catch (InvocationTargetException exception) {
            consoleHandler.sendWithNewLine(exception.getCause().getMessage());
            return true;
        }
        catch (Exception exception) {
            consoleHandler.sendWithNewLine(exception.getMessage());
            return true;
        }
    }

    private <T> T castField(String userInput, Function<String, T> converter, Class<T> type) throws InvalidTypeCastException {
        try {
            return converter.apply(userInput);
        }
        catch (Exception exception) {
            throw new InvalidTypeCastException(
                    MessageFormat.format("Неверный ввод! Поле должно быть типа {0}", type.getSimpleName()));
        }
    }
}