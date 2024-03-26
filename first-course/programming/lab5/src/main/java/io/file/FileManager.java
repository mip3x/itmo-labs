package io.file;

import collection.CollectionManager;
import collection.data.Coordinates;
import collection.data.Person;
import collection.data.StudyGroup;
import exception.InvalidInputException;

import java.io.*;
import java.text.MessageFormat;
import javax.xml.bind.*;

public class FileManager {
    private static String filePath;

    public static void setFilePath(String path) throws Exception {
        File file = new File(path);

        if (!file.exists()) {
            throw new Exception(MessageFormat.format("Файла {0} не существует!", path));
        }

        filePath = path;
    }

    public static void loadCollection() throws IOException, JAXBException {
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

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

            unmarshaller.setEventHandler(event -> {
                if (event.getLinkedException() instanceof InvalidInputException)
                    throw new InvalidInputException(event.getMessage());
                return true;
            });

            StringReader xmlReader = new StringReader(xmlData.toString());

            CollectionManager.getInstance().setStudyGroupCollection(
                    ((CollectionManager) unmarshaller.unmarshal(xmlReader)).getStudyGroupCollection());

        }
        catch (IOException exception) {
            throw new IOException("Ошибка при чтении файла: не достаточно прав доступа!");
        }
        catch (JAXBException exception) {
            throw new JAXBException("Ошибка при чтении файла: файл пуст или данные некорректны!");
        }
    }
    public static void saveCollection() throws IOException, JAXBException {
        try {
            JAXBContext context = JAXBContext.newInstance(Person.class,
                    Coordinates.class,
                    StudyGroup.class,
                    CollectionManager.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            try {
                FileWriter fileWriter = new FileWriter(filePath);
                marshaller.marshal(CollectionManager.getInstance(), fileWriter);
            } catch (IOException exception) {
                throw new IOException("Ошибка при записи в файл: не достаточно прав доступа!");
            }
        } catch (JAXBException exception) {
            throw new JAXBException("Ошибка при обработке коллекции в xml!");
        }
    }
}