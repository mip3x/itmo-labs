package tcp;

import collection.CollectionManager;
import io.console.ConsoleHandler;
import io.console.command.Command;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transfer.Request;
import validation.CommandValidator;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server implements Runnable {
    private final Logger serverLogger = LogManager.getLogger();
    private final int EOFStatus = -1;
    private Selector selector;
    private final int port;
    private final ConsoleHandler serverConsole = new ConsoleHandler();
    public Server(int port) {
        this.port = port;
    }

    public void init() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(port));

            serverConsole.writeWithPrompt(serverLogger::trace, "Server bound on port " + port);

            selector = Selector.open();

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // subscribe on accept

        } catch (IOException exception) {
            serverConsole.writeWithNewLine(serverLogger::error, exception.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (serverConsole.ready()) {
                    String inputLine = serverConsole.receive();

                    if (inputLine.equals("save")) {
                        String message = CollectionManager.getInstance().saveCollection();
                        serverConsole.writeWithPromptNewLine(serverLogger::info, message);
                    }

                    else if (inputLine.equals("exit")) {
                        String message = CollectionManager.getInstance().saveCollection();
                        serverConsole.writeWithNewLine(serverLogger::info, message);
                        serverConsole.writeWithPrompt(serverLogger::info, "Server stopped");
                        System.exit(0);
                    }
                    else {
                        serverConsole.writeWithPrompt(serverLogger::debug, inputLine);
                    }
                }

                ByteBuffer buffer = ByteBuffer.allocate(1024);

                int select = selector.selectNow();
                if (select == 0) continue;

                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey selectedKey = selectedKeys.next();

                    if (selectedKey.isAcceptable()) {
                        acceptConnection(selectedKey);
                    } else if (selectedKey.isReadable()) {
                        try {
                            handleRequest(buffer, selectedKey);
                        } catch (IOException exception) {
                            serverConsole.writeWithPromptNewLine(serverLogger::error,
                                    "Error occurred when trying to handle request: " + exception.getMessage());
                            return;
                        }
                    }
                    selectedKeys.remove();

                }
            } catch (IOException exception) {
                serverConsole.writeWithPromptNewLine(serverLogger::error,
                        "Server error: " + exception.getMessage());
            }
        }
    }

    private void acceptConnection(SelectionKey selectedKey) {
        serverConsole.writeWithNewLine(serverLogger::trace, "Trying to accept connection...");

        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectedKey.channel();
        try {
            SocketChannel clientSocketChannel = serverSocketChannel.accept();
            clientSocketChannel.configureBlocking(false);
            clientSocketChannel.register(selector, SelectionKey.OP_READ);

            serverConsole.writeWithPrompt(serverLogger::info, "Client connection accepted");
        } catch (IOException exception) {
            serverConsole.writeWithPrompt(serverLogger::error, "Error occurred when trying to accept connection: " + exception.getMessage());
        }
    }

    private void handleRequest(ByteBuffer buffer, SelectionKey selectedKey) throws IOException {
        SocketChannel clientChannel = (SocketChannel) selectedKey.channel();
        serverConsole.writeWithNewLine(serverLogger::trace, "Reading from client " + clientChannel);
        int readBytes;

        try {
            readBytes = clientChannel.read(buffer);
        } catch (IOException exception) {
            serverConsole.writeWithPrompt(serverLogger::error, "Error occurred when trying to read request: " + exception.getMessage());
            clientChannel.close();
            return;
        }

        if (readBytes == EOFStatus) {
            clientChannel.close();
            serverConsole.writeWithPrompt(serverLogger::info, "Client closed");
            return;
        }

        Request request = SerializationUtils.deserialize(buffer.array());
        if (request == null) {
            serverConsole.writeWithPrompt(serverLogger::info, "NULL request");
            return;
        }

        serverConsole.write(serverLogger::info, "Request by " + clientChannel + " is next: \nCOMMAND: " + request.getCommand());

        String someResponse;

        Command receivedCommand = CommandValidator.matchCommand(request.getCommand());
        if (receivedCommand != null) someResponse = "command is {" + receivedCommand.getName() + "}";
        else someResponse = "Command was not recognized!";

        serverConsole.writeWithPrompt(serverLogger::info, someResponse);

        buffer = ByteBuffer.wrap(someResponse.getBytes());
        clientChannel.write(buffer);
    }
}