package collection;

import java.util.LinkedList;
import java.util.Date;

import collection.data.StudyGroup;

public class CollectionManager {
    private LinkedList<StudyGroup> studyGroupCollection;
    private static CollectionManager instance = null;
    private Date initializationDate;

    public static CollectionManager getInstance() {
        if (instance == null) instance = new CollectionManager();
        return instance;
    }

    public CollectionManager() {
        studyGroupCollection = new LinkedList<>();
        initializationDate = new Date();
        studyGroupCollection.add(new StudyGroup());
        // StudyGroup testGroup = studyGroupCollection.get(0);
    }

    public String clearCollection() {
        studyGroupCollection.clear();
        return "Коллекция была успешно очищена";
    }

    public String getCollectionHead() {
        return "";
    }

    public String getCollectionInfo() {
        String collectionType = "Тип коллекции: " + studyGroupCollection.getClass().getName();
        String collectionInitializationDate = "Время инициализации коллекции: " + initializationDate;
        String collectionElementsNumber = "Количество элементов коллекции: " + studyGroupCollection.size(); 
        return collectionType + "\n" + collectionInitializationDate + "\n" + collectionElementsNumber;
    }
}
