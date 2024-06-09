package tcp;

import collection.data.*;
import exception.ExitException;
import exception.InvalidInputException;
import exception.InvalidTypeCastException;
import command.InformationStorage;
import command.CommandParser;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dto.CommandDto;
import transfer.Request;
import transfer.Response;
import dto.UserDto;
import validation.ValidationStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Client {
    private final int EOFStatus = -1;
    Logger clientLogger = LogManager.getLogger();
    private SocketChannel server;
    private final String hostname = "localhost";
    private final int port = 1337;
    private String username;
    private String password;
    private boolean registrationRequired;
    private Response response;
    private static final int MAX_RECURSION_DEPTH = 5;
    private int currentRecursionDepth = 0;

    public Client() {
        InformationStorage.getInstance();
    }

    private void process(String inputLine) {
        if (inputLine.equals("connect")) {
//            connect();
            return;
        }
        if (inputLine.equals("disconnect")) {
            closeConnection();
            return;
        }
        if (inputLine.equals("logout")) {
            logout();
            clientLogger.info("Logged out");
            return;
        }
        if (inputLine.equals("exit")) {
            closeConnection();
            System.exit(0);
        }

        CommandDto commandDto = CommandParser.parseCommand(inputLine);
        clientLogger.trace("Command parsed");

        if (commandDto == null) {
            clientLogger.trace("Request is null");
            return;
        }

        if (commandDto.getCommand() == null) {
            clientLogger.error("Command was not recognized!");
            return;
        }

        if (server == null || !server.isConnected()) {
            clientLogger.error("Not connected to the server: impossible to form a request!");
            return;
        }

        Request request = new Request(commandDto, new UserDto(username, password, registrationRequired));

        if (commandDto.getCommand().getName().equals("execute_script")) {
            clientLogger.trace("Going to execute script...");

            if (currentRecursionDepth > MAX_RECURSION_DEPTH) {
                currentRecursionDepth = 0;
                clientLogger.error("Recursion max depth exceeded = " + MAX_RECURSION_DEPTH);
                return;
            }

            currentRecursionDepth++;

            handleScriptExecution(request);
            return;
        }

        clientLogger.debug("Going to send request with command...");
        clientLogger.info("Request is {}, {}, {}", request.commandDto().getCommand(), request.commandDto().getCommandArguments(), request.commandDto().getStudyGroup());

        if (sendRequest(request)) response = getResponse();
//        handleResponse(response, request);
        response = null;
    }

    public Response handleUser(String username, String password, boolean registrationRequired) {
        Request request = new Request(null, new UserDto(username, password, registrationRequired));
        if (sendRequest(request)) return handleResponse(getResponse());
        return null;
    }

    private boolean sendRequest(Request request) {
        clientLogger.trace("Serializing request");
        ByteBuffer buffer = ByteBuffer.wrap(SerializationUtils.serialize(request));

        clientLogger.trace("Sending request to the server");
        try {
            server.write(buffer);
            return true;
        } catch (IOException | NullPointerException exception) {
            clientLogger.error("Error occurred when sending request: {}", exception.getMessage());
            closeConnection()   ;
            return false;
        }
    }

    private Response getResponse() {
        clientLogger.trace("Getting response from the server");

        ByteBuffer buffer = ByteBuffer.allocate(4096);
        try {
            int readBytes = server.read(buffer);
            if (readBytes == EOFStatus) {
                return null;
            }

            response = SerializationUtils.deserialize(buffer.array());

            return response;

        } catch (IOException exception) {
            clientLogger.error("Error occurred when trying to get response: {}", exception.getMessage());
            closeConnection();
            return null;
        }
    }

    private Response handleResponse(Response response) {
        if (response != null) {
            ValidationStatus responseStatus = response.getStatus();

            if (responseStatus != ValidationStatus.NOT_RECOGNIZED && responseStatus != ValidationStatus.SUCCESS) {
                clientLogger.error(response.getMessage());

                String newResponseMessage = response.getMessage();
                if (response.getStatusDescription() != null)
                    newResponseMessage += ": " + response.getStatusDescription();
                response.setMessage(newResponseMessage);
            }

            clientLogger.info("Got response: \n" + response.getMessage());
            return response;
        }
        return null;
    }

    private void handleScriptExecution(Request request) {
        String filePath;
        try {
            filePath = request.commandDto().getCommandArguments().get(0);
            File file = new File(filePath);

            if (!file.exists()) {
                clientLogger.trace("File with script does not exist!");
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

                commandsToExecute.forEach(this::process);
                clientLogger.trace("Commands from script have been sent to execute");
            } catch (IOException exception) {
                clientLogger.error("Error reading file: insufficient access rights!");
            }

        } catch (Exception exception) {
            clientLogger.error("Enter valid path to the file!");
        }
    }

    private void logout() {
        username = null;
        password = null;
    }

    public void closeConnection() {
        try {
            if (server != null) server.close();

            clientLogger.info("Connection is closed");
        } catch (IOException exception) {
            clientLogger.error("Couldn't close connection!");
        }
    }

    public void connect() throws IOException {
        server = SocketChannel.open(new InetSocketAddress(hostname, port));
    }

    private StudyGroup inputElement() throws ExitException {
        StudyGroup studyGroup = new StudyGroup();

        while (inputField("name",
                studyGroup::setName,
                String::toString,
                String.class
        )) ;

        while (inputField("quantity of students",
                studyGroup::setStudentsCount,
                Long::parseLong,
                Long.class
        )) ;

        while (inputField("quantity of students that should be expelled",
                studyGroup::setShouldBeExpelled,
                Long::parseLong,
                Long.class
        )) ;

//        consoleHandler.sendWithNewLine("* Enter the coordinates:");

        Coordinates coordinates = new Coordinates();

        while (inputField("the value of the 'X coordinate' field",
                coordinates::setX,
                Long::parseLong,
                long.class
        )) ;

        while (inputField("the value of the 'Y coordinate' field",
                coordinates::setY,
                Double::parseDouble,
                Double.class
        )) ;

        studyGroup.setCoordinates(coordinates);

//        while (inputField("значение поля 'форма обучения' (одно из нижеперечисленных)\n"
        while (inputField("value of field 'form of education' (one from list below)\n"
                        + Arrays.toString(FormOfEducation.values()),
                studyGroup::setFormOfEducation,
                FormOfEducation::valueOf,
                FormOfEducation.class
        )) ;

//        while (inputField("значение поля 'семестр обучения' (одно из нижеперечисленных)\n"
        while (inputField("value of field 'semester' (one from list below)\n"
                        + Arrays.toString(Semester.values()),
                studyGroup::setSemester,
                Semester::valueOf,
                Semester.class
        )) ;

        Person person = new Person();

//        consoleHandler.sendWithNewLine("* Введите данные старосты:");

//        while (inputField("имя старосты",
        while (inputField("admins name",
                person::setName,
                String::toString,
                String.class
        )) ;

//        while (inputField("вес старосты",
        while (inputField("admins weight",
                person::setWeight,
                Long::parseLong,
                Long.class
        )) ;

//        while (inputField("номер паспорта старосты",
        while (inputField("admins passport number",
                person::setPassportID,
                String::toString,
                String.class
        )) ;

//        while (inputField("цвет глаз старосты (один из нижеперечисленных)\n"
        while (inputField("eyes' color of admin (one from list below)\n"
                        + Arrays.toString(Color.values()),
                person::setEyeColor,
                Color::valueOf,
                Color.class
        )) ;

        Location location = null;
//        List<String> locationInputList = Arrays.asList("да", "нет", "");
        List<String> locationInputList = Arrays.asList("y", "n", "");
//        String locationInputText = "* Ввод локации\nВыберите 'да', если необходим (перевод строки воспринимается как 'да') (да/нет)";
        String locationInputText = "* Location input\nChoose 'y', if required (new line interpreted as 'y') (y/n)";
        String locationInputAnswer;


        location = new Location();
//            while (inputField("значение поля 'координата X'",
        while (inputField("value of field 'coordinate X'",
                location::setX,
                Double::parseDouble,
                Double.class
        )) ;

//            while (inputField("значение поля 'координата Y'",
        while (inputField("value of field 'coordinate Y'",
                location::setY,
                Double::parseDouble,
                Double.class
        )) ;

//            while (inputField("значение поля 'координата Z'",
        while (inputField("value of field 'coordinate Z'",
                location::setZ,
                Integer::parseInt,
                Integer.class
        )) ;

//            while (inputField("название локации",
        while (inputField("location name",
                location::setName,
                String::toString,
                String.class
        )) ;

        person.setLocation(location);
        studyGroup.setGroupAdmin(person);

        return studyGroup;
    }

    private <T> boolean inputField(String fieldName, Consumer<T> method, Function<String, T> converter, Class<T> type) throws ExitException {
        try {
//            String userInput = consoleHandler.receive(MessageFormat.format("> Enter {0}", fieldName));
            String userInput = "";

            if (userInput.trim().equals("exit")) {
                clientLogger.error("Exit! Inputted data not saved!");
//                throw new ExitException("Выход! Введенные данные об объекте не будут сохранены!");
                throw new ExitException("Exit! Inputted data not saved!");
            }

            if (userInput.isBlank()) {
                method.accept(null);
                return false;
            }

            method.accept(castField(userInput, converter, type));
            return false;
        } catch (InvalidInputException | InvalidTypeCastException exception) {
            clientLogger.error("Error in element entering: " + exception.getMessage());
            return true;
        }
    }

    private <T> T castField(String userInput, Function<String, T> converter, Class<T> type) {
        try {
            return converter.apply(userInput);
        } catch (Exception exception) {
            clientLogger.error("Error casting!");
            throw new InvalidTypeCastException(
                    MessageFormat.format("Invalid input! Field should be type of {0}!", type.getSimpleName()));
//                    MessageFormat.format("Неверный ввод! Поле должно быть типа {0}!", type.getSimpleName()));
        }
    }

    private StudyGroup getTestStudyGroup() {
        StudyGroup providedStudyGroup = new StudyGroup();
        providedStudyGroup.setName("HUBA BUBA");
        providedStudyGroup.setStudentsCount(758L);
        providedStudyGroup.setShouldBeExpelled(2342L);
        providedStudyGroup.setFormOfEducation(FormOfEducation.EVENING_CLASSES);
        providedStudyGroup.setSemester(Semester.SECOND);

        Coordinates coordinates = new Coordinates();
        coordinates.setX(12L);
        coordinates.setY(13d);

        Person person = new Person();
        person.setName("dudeDUDE");
        person.setPassportID("77777");
        person.setWeight(89L);
//        person.setEyeColor(Color.BROWN);

//        Location location = new Location();
//        location.setX(123d);
//        location.setY(564d);
//        location.setZ(564);
//        location.setName("usa");
//        person.setLocation(location);
        person.setLocation(null);

        providedStudyGroup.setCoordinates(coordinates);
        providedStudyGroup.setGroupAdmin(person);
        return providedStudyGroup;
    }
}
