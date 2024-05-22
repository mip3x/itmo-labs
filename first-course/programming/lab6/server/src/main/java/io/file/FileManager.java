package io.file;

import collection.CollectionManager;
import collection.data.Coordinates;
import collection.data.Person;
import collection.data.StudyGroup;
import exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for managing files
 */
public class FileManager {
    private static final Logger fileManagerLogger = LogManager.getLogger();
    private static String filePath;

    /**
     * Sets file path to file
     * @param path Path to file
     * @throws Exception Throws in case file doesn't exist
     */
    public static void setFilePath(String path) throws Exception {
        File file = new File(path);

        if (!file.exists()) {
            fileManagerLogger.error("File does not exist!");
            throw new Exception(MessageFormat.format("File {0} doesn't exist!", path));
        }

        filePath = path;
        fileManagerLogger.trace("File path has been successfully set");
    }

    /**
     * Loads collection from file
     * @throws IOException Throws in case lack of permission to file
     * @throws JAXBException Throws in case not all file's objects are valid
     */
    public static void loadCollection() throws IOException, JAXBException {
        if (!Files.isReadable(Path.of(filePath))) {
            fileManagerLogger.error("Error reading file: insufficient access rights!");
            throw new IOException("Error reading file: insufficient access rights!");
        }

        try(FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);) {

            String line;
            StringBuilder xmlData = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                xmlData.append(line.trim());
            }

            JAXBContext context = JAXBContext.newInstance(CollectionManager.class,
                    StudyGroup.class,
                    Coordinates.class,
                    Person.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            StringReader xmlReader = new StringReader(xmlData.toString());

            CollectionManager.getInstance().setStudyGroupCollection(
                    ((CollectionManager) unmarshaller.unmarshal(xmlReader)).getStudyGroupCollection());

            int parsedGroupsCount = CollectionManager.getInstance().getStudyGroupCollection().size();
            List<String> exceptionMessages = new ArrayList<>();

            CollectionManager.getInstance().setStudyGroupCollection(
                    CollectionManager.getInstance().getStudyGroupCollection().stream()
                            .filter(studyGroup -> {
                                try {
                                    studyGroup.validateStudyGroup();
                                    fileManagerLogger.trace("Study group validated");
                                    return true;
                                } catch (InvalidInputException exception) {
                                    fileManagerLogger.error("Error reading file: fiels is incorrect!");
                                    exceptionMessages.add("Field '" + exception.getMessage().split("'")[1] + "' is incorrect:\n" + exception.getMessage());
                                    return false;
                                }
                            })
                            .collect(Collectors.toCollection(LinkedList<StudyGroup>::new))
            );

            int validGroupsCount = CollectionManager.getInstance().getStudyGroupCollection().size();

            if (validGroupsCount == 0)
            {
                fileManagerLogger.error("There is not a single valid object in the file!");
                throw new InvalidInputException(
                        MessageFormat.format("There is not a single valid object in the file!\n{0}",
                                String.join("\n", exceptionMessages))
                );
            }

            else if (validGroupsCount < parsedGroupsCount) {
                fileManagerLogger.error("Not all objects in the file are valid!");
                throw new InvalidInputException(
                        MessageFormat.format("Not all objects in the file are valid!\n{0}",
                                String.join("\n", exceptionMessages))
                );
            }
        }
        catch (JAXBException exception) {
            fileManagerLogger.error("Error reading file: The file is empty or the data is incorrect!");
            throw new JAXBException("Error reading file: The file is empty or the data is incorrect!");
        }
    }

    /**
     * Saves collection to file
     * @throws IOException Throws in case lack of permission to file
     * @throws JAXBException Throws in case error with convert collection to XML
     */
    public static void saveCollection() throws IOException, JAXBException {
        try {
            JAXBContext context = JAXBContext.newInstance(Person.class,
                    Coordinates.class,
                    StudyGroup.class,
                    CollectionManager.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            if (!Files.isWritable(Path.of(filePath))){
                fileManagerLogger.error("Error writing file: insufficient access rights!");
                throw new IOException("Ошибка при записи в файл: не достаточно прав доступа!");
            }

            FileWriter fileWriter = new FileWriter(filePath);
            marshaller.marshal(CollectionManager.getInstance(), fileWriter);

        } catch (JAXBException exception) {
            fileManagerLogger.error("Error processing the collection in xml!");
            throw new JAXBException("Error processing the collection in xml!");
        }
    }
}