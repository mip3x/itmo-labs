package io.console.command;

import java.io.Serializable;

public class CommandDTO implements Serializable {
    private final String name;
    public CommandDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
