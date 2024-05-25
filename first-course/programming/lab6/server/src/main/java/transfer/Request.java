package transfer;

import io.console.command.CommandDTO;

import java.io.Serializable;

public class Request implements Serializable {
    private CommandDTO commandDTO;

    public Request(CommandDTO commandDTO) {
        this.commandDTO = commandDTO;
    }

    public CommandDTO getCommandDTO() {
        return commandDTO;
    }

    public void setCommandDTO(CommandDTO commandDTO) {
        this.commandDTO = commandDTO;
    }
}