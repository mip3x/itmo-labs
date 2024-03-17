package console;

import collection.Invokable;
import collection.data.Coordinates;
import collection.data.FormOfEducation;
import collection.data.Semester;
import collection.data.StudyGroup;
import console.command.list.Command;
import exception.InvalidInputException;
import exception.InvalidTypeCastException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
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

        consoleHandler.sendWithNewLine("* Введите координаты");

        Coordinates coordinates = new Coordinates();

        while (InputField("значение поля 'координата X'",
                coordinates,
                coordinates.getClass().getMethod("setX", Long.class),
                Long::parseLong,
                long.class));

        while (InputField("значение поля 'координата Y'",
                coordinates,
                coordinates.getClass().getMethod("setY", Double.class),
                Double::parseDouble,
                Double.class));

        try {
            studyGroup.setCoordinates(coordinates);
        }
        catch (Exception exception) {
            consoleHandler.sendWithNewLine(exception.getMessage());
        }

        while (InputField("количество студентов на отчисление",
                studyGroup,
                studyGroup.getClass().getMethod("setShouldBeExpelled", Long.class),
                Long::parseLong,
                Long.class
                ));

        while (InputField("значение поля 'форма обучения' (одно из нижеперечисленных):\n"
                + Arrays.toString(FormOfEducation.values()),
                studyGroup,
                studyGroup.getClass().getMethod("setFormOfEducation", FormOfEducation.class),
                FormOfEducation::valueOf,
                FormOfEducation.class
        ));

        while (InputField("значение поля 'семестр обучения' (одно из нижеперечисленных):\n"
                        + Arrays.toString(Semester.values()),
                studyGroup,
                studyGroup.getClass().getMethod("setSemesterEnum", Semester.class),
                Semester::valueOf,
                Semester.class
        ));

        return studyGroup;
    }

    private <T> boolean InputField(String fieldName, Invokable invokable, Method method, Function<String, T> converter, Class<T> type) {
        try {
            String userInput = consoleHandler.receive(MessageFormat.format("> Введите {0}", fieldName));

            if (userInput.isBlank()) {
                method.invoke(invokable, (Object) null);
                return false;
            }

            method.invoke(invokable, castField(userInput, converter, type));
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