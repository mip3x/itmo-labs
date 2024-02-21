package console.command.list;

import java.util.List;

public class History extends Command {
    private List<Command> history;
    private int historySize;

    public History(String name, String description, List<Command> history, int historySize) {
        super(name, description);
        this.history = history;
        this.historySize = historySize;
    }

    @Override
    public String execute() {        
        String result = "";

        if (history.size() != 0) {

            if (history.size() <= historySize) {
                for (Command command: history) {
                    result += command.getName() + "\n";
                }
            }
            else {
                for (int i = history.size() - historySize; i <= history.size() - 1; i++) {
                    result += history.get(i).getName() + "\n";
                }
            }

            return result.substring(0, result.length() - 1);
        }
        else return result;
    } 
}
