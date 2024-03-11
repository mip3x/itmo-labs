package collection;

import java.util.LinkedList;
import java.util.Date;

import collection.data.StudyGroup;
import collection.data.StudyGroupBuilder;
import console.ConsoleManager;

public class CollectionManager {
    private static CollectionManager instance = null;
    private LinkedList<StudyGroup> studyGroupCollection;
    private Date initializationDate;

    public static CollectionManager getInstance() {
        if (instance == null) instance = new CollectionManager();
        return instance;
    }

    public CollectionManager() {
        studyGroupCollection = new LinkedList<>();
        initializationDate = new Date();
        studyGroupCollection.add(new StudyGroup());
    }

    public String clearCollection() {
        studyGroupCollection.clear();
        return "Коллекция была успешно очищена";
    }

    public String getCollectionHead() {
        try {
            return getStudyGroupInfo(studyGroupCollection.get(0));
        }
        catch (IndexOutOfBoundsException e) {
            return "Невозможно получить элемент коллекции: коллекция пуста!";
        }
    }

    public String getAllStudyGroupsInfo() {
        String studyGroupsInfo = "";
        if (studyGroupCollection.size() == 0) return "Невозможно получить элемент коллекции: коллекция пуста!";
        studyGroupCollection.forEach(studyGroup -> studyGroupsInfo.concat(getStudyGroupInfo(studyGroup) + "\n"));
        return studyGroupsInfo;
    }

    public String getStudyGroupInfo(StudyGroup studyGroup) {
        return studyGroup.toString();
    }

    public String getCollectionInfo() {
        String collectionType = "Тип коллекции: " + studyGroupCollection.getClass().getName();
        String collectionInitializationDate = "Время инициализации коллекции: " + initializationDate;
        String collectionElementsNumber = "Количество элементов коллекции: " + studyGroupCollection.size(); 
        return collectionType + "\n" + collectionInitializationDate + "\n" + collectionElementsNumber;
    }

    public void addStudyGroupToCollection(StudyGroup studyGroup) {
        studyGroupCollection.add(studyGroup);
    }

    public StudyGroup callStudyGroupBuilder() {
        return new StudyGroupBuilder().getStudyGroup();
    }

    public StudyGroup callStudyGroupBuilder(Integer id) {
        // search
        return new StudyGroupBuilder(id).getStudyGroup();
    }
}
