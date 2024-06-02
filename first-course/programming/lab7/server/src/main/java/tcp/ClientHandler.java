package tcp;

import collection.CollectionManager;
import collection.data.StudyGroup;
import io.console.ConsoleHandler;
import io.console.InformationStorage;
import io.console.command.Command;
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
    private final Logger clientHandlerLogger = LogManager.getLogger();
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
            clientHandlerConsole.writeWithNewLine(clientHandlerLogger::error,
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

                            if (request == null) return;
                            clientHandlerConsole.write(clientHandlerLogger::info,
                                    "Request by " + clientChannel + " is next: " +
                                            "\nUSER: " + request.userDto().getUsername() + request.userDto().getPassword() +
                                            "\nCOMMAND: " + request.commandDto().getCommand());

                            Response response;
                            Callable<Response> responseCallable = () -> handleRequest(request);
                            FutureTask<Response> responseFutureTask = new FutureTask<>(responseCallable);

                            Thread handleRequestThread = new Thread(responseFutureTask);
                            handleRequestThread.start();

                            try {
                                response = responseFutureTask.get();
                            } catch (Exception exception) {
                                clientHandlerConsole.writeWithPromptNewLine(clientHandlerLogger::error,
                                        "Error occurred while handling request: " + exception.getMessage());
                                return;
                            }

                            clientHandlerConsole.writeWithPrompt(clientHandlerLogger::info, response.getResponseMessage());

                            new Thread(() -> {
                                try {
                                    sendResponse(response);
                                } catch (IOException exception) {
                                    clientHandlerConsole.writeWithPromptNewLine(clientHandlerLogger::error,
                                            "Error occurred while trying to send response: " + exception.getMessage());
                                }
                            }).start();

                        } catch (IOException exception) {
                            clientHandlerConsole.writeWithPromptNewLine(clientHandlerLogger::error,
                                    "Error occurred when handling client: " + exception.getMessage());
                            return;
                        }
                    }
                    selectedKeys.remove();
                }
            } catch (IOException exception) {
                clientHandlerConsole.writeWithNewLine(clientHandlerLogger::error,
                        "Server error: " + exception.getMessage());
            }
        }
    }

    private Request getRequest(ByteBuffer buffer) throws IOException {
        clientHandlerConsole.writeWithNewLine(clientHandlerLogger::trace, "Reading from client " + clientChannel);
        int readBytes;

        try {
            readBytes = clientChannel.read(buffer);
        } catch (IOException exception) {
            clientHandlerConsole.writeWithPrompt(clientHandlerLogger::error, "Error occurred when trying to read request: " + exception.getMessage());
            clientChannel.close();
            return null;
        }

        int EOFStatus = -1;
        if (readBytes == EOFStatus) {
            clientChannel.close();
            clientHandlerConsole.writeWithPrompt(clientHandlerLogger::info, "Client closed");
            return null;
        }

        Request request;
        try {
            request = SerializationUtils.deserialize(buffer.array());
        } catch (SerializationException serializationException) {
            clientHandlerConsole.writeWithPrompt(clientHandlerLogger::error, serializationException.getMessage());
            request = new Request(null, null);
        }
        return request;
    }

    private Response handleRequest(Request request) {
        InformationStorage.getInstance().setArguments(request.commandDto().getCommandArguments());

        StudyGroup providedStudyGroup = request.commandDto().getStudyGroup();
        InformationStorage.getInstance().setReceivedStudyGroup(providedStudyGroup);

        Response response = new Response();

        CommandValidator.MatchedCommand receivedCommand =
                CommandValidator.validateCommand(request.commandDto().getCommand(), request.commandDto().getCommandArguments(), request.commandDto().getStudyGroup());
        clientHandlerConsole.write(clientHandlerLogger::trace, String.valueOf(receivedCommand.validationStatus()));

        if (receivedCommand.command() == null) {
            response.setResponseMessage(receivedCommand.validationStatusDescription());
            response.setResponseStatus(ValidationStatus.NOT_RECOGNIZED);
        } else {
            response.setResponseMessage("Command <" + receivedCommand.command().getName() + ">");

            if (receivedCommand.validationStatus() == ValidationStatus.SUCCESS)
                tryToExecuteCommand(receivedCommand.command(), response);
            else {
                response.setResponseStatus(receivedCommand.validationStatus());
                response.setResponseStatusDescription(receivedCommand.validationStatusDescription());
            }
        }
        return response;
    }

    private void sendResponse(Response response) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(SerializationUtils.serialize(response));
        clientChannel.write(buffer);
    }

    private void tryToExecuteCommand(Command command, Response response) {
        response.setResponseMessage(command.execute(CollectionManager.getInstance()));
        response.setResponseStatus(ValidationStatus.SUCCESS);

        informationStorage.addToHistory(command);
    }
}
