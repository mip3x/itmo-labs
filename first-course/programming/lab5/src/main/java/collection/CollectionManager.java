package collection;

import java.util.LinkedList;
import java.util.Date;

import collection.data.StudyGroup;

public class CollectionManager {
    private static CollectionManager instance = null;
    private final LinkedList<StudyGroup> studyGroupCollection;
    private final Date initializationDate;

    public static CollectionManager getInstance() {
        if (instance == null) instance = new CollectionManager();
        return instance;
    }

    public CollectionManager() {
        studyGroupCollection = new LinkedList<>();
        initializationDate = new Date();
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
        StringBuilder studyGroupsInfo = new StringBuilder();
        if (studyGroupCollection.isEmpty()) return "Невозможно получить элемент коллекции: коллекция пуста!";
        studyGroupCollection.forEach(studyGroup ->
                studyGroupsInfo.append(getStudyGroupInfo(studyGroup)).append("\n\n"));
        String result = studyGroupsInfo.toString();
        return result.substring(0, result.length() - 2);
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

    public String addStudyGroupToCollection(StudyGroup studyGroup) {
        studyGroupCollection.add(studyGroup);
        return "Новый элемент был успешно добавлен в коллекцию";
    }

    public boolean validateID(Integer id) {
        return studyGroupCollection.stream().anyMatch(studyGroup -> studyGroup.compareId(id));
    }

    public String removeById(Integer id) {
        studyGroupCollection.removeIf(studyGroup -> studyGroup.compareId(id));
        return "Объект по данному id успешно удален";
    }
}
