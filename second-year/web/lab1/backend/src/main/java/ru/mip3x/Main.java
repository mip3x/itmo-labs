package ru.mip3x;

import ru.mip3x.server.Server;

public class Main {
    public static void main(String[] args) {
        Server fcgiServer = new Server();
        fcgiServer.run();
    }
}
