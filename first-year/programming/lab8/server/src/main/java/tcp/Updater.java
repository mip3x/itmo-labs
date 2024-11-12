package tcp;

import collection.data.StudyGroup;
import console.ConsoleHandler;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transfer.Response;
import validation.ValidationStatus;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedList;

public class Updater extends Server {
    private final Logger logger = LogManager.getLogger();
    private static Updater instance = null;
    private final ConsoleHandler console = new ConsoleHandler();
    private ArrayList<SocketChannel> clientSocketChannels = new ArrayList<>();

    public static Updater getInstance() {
        if (instance == null) instance = new Updater();
        return instance;
    }

    @Override
    protected synchronized void acceptConnection(SelectionKey selectedKey) {
        console.writeWithNewLine(logger::trace, "Trying to accept connection...");

        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectedKey.channel();
        try {
            SocketChannel clientSocketChannel = serverSocketChannel.accept();
            clientSocketChannels.add(clientSocketChannel);

            console.writeWithPrompt(logger::info, "Client connection accepted");
        } catch (IOException exception) {
            console.writeWithPrompt(logger::error, "Error occurred when trying to accept connection: " + exception.getMessage());
        }
    }

    public synchronized void updateCollection(LinkedList<StudyGroup> collection) {
        new Thread(() -> clientSocketChannels.forEach(
                clientChannel -> {
                    Response response = new Response("Collection updated", ValidationStatus.SUCCESS, collection);

                    ByteBuffer buffer = ByteBuffer.wrap(SerializationUtils.serialize(response));
                    try {
                        clientChannel.write(buffer);
                    } catch (IOException exception) {
                        logger.error("Error occurred while trying to update collection: " + exception.getMessage());
                    }
                }
        )).start();
    }
}
