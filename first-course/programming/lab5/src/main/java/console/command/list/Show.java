package console.command.list;

import console.InformationStorage;
import collection.CollectionManager;

public class Show extends Command {
//    private final CollectionManager collectionManager;

    public Show() {
        super("show", "Вывести все элементы коллекции");
    }

    @Override
    public String execute() {
//        return collectionManager.getAllStudyGroupsInfo();
        return "Show was executed";
    } 
}
