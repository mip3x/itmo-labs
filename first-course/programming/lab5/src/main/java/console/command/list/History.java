package console.command.list;

import console.command.InformationManager;

import java.util.List;

public class History extends Command {
    private List<Command> history;
    private int historySize;

    public History(String name, String description, InformationManager informationManager) {
        super(name, description, informationManager);
        this.historySize = informationManager.getHistorySize();
        this.history = informationManager.getHistory();
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
