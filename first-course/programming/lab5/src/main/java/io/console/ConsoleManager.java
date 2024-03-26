package io.console;

import collection.data.*;
import io.console.command.list.Command;
import io.console.command.list.RequestingId;
import io.console.command.list.RequestingInput;
import exception.ExitException;
import exception.InvalidInputException;
import exception.InvalidTypeCastException;
import io.file.FileManager;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConsoleManager {
    private static ConsoleManager instance = null;
    private final ConsoleHandler consoleHandler;
    private final InformationStorage informationStorage;

    public static ConsoleManager getInstance() {
        if (instance == null) instance = new ConsoleManager();
        return instance;
    }

    public ConsoleManager() {
        this.informationStorage = InformationStorage.getInstance();
        this.consoleHandler = new ConsoleHandler();

        try {
            FileManager.loadCollection();
        }
        catch (InvalidInputException | IOException | JAXBException exception) {
            consoleHandler.sendWithNewLine(exception.getMessage());
        }
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
                if (command.getName().split(" ")[0].equals(tokens[0])) {

                    if (tokens.length > 1) informationStorage.setArguments(List.of(tokens).subList(1, tokens.length));

                    if (command instanceof RequestingId) {
                        try {
                            if (!((RequestingId) command).validateId())
                                throw new InvalidInputException("Объекта с данным id нет в коллекции!");
                        }
                        catch (InvalidInputException exception) {
                            consoleHandler.sendWithNewLine(exception.getMessage());
                            return;
                        }
                    }

                    if (command instanceof RequestingInput) {
                        try {
                            informationStorage.setReceivedStudyGroup(inputElement());
                        }
                        catch (ExitException exitException) {
                            consoleHandler.sendWithNewLine(exitException.getMessage());
                            return;
                        }
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

    private StudyGroup inputElement() throws ExitException {
        consoleHandler.sendWithNewLine(">*> Чтобы выйти из режима ввода объекта, введите 'exit'. Данные не сохранятся! <*<");
        StudyGroup studyGroup = new StudyGroup();

        while (inputField("имя",
                studyGroup::setName,
                String::toString,
                String.class
        ));

        while (inputField("количество студентов",
                studyGroup::setStudentsCount,
                Long::parseLong,
                Long.class
        ));

        while (inputField("количество студентов на отчисление",
                studyGroup::setShouldBeExpelled,
                Long::parseLong,
                Long.class
        ));

        consoleHandler.sendWithNewLine("* Введите координаты:");

        Coordinates coordinates = new Coordinates();

        while (inputField("значение поля 'координата X'",
                coordinates::setX,
                Long::parseLong,
                long.class
        ));

        while (inputField("значение поля 'координата Y'",
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

    private <T> boolean inputField(String fieldName, Consumer<T> method, Function<String, T> converter, Class<T> type) throws ExitException {
        try {
            String userInput = consoleHandler.receive(MessageFormat.format("> Введите {0}", fieldName));

            if (userInput.trim().equals("exit"))
                throw new ExitException("Выход! Введенные данные об объекте не будут сохранены!");

            if (userInput.isBlank()) {
                method.accept(null);
                return false;
            }

            method.accept(castField(userInput, converter, type));
            return false;
        }
        catch (InvalidInputException | InvalidTypeCastException exception) {
            consoleHandler.sendWithNewLine(exception.getMessage());
            return true;
        }
    }

    private <T> T castField(String userInput, Function<String, T> converter, Class<T> type) {
        try {
            return converter.apply(userInput);
        }
        catch (Exception exception) {
            throw new InvalidTypeCastException(
                    MessageFormat.format("Неверный ввод! Поле должно быть типа {0}!", type.getSimpleName()));
        }
    }
}