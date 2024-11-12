package tcp;

import console.ConsoleHandler;
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

public class Server extends AbstractServer implements Runnable {
    private final Logger logger = LogManager.getLogger();
    private static Server instance = null;
    private Selector selector;
    private final ConsoleHandler console = new ConsoleHandler();
    private final int THREADS_CONST = 5;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(THREADS_CONST);

    public static Server getInstance() {
        if (instance == null) instance = new Server();
        return instance;
    }

    public void init() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(port));

            console.write(logger::trace, "Server bound on port " + port);

            selector = Selector.open();

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (Exception exception) {
            console.writeWithNewLine(logger::error, exception.getMessage());
            System.exit(-1);
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
                console.writeWithNewLine(logger::error,
                        "Server error: " + exception.getMessage());
            }
        }
    }

    @Override
    protected void acceptConnection(SelectionKey selectedKey) {
        console.writeWithNewLine(logger::trace, "Trying to accept connection...");

        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectedKey.channel();
        try {
            SocketChannel clientSocketChannel = serverSocketChannel.accept();
            threadPool.execute(new ClientHandler(clientSocketChannel));

            console.writeWithPrompt(logger::info, "Client connection accepted");
        } catch (IOException exception) {
            console.writeWithPrompt(logger::error, "Error occurred when trying to accept connection: " + exception.getMessage());
        }
    }
}
