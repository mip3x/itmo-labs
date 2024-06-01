package tcp;

import io.console.ConsoleHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private final Logger serverLogger = LogManager.getLogger();
    private Selector selector;
    private final int port;
    private final ConsoleHandler serverConsole = new ConsoleHandler();
    private final int THREADS_CONST = 5;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(THREADS_CONST);

    public Server(int port) {
        this.port = port;
    }

    public void init() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(port));

            serverConsole.write(serverLogger::trace, "Server bound on port " + port);

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
                selector.select();
                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey selectedKey = selectedKeys.next();
                    if (selectedKey.isAcceptable()) acceptConnection(selectedKey);
                    selectedKeys.remove();
                }
            } catch (IOException exception) {
                serverConsole.writeWithNewLine(serverLogger::error,
                        "Server error: " + exception.getMessage());
            }
        }
    }

    private void acceptConnection(SelectionKey selectedKey) {
        serverConsole.writeWithNewLine(serverLogger::trace, "Trying to accept connection...");

        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectedKey.channel();
        try {
            SocketChannel clientSocketChannel = serverSocketChannel.accept();
            threadPool.execute(new ClientHandler(clientSocketChannel));

            serverConsole.writeWithPrompt(serverLogger::info, "Client connection accepted");
        } catch (IOException exception) {
            serverConsole.writeWithPrompt(serverLogger::error, "Error occurred when trying to accept connection: " + exception.getMessage());
        }
    }
}
