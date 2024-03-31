package collection;

import java.util.LinkedList;
import java.util.Date;
import java.util.stream.IntStream;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import collection.data.*;

@XmlRootElement(name = "studyGroupCollection")
public class CollectionManager {
    private static CollectionManager instance = null;
    private LinkedList<StudyGroup> studyGroupCollection;
    private final Date initializationDate;

    public static CollectionManager getInstance() {
        if (instance == null) instance = new CollectionManager();
        return instance;
    }

    public CollectionManager() {
        studyGroupCollection = new LinkedList<>();
        initializationDate = new Date();
    }

    @XmlElement(name = "studyGroup")
    public LinkedList<StudyGroup> getStudyGroupCollection() {
        return studyGroupCollection;
    }

    public void setStudyGroupCollection(LinkedList<StudyGroup> studyGroupCollection) {
        this.studyGroupCollection = studyGroupCollection;
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

    public String updateById(Integer id, StudyGroup studyGroup) {
        int index = IntStream.range(0, studyGroupCollection.size())
                .filter(i -> studyGroupCollection.get(i).compareId(id))
                .findFirst()
                .orElse(-1);

        studyGroup.setId(id);
        studyGroup.setDate(studyGroupCollection.get(index).getCreationDate());
        studyGroupCollection.set(index, studyGroup);
        return "Объект по заданному id был успешно обновлен";
    }
}
