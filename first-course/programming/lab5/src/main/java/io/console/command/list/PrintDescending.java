package io.console.command.list;

import collection.CollectionManager;

import java.util.Collections;

/**
 * Returns elements of collection from high to low
 */
public class PrintDescending extends Command {
    private final CollectionManager collectionManager = CollectionManager.getInstance();
    public PrintDescending() {
        super("print_descending", "вывести элементы коллекции в порядке убывания");
    }

    @Override
    public String execute() {
        Collections.sort(collectionManager.getStudyGroupCollection());
        Collections.reverse(collectionManager.getStudyGroupCollection());
        return collectionManager.getAllStudyGroupsInfo();
    }
}
