package tcp;

import java.nio.channels.SelectionKey;

public abstract class AbstractServer {
    protected int port;

    protected abstract void acceptConnection(SelectionKey selectionKey);

    public void setPort(int port) {
        this.port = port;
    }
}
