package io.console.command.list;

import collection.CollectionService;
import io.console.command.Command;

public class Test extends Command {
    public Test() {
        super("test", "test command which is unknown for server");
    }

    @Override
    public String execute(CollectionService collectionService, String username) {
        return "test";
    }
}