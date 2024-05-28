package collection;

import collection.data.StudyGroup;

import io.console.file.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.IntStream;

/**
 * Manages collection
 * Singleton
 */
@XmlRootElement(name = "studyGroupCollection")
public class CollectionManager implements Serializable {
    private static final Logger collectionManagerLogger = LogManager.getLogger();
    private static CollectionManager instance = null;
    private LinkedList<StudyGroup> studyGroupCollection;
    private final Date initializationDate;

    /**
     * Get instance of class
     * @return instance of class
     */
    public static CollectionManager getInstance() {
        if (instance == null) instance = new CollectionManager();
        return instance;
    }

    public CollectionManager() {
        studyGroupCollection = new LinkedList<>();
        initializationDate = new Date();
    }

    /**
     * Get collection
     * @return collection
     */
    @XmlElement(name = "studyGroup")
    public LinkedList<StudyGroup> getStudyGroupCollection() {
        return studyGroupCollection;
    }

    /**
     * Set collection
     * @param studyGroupCollection collection to set
     */
    public void setStudyGroupCollection(LinkedList<StudyGroup> studyGroupCollection) {
        this.studyGroupCollection = studyGroupCollection;
    }

    /**
     * Save collection
     */
    public String saveCollection() {
        try {
            FileManager.saveCollection();
            return "Collection was successfully saved";
        } catch (IOException | JAXBException exception) {
            collectionManagerLogger.error(exception);
            return exception.getMessage();
        }
    }

    /**
     * Clear collection
     * @return message about clearing
     */
    public String clearCollection() {
        studyGroupCollection.clear();
        collectionManagerLogger.trace("Collection has been cleared");
        return "Collection has been cleared";
//        return "Коллекция была успешно очищена";
    }

    /**
     * Get head of collection
     * @return head of collection
     */
    public String getCollectionHead() {
        try {
            collectionManagerLogger.trace("Trying to get collection head");
            return getStudyGroupInfo(studyGroupCollection.get(0));
        }
        catch (IndexOutOfBoundsException e) {
            collectionManagerLogger.error("Collection is empty: impossible to get element!");
            return "Collection is empty: impossible to get element!";
//            return "Невозможно получить элемент коллекции: коллекция пуста!";
        }
    }

    /**
     * Get info about all elements of collection
     * @return info about all elements of collection
     */
    public String getAllStudyGroupsInfo() {
        StringBuilder studyGroupsInfo = new StringBuilder();
        if (studyGroupCollection.isEmpty()) {
            collectionManagerLogger.warn("Collection is empty: impossible to get element!");
            return "Collection is empty: impossible to get element!";
//            return "Невозможно получить элемент коллекции: коллекция пуста!";
        }
        studyGroupCollection.forEach(studyGroup ->
                studyGroupsInfo.append(getStudyGroupInfo(studyGroup)).append("\n\n"));
        String result = studyGroupsInfo.toString();

        collectionManagerLogger.trace("Study groups info is gotten");
        return result.substring(0, result.length() - 2);
    }

    /**
     * Get info about element of collection
     * @param studyGroup element of collection
     * @return info about element of collection
     */
    public String getStudyGroupInfo(StudyGroup studyGroup) {
        return studyGroup.toString();
    }

    /**
     * Get info about collection
     * @return info about collection
     */
    public String getCollectionInfo() {
//        String collectionType = "Тип коллекции: " + studyGroupCollection.getClass().getName();
        String collectionType = "Collection type: " + studyGroupCollection.getClass().getName();
//        String collectionInitializationDate = "Время инициализации коллекции: " + initializationDate;
        String collectionInitializationDate = "Collection initialization time: " + initializationDate;
//        String collectionElementsNumber = "Количество элементов коллекции: " + studyGroupCollection.size();
        String collectionElementsNumber = "Size of collection: " + studyGroupCollection.size();
        return collectionType + "\n" + collectionInitializationDate + "\n" + collectionElementsNumber;
    }

    /**
     * Add element to collection
     * @param studyGroup element to add in collection
     * @return message about adding
     */
    public String addStudyGroupToCollection(StudyGroup studyGroup) {
        studyGroupCollection.add(studyGroup);
//        return "Новый элемент был успешно добавлен в коллекцию";
        return "New element was successfully added to collection";
    }

    /**
     * Validate id
     * @param id id to validate
     * @return status of validation
     */
    public boolean validateID(Integer id) {
        return studyGroupCollection.stream().anyMatch(studyGroup -> studyGroup.compareId(id));
    }

    /**
     * Remove element by given id
     * @param id id of element to delete
     * @return message about deleting
     */
    public String removeById(Integer id) {
        studyGroupCollection.removeIf(studyGroup -> studyGroup.compareId(id));
//        return "Объект по данному id успешно удален";
        return "Object with such id was successfully deleted";
    }

    /**
     * Update element by id
     * @param id id of element to update
     * @param studyGroup element of collection
     * @return message about updating
     */
    public String updateById(Integer id, StudyGroup studyGroup) {
        int index = IntStream.range(0, studyGroupCollection.size())
                .filter(i -> studyGroupCollection.get(i).compareId(id))
                .findFirst()
                .orElse(-1);

        studyGroup.setId(id);
        studyGroup.setCreationDate(studyGroupCollection.get(index).getCreationDate());
        studyGroupCollection.set(index, studyGroup);
//        return "Объект по заданному id был успешно обновлен";
        return "Object with given id was successfully updated";
    }
}
