package io.file;

import collection.data.StudyGroup;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.LinkedList;

public class FileManager {
    private static Path filePath;

    public static void setFilePath(String path) throws Exception {
        File file = new File(path);

        if (!file.exists()) {
            throw new Exception(MessageFormat.format("Файла {0} не существует!", path));
        }

//                if (!file.createNewFile()) throw new Exception();
//                throw new Exception(MessageFormat.format("Не удалось создать файл {0}!", path));

        filePath = Path.of(file.getPath());
    }

    public static LinkedList<StudyGroup> loadCollection(){
        return null;
    }
    public static void saveCollection(LinkedList<StudyGroup> collection) {
    }
}
