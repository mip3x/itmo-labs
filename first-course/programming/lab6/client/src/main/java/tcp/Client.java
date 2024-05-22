package tcp;

import io.console.ConsoleHandler;
import io.console.command.CommandDTO;
import io.console.command.CommandParser;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transfer.Request;
import transfer.Response;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client implements Runnable {
    private final int EOFStatus = -1;
    Logger clientLogger = LogManager.getLogger();
    private SocketChannel server;
    private final String hostname;
    private final int port;
    private final ConsoleHandler consoleHandler;
    private Response response;

    public Client(String hostname, int port, ConsoleHandler consoleHandler) {
        this.hostname = hostname;
        this.port = port;
        this.consoleHandler = consoleHandler;
    }

    @Override
    public void run() {
        consoleHandler.sendWithNewLine("""
                Type 'exit' in order to close connection and exit from the program
                Type 'connect in order to connect/reconnect to the server
                Type 'disconnet' in order to disconnect from the server""");

        String inputLine;
        while ((inputLine = consoleHandler.receive(true)) != null) {
            process(inputLine);
        }
    }

    private void process(String inputLine) {
        if (inputLine.equals("connect")) {
            connect();
            return;
        }
        if (inputLine.equals("disconnect")) {
            closeConnection();
            return;
        }
        if (inputLine.equals("exit")) {
            closeConnection();
            System.exit(0);
        }

        CommandParser.ParsedCommand parsedCommand = CommandParser.parseCommand(inputLine);

        if (parsedCommand == null) {
            clientLogger.trace("Command is null");
            return;
        }

        Request request = new Request(new CommandDTO(parsedCommand.commandName()));
        clientLogger.debug("going to send request with command...");
        clientLogger.info("Request is {}", request.getCommand());

        if (sendRequest(request)) response = getResponse();
        else response = null;

        if (response != null) consoleHandler.sendWithNewLine("Got response: " + response.getResponseText());
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
            closeConnection();
            return false;
        }
    }

    private Response getResponse() {
        clientLogger.trace("Getting response from the server");

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            int readBytes = server.read(buffer);
            if (readBytes == EOFStatus) {
                return null;
            }

            response = new Response(new String(buffer.array()).trim());
            return response;
        } catch (IOException exception) {
            clientLogger.error("Error occurred when trying to get response: {}", exception.getMessage());
            closeConnection();
            return null;
        }
    }

    private void closeConnection() {
        try {
            if (server != null) server.close();

            clientLogger.info("Connection is closed");
        } catch (IOException exception) {
            clientLogger.error("Couldn't close connection!");
        }
    }

    private void connect() {
        try {
            server = SocketChannel.open(new InetSocketAddress(hostname, port));
            clientLogger.info("Successfully connected to server");
        } catch (IOException exception) {
            clientLogger.warn("Couldn't connect to the server. Type 'connect' to reconnect");
            closeConnection();
        }
    }
}
