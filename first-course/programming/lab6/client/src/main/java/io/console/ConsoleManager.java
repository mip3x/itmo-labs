package io.console;

import collection.data.*;
import exception.ExitException;
import exception.InvalidInputException;
import exception.InvalidTypeCastException;
import exception.RecursionException;
import io.console.command.Command;
import io.console.command.list.ExecuteScript;
import io.console.command.list.RequestingId;
import io.console.command.list.RequestingInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Manages the console
 * Singleton
 */
public class ConsoleManager {
    private static final Logger consoleManagerLogger = LogManager.getLogger();
    private static ConsoleManager instance = null;
    private final ConsoleHandler consoleHandler;
    private final InformationStorage informationStorage;
    private static final int MAX_RECURSION_DEPTH = 5;
    private int currentRecursionDepth = 0;

    /**
     * Get instance of class
     * @return instance of class
     */
    public static ConsoleManager getInstance() {
        if (instance == null) instance = new ConsoleManager();
        return instance;
    }

    public ConsoleManager() {
        this.informationStorage = InformationStorage.getInstance();
        this.consoleHandler = new ConsoleHandler();
    }

    /**
     * Asks for user input
     */
    public void init() {
        String inputLine;

        while ((inputLine = consoleHandler.receive(true)) != null) {
            process(inputLine);
        }
    }

    /**
     * Process the line
     * @param inputLine line inputted by user
     */
    public void process(String inputLine) {
        try {
            String[] tokens = inputLine.trim().split(" ");

            if (tokens[0].isBlank()) return;

            for (Command command: InformationStorage.getCommandsList()) {
                if (command.getName().split(" ")[0].equals(tokens[0])) {

                    if (command instanceof ExecuteScript) {
                       if (currentRecursionDepth > MAX_RECURSION_DEPTH) {
                           currentRecursionDepth = 0;

                           consoleManagerLogger.error("Recursion max depth exceeded!");
                           throw new RecursionException("Глубина рекурсии превысила максимальную глубину рекурсии = " + MAX_RECURSION_DEPTH);
                       }
                       currentRecursionDepth++;
                    };

                    if (tokens.length > 1) informationStorage.setArguments(List.of(tokens).subList(1, tokens.length));

                    if (command instanceof RequestingId) {
                        if (!((RequestingId) command).validateId())
                        {
                            consoleManagerLogger.error("No object with such id!");
                            throw new InvalidInputException("Объекта с данным id нет в коллекции!");
                        }
                    }

                    if (command instanceof RequestingInput) {
                        informationStorage.setReceivedStudyGroup(inputElement());
                    }

                    String output = command.execute();
                    informationStorage.addToHistory(command);
                    consoleManagerLogger.trace("Command added to history");

                    consoleHandler.sendWithNewLine(output);
                    return;
                }
            }
            consoleManagerLogger.error("No such command!");
            throw new InvalidInputException("Command was not recognized!");
        } catch (Exception exception) {
            consoleManagerLogger.error("Error processing input!");
            consoleHandler.sendWithNewLine(exception.getMessage());
        }
    }

    /**
     * Reads element of collection from user
     * @return inputted element of collection
     * @throws ExitException throws in case user stops input of element
     */
    private StudyGroup inputElement() throws ExitException {
        consoleHandler.sendWithNewLine(">*> To exit the object input mode, type 'exit'. The data is not saved! <*<");
        StudyGroup studyGroup = new StudyGroup();

        while (inputField("name",
                studyGroup::setName,
                String::toString,
                String.class
        ));

        while (inputField("quantity of students",
                studyGroup::setStudentsCount,
                Long::parseLong,
                Long.class
        ));

        while (inputField("quantity of students that should be expelled",
                studyGroup::setShouldBeExpelled,
                Long::parseLong,
                Long.class
        ));

        consoleHandler.sendWithNewLine("* Enter the coordinates:");

        Coordinates coordinates = new Coordinates();

        while (inputField("the value of the 'X coordinate' field",
                coordinates::setX,
                Long::parseLong,
                long.class
        ));

        while (inputField("the value of the 'Y coordinate' field",
                coordinates::setY,
                Double::parseDouble,
                Double.class
        ));

        studyGroup.setCoordinates(coordinates);

        while (inputField("значение поля 'форма обучения' (одно из нижеперечисленных)\n"
                        + Arrays.toString(FormOfEducation.values()),
                studyGroup::setFormOfEducation,
                FormOfEducation::valueOf,
                FormOfEducation.class
        ));

        while (inputField("значение поля 'семестр обучения' (одно из нижеперечисленных)\n"
                        + Arrays.toString(Semester.values()),
                studyGroup::setSemester,
                Semester::valueOf,
                Semester.class
        ));

        Person person = new Person();

        consoleHandler.sendWithNewLine("* Введите данные старосты:");

        while (inputField("имя старосты",
                person::setName,
                String::toString,
                String.class
        ));

        while (inputField("вес старосты",
                person::setWeight,
                Long::parseLong,
                Long.class
        ));

        while (inputField("номер паспорта старосты",
                person::setPassportID,
                String::toString,
                String.class
        ));

        while (inputField("цвет глаз старосты (один из нижеперечисленных)\n"
                + Arrays.toString(Color.values()),
                person::setEyeColor,
                Color::valueOf,
                Color.class
        ));

        Location location = new Location();
        List<String> locationInputList = Arrays.asList("да", "нет", "");
        String locationInputText = "* Ввод локации\nВыберите 'да', если необходим (перевод строки воспринимается как 'да') (да/нет)";
        String locationInputAnswer;

        while (!(locationInputList.contains(locationInputAnswer = consoleHandler.receive(locationInputText).trim())));

        if (locationInputAnswer.equals("да") || locationInputAnswer.isBlank()) {
            while (inputField("значение поля 'координата X'",
                    location::setX,
                    Double::parseDouble,
                    Double.class
            ));

            while (inputField("значение поля 'координата Y'",
                    location::setY,
                    Double::parseDouble,
                    Double.class
            ));

            while (inputField("значение поля 'координата Z'",
                    location::setZ,
                    Integer::parseInt,
                    Integer.class
            ));

            while (inputField("название локации",
                    location::setName,
                    String::toString,
                    String.class
            ));
        }

        person.setLocation(location);
        studyGroup.setGroupAdmin(person);

        return studyGroup;
    }

    /**
     * Manage field to input
     * @param fieldName name of field
     * @param method method to set field
     * @param converter with which type to cast
     * @param type to which type cast
     * @return success in case field was inputted
     * @param <T>
     * @throws ExitException throws in case user want to exit
     */
    private <T> boolean inputField(String fieldName, Consumer<T> method, Function<String, T> converter, Class<T> type) throws ExitException {
        try {
            String userInput = consoleHandler.receive(MessageFormat.format("> Введите {0}", fieldName));

            if (userInput.trim().equals("exit"))
            {
                consoleManagerLogger.error("Exit! Inputted data not saved!");
                throw new ExitException("Выход! Введенные данные об объекте не будут сохранены!");
            }

            if (userInput.isBlank()) {
                method.accept(null);
                return false;
            }

            method.accept(castField(userInput, converter, type));
            return false;
        }
        catch (InvalidInputException | InvalidTypeCastException exception) {
            consoleManagerLogger.error("Error in element entering!");
            consoleHandler.sendWithNewLine(exception.getMessage());
            return true;
        }
    }

    /**
     * Casts user input to given type
     * @param userInput user input
     * @param converter with which to convert
     * @param type which type should be
     * @return casted field
     * @param <T>
     */
    private <T> T castField(String userInput, Function<String, T> converter, Class<T> type) {
        try {
            return converter.apply(userInput);
        }
        catch (Exception exception) {
            consoleManagerLogger.error("Error casting!");
            throw new InvalidTypeCastException(
                    MessageFormat.format("Неверный ввод! Поле должно быть типа {0}!", type.getSimpleName()));
        }
    }
}