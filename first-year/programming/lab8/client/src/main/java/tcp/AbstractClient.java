package tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transfer.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public abstract class AbstractClient extends Thread {
    private final Logger logger = LogManager.getLogger();
    protected String hostname;
    protected int port;
    protected SocketChannel server;
    public void setPort(int port) {
        this.port = port;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void connect() throws IOException {
        server = SocketChannel.open(new InetSocketAddress(hostname, port));
    }

    public void closeConnection() {
        try {
            if (server != null) server.close();

            logger.info("Connection is closed");
        } catch (IOException exception) {
            logger.error("Couldn't close connection!");
        }
    }

    protected Response getResponse() {
        logger.trace("Getting response from the server");

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(server.socket().getInputStream());
            return (Response) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            logger.error("Error occurred while trying to get response: {}", exception.getMessage());
            closeConnection();
            return null;
        }
    }
}
