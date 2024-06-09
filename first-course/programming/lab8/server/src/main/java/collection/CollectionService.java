package collection;

import collection.data.StudyGroup;
import database.DataBaseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class CollectionService implements Serializable {
    private static final Logger collectionServiceLogger = LogManager.getLogger();
    private static CollectionService instance = null;
    private List<StudyGroup> studyGroupCollection;
    private final ReentrantLock locker;
    private final Date initializationDate;

    public static CollectionService getInstance() {
        if (instance == null) instance = new CollectionService();
        return instance;
    }

    public CollectionService() {
        studyGroupCollection = Collections.synchronizedList(new LinkedList<>());
        locker = new ReentrantLock();
        initializationDate = new Date();
    }

    public LinkedList<StudyGroup> getCollection() {
        return (LinkedList<StudyGroup>) studyGroupCollection;
    }

    public void setCollection(LinkedList<StudyGroup> studyGroupCollection) {
        this.studyGroupCollection = studyGroupCollection;
    }

    public String getCollectionHead() {
        try {
            collectionServiceLogger.trace("Trying to get collection head");
            return getStudyGroupInfo(studyGroupCollection.get(0));
        }
        catch (IndexOutOfBoundsException e) {
            collectionServiceLogger.error("Collection is empty: impossible to get element!");
            return "Collection is empty: impossible to get element!";
//            return "Невозможно получить элемент коллекции: коллекция пуста!";
        }
    }

    public String getAllStudyGroupsInfo() {
        locker.lock();
        StringBuilder studyGroupsInfo = new StringBuilder();
        if (studyGroupCollection.isEmpty()) {
            collectionServiceLogger.warn("Collection is empty: impossible to get element!");
            locker.unlock();
            return "Collection is empty: impossible to get element!";
//            return "Невозможно получить элемент коллекции: коллекция пуста!";
        }
        studyGroupCollection.forEach(studyGroup ->
                studyGroupsInfo.append(getStudyGroupInfo(studyGroup)).append("\n\n"));
        String result = studyGroupsInfo.toString();

        collectionServiceLogger.trace("Study groups info is gotten");
        locker.unlock();
        return result.substring(0, result.length() - 2);
    }

    public String getStudyGroupInfo(StudyGroup studyGroup) {
        return studyGroup.toString();
    }

    public String getCollectionInfo() {
//        String collectionType = "Тип коллекции: " + studyGroupCollection.getClass().getName();
        String collectionType = "Collection type: " + studyGroupCollection.getClass().getName();
//        String collectionInitializationDate = "Время инициализации коллекции: " + initializationDate;
        String collectionInitializationDate = "Collection initialization time: " + initializationDate;
//        String collectionElementsNumber = "Количество элементов коллекции: " + studyGroupCollection.size();
        String collectionElementsNumber = "Size of collection: " + studyGroupCollection.size();
        return collectionType + "\n" + collectionInitializationDate + "\n" + collectionElementsNumber;
    }

    public String addStudyGroup(StudyGroup studyGroup) {
        locker.lock();
        String responseMessage;
        int studyGroupId = DataBaseService.saveStudyGroup(studyGroup);
        if (studyGroupId != -1) {
            studyGroup.setId(studyGroupId);
            studyGroupCollection.add(studyGroup);

//        "Новый элемент был успешно добавлен в коллекцию";
            responseMessage = "New element was successfully added to collection";
        }
        else {
            collectionServiceLogger.error("Error occurred while saving study group to database!");
            responseMessage = "Error occurred while saving study group to database!";
        }
        locker.unlock();
        return responseMessage;
    }

    public synchronized boolean validateId(Integer id) {
        return studyGroupCollection.stream().anyMatch(studyGroup -> studyGroup.compareId(id));
    }

    public String removeById(Integer id, String username) {
        locker.lock();
        String responseMessage;
        if (DataBaseService.removeStudyGroup(id, username)) {
            int index = IntStream.range(0, studyGroupCollection.size())
                    .filter(i -> studyGroupCollection.get(i).compareId(id))
                    .findFirst()
                    .orElse(-1);
            if (studyGroupCollection.get(index).getCreator().equals(username)) {
                studyGroupCollection.removeIf(studyGroup -> studyGroup.compareId(id));
                responseMessage = "Object with id=" + id + " was successfully deleted";
            }
            else responseMessage = "You cannot delete object with id=" + id + ", because you are not its creator!";
        }
        else responseMessage = "Error occurred while trying to delete object with id=" + id + " from database";
//        return "Объект по данному id успешно удален";
        locker.unlock();
        return responseMessage;
    }

    public String updateById(Integer id, StudyGroup providedStudyGroup) {
        locker.lock();
        String responseMessage;
        if (DataBaseService.updateStudyGroup(providedStudyGroup, id)) {
            int index = IntStream.range(0, studyGroupCollection.size())
                    .filter(i -> studyGroupCollection.get(i).compareId(id))
                    .findFirst()
                    .orElse(-1);

            if (providedStudyGroup.getCreator().equals(studyGroupCollection.get(index).getCreator())) {
                providedStudyGroup.setId(id);
                studyGroupCollection.set(index, providedStudyGroup);
                responseMessage = "Object with id=" + id + " was successfully updated";
            }
            else responseMessage = "You cannot modify object with id=" + id + ", because you are not its creator!";
        }
        else responseMessage = "Error occurred while trying to modify element with id=" + id + " in database";
//        return "Объект по заданному id был успешно обновлен";
        locker.unlock();
        return responseMessage;
    }
}
