package tcp;

import collection.CollectionService;
import collection.data.StudyGroup;
import console.ConsoleHandler;
import command.InformationStorage;
import command.Command;
import database.DataBaseService;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transfer.Request;
import transfer.Response;
import validation.CommandValidator;
import validation.ValidationStatus;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.*;

public class ClientHandler implements Runnable {
    private final Logger logger = LogManager.getLogger();
    private final ConsoleHandler clientHandlerConsole = new ConsoleHandler();
    private SocketChannel clientChannel;
    private final InformationStorage informationStorage = InformationStorage.getInstance();
    private Selector selector;

    public ClientHandler(SocketChannel clientSocketChannel) {
        try {
            selector = Selector.open();

            clientChannel = clientSocketChannel;
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException exception) {
            clientHandlerConsole.writeWithNewLine(logger::error,
                    "Error occurred while handling client: " + exception.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(4096);

                selector.select();
                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey selectedKey = selectedKeys.next();

                    if (selectedKey.isReadable()) {
                        try {
                            Request request = getRequest(buffer);

                            clientHandlerConsole.write(logger::info,
                                    "Request by " + clientChannel);

                            Response response;
                            Callable<Response> responseCallable = () -> handleRequest(request);
                            FutureTask<Response> responseFutureTask = new FutureTask<>(responseCallable);

                            Thread handleRequestThread = new Thread(responseFutureTask);
                            handleRequestThread.start();

                            try {
                                response = responseFutureTask.get();
                                logger.trace("HANDLED RESPONSE");
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                clientHandlerConsole.writeWithPromptNewLine(logger::error,
                                        "Error occurred while handling request: " + exception.getMessage());
                                return;
                            }

                            clientHandlerConsole.writeWithPrompt(logger::info, response.getMessage());

                            new Thread(() -> {
                                try {
                                    sendResponse(response);
                                } catch (IOException exception) {
                                    clientHandlerConsole.writeWithPromptNewLine(logger::error,
                                            "Error occurred while trying to send response: " + exception.getMessage());
                                }
                            }).start();

                        } catch (IOException exception) {
                            clientHandlerConsole.writeWithPromptNewLine(logger::error,
                                    "Error occurred when handling client: " + exception.getMessage());
                            return;
                        }
                    }
                    selectedKeys.remove();
                }
            } catch (IOException exception) {
                clientHandlerConsole.writeWithNewLine(logger::error,
                        "Server error: " + exception.getMessage());
            }
        }
    }

    private Request getRequest(ByteBuffer buffer) throws IOException {
        clientHandlerConsole.writeWithNewLine(logger::trace, "Reading from client " + clientChannel);
        int readBytes;

        try {
            readBytes = clientChannel.read(buffer);
        } catch (IOException exception) {
            exception.printStackTrace();
            clientHandlerConsole.writeWithPrompt(logger::error, "Error occurred when trying to read request: " + exception.getMessage());
            clientChannel.close();
            return null;
        }

        int EOFStatus = -1;
        if (readBytes == EOFStatus) {
            clientChannel.close();
            clientHandlerConsole.writeWithPrompt(logger::info, "Client closed");
            return null;
        }

        Request request;
        try {
            request = SerializationUtils.deserialize(buffer.array());
        } catch (SerializationException serializationException) {
            clientHandlerConsole.writeWithPrompt(logger::error, serializationException.getMessage());
            request = null;
        }
        return request;
    }

    private Response handleRequest(Request request) {
        if (request == null) return new Response("Unknown request!", ValidationStatus.NOT_RECOGNIZED);

        Response response = new Response();

        boolean userExistence = DataBaseService.checkUserExistence(request.userDto().getUser().username());
        boolean registrationRequired = request.userDto().isRegistrationRequired();
        boolean userRegistrationStatus = false;

        logger.info("USER EXISTS: {}\nREGISTRATION REQUIRED: {}", userExistence, registrationRequired);

        if (userExistence && registrationRequired)
            return new Response("User with such username already exists!", ValidationStatus.USER_ALREADY_EXISTS);
        if (!userExistence && registrationRequired)
            userRegistrationStatus = DataBaseService.saveUser(request.userDto().getUser());
        if (userExistence && !DataBaseService.validateUserPassword(request.userDto().getUser()))
            return new Response("Invalid user data provided!", ValidationStatus.INVALID_USER_DATA);
        if (registrationRequired && !userRegistrationStatus)
            return new Response("Error registering", ValidationStatus.REGISTRATION_ERROR);
        if (!userExistence && !registrationRequired)
            return new Response("No account with such id was found: invalid user data provided!", ValidationStatus.INVALID_USER_DATA, "NO_ACC");

        logger.info("Client successfully authorized");
        if (request.commandDto() == null) return new Response("Client successfully authorized", ValidationStatus.SUCCESS, CollectionService.getInstance().getCollection());

        logger.info("Command is not null: trying to execute");

        InformationStorage.getInstance().setArguments(request.commandDto().getCommandArguments());

        StudyGroup providedStudyGroup = request.commandDto().getStudyGroup();
        InformationStorage.getInstance().setReceivedStudyGroup(providedStudyGroup);

        CommandValidator.MatchedCommand receivedCommand =
                CommandValidator.validateCommand(request.commandDto().getCommand(),
                        request.commandDto().getCommandArguments(),
                        request.commandDto().getStudyGroup(),
                        request.userDto().getUser().username());
        clientHandlerConsole.write(logger::trace, String.valueOf(receivedCommand.validationStatus()));

        if (receivedCommand.command() == null) {
            response.setMessage(receivedCommand.validationStatusDescription());
            response.setStatus(ValidationStatus.NOT_RECOGNIZED);
        } else {
            response.setMessage("Command <" + receivedCommand.command().getName() + ">");

            if (receivedCommand.validationStatus() == ValidationStatus.SUCCESS)
                tryToExecuteCommand(receivedCommand.command(), response, request.userDto().getUser().username());
            else {
                response.setStatus(receivedCommand.validationStatus());
                response.setStatusDescription(receivedCommand.validationStatusDescription());
            }
        }
        return response;
    }

    private void sendResponse(Response response) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(SerializationUtils.serialize(response));
        clientChannel.write(buffer);
    }

    private void tryToExecuteCommand(Command command, Response response, String username) {
        logger.trace("Trying to execute command...");

        String commandResponse = command.execute(CollectionService.getInstance(), username);
        response.setMessage(commandResponse);
        response.setStatus(commandResponse.contains("!") ?
                ValidationStatus.EXECUTION_ERROR : ValidationStatus.SUCCESS);

        informationStorage.addToHistory(command);
    }
}
