package transfer;

import io.console.command.CommandDTO;

import java.io.Serializable;

public class Request implements Serializable {
    private CommandDTO commandDTO;
    private Data data;
    public Request(CommandDTO commandDTO) {
        this.commandDTO = commandDTO;
    }

    public Request() {}

    public CommandDTO getCommand() {
        return commandDTO;
    }

    public Data getData() {
        return data;
    }

    public void setCommand(CommandDTO commandDTO) {
        this.commandDTO = commandDTO;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
