package io.console.command.list;

import collection.CollectionManager;
import io.file.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Saves collection to file
 */
public class Save extends Command {
    private static final Logger saveCommandLogger = LogManager.getLogger();
    public Save() {
        super("save", "сохранить коллекцию в файл");
    }

    @Override
    public String execute() {
        if (CollectionManager.getInstance().getStudyGroupCollection() == null)
        {
            saveCommandLogger.info("Collection is empty. Nothing to save");
            saveCommandLogger.trace("Save command executed");
            return "Коллекция пуста! Сохранение не произведено!";
        }
        try {
            FileManager.saveCollection();
            saveCommandLogger.info("Collection has been saved");
            saveCommandLogger.trace("Save command executed");
            return "Коллекция была успешно сохранена!";
        }
        catch (Exception exception) {
            saveCommandLogger.error("Saving error");
            return exception.getMessage();
        }
    }
}
