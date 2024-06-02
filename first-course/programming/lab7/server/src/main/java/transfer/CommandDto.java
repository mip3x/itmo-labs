package transfer;

import collection.data.StudyGroup;
import io.console.command.Command;

import java.io.Serializable;
import java.util.List;

public class CommandDto implements Serializable {
    private Command command;
    private List<String> commandArguments;
    private StudyGroup studyGroup;

    public CommandDto(Command command, List<String> commandArguments) {
        this.command = command;
        this.commandArguments = commandArguments;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public List<String> getCommandArguments() {
        return commandArguments;
    }

    public void setCommandArguments(List<String> commandArguments) {
        this.commandArguments = commandArguments;
    }

    public StudyGroup getStudyGroup() {
        return studyGroup;
    }

    public void setStudyGroup(StudyGroup studyGroup) {
        this.studyGroup = studyGroup;
    }
}
