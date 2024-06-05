package io.database;

import collection.CollectionService;
import collection.data.*;
import exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class DataBaseService {
    private static final String INSERT_USER = "INSERT INTO users (username, hashed_password) VALUES (?, ?)";
    private static final String CHECK_USER_EXISTENCE = "SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)";
    private static final String CHECK_USER_PASSWORD = "SELECT hashed_password FROM users WHERE username = ?";
    private static final String SAVE_STUDY_GROUP = "INSERT INTO study_groups (name, coordinates_id," +
            " creation_date, students_count, should_be_expelled, form_of_education, semester, admin_id, creator_id) " +
            "VALUES (?, ?, ?, ?, ?, ?::form_of_education, ?::semester, ?, (SELECT id FROM users WHERE username = ?))";
    private static final String SAVE_COORDINATES = "INSERT INTO coordinates (x, y) VALUES (?, ?)";
    private static final String SAVE_LOCATION = "INSERT INTO locations (x, y, z, name) VALUES (?, ?, ?, ?)";
    private static final String SAVE_PERSON = "INSERT INTO persons (name, weight," +
            " passport_id, eye_color, location_id) VALUES (?, ?, ?, ?::color, ?)";
    private static final String LOAD_STUDY_GROUP = "SELECT * FROM study_groups";
    private static final String LOAD_COORDINATES = "SELECT * FROM coordinates WHERE id = ?";
    private static final String LOAD_PERSON = "SELECT * FROM persons WHERE id = ?";
    private static final String LOAD_LOCATION = "SELECT * FROM locations WHERE id = ?";
    private static final String LOAD_CREATOR = "SELECT * FROM users WHERE id = ?";
    private static final String UPDATE_STUDY_GROUP = "UPDATE study_groups SET name = ?, coordinates_id = ?," +
            " creation_date = ?, students_count = ?, should_be_expelled = ?," +
            " form_of_education = ?::form_of_education, semester = ?::semester, admin_id = ?" +
            " WHERE (id = ? AND creator_id IN (SELECT id FROM users WHERE username = ?))";
    private static final String REMOVE_STUDY_GROUP = "DELETE FROM study_groups WHERE id = ? AND " +
            "creator_id = (SELECT id FROM users WHERE username = ?)";
    private static final Logger logger = LogManager.getLogger();
    private static Connection connection;

    public static boolean establishConnection(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);

            if (connection == null) {
                logger.error("Connection with database failed!");
                return false;
            }
            logger.info("Connection with database established");
            return true;

        } catch (Exception exception) {
            logger.error("Connection with database failed: " + exception);
            return false;
        }
    }

    public static boolean removeStudyGroup(int id, String username) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_STUDY_GROUP)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
            return true;

        } catch (SQLException exception) {
            logger.error("Error occurred while trying to remove studyGroup {}", id);
        }
        return false;
    }

    public static boolean updateStudyGroup(StudyGroup providedStudyGroup, int id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STUDY_GROUP)) {
            preparedStatement.setString(1, providedStudyGroup.getName());

            int coordinatesID = saveCoordinates(providedStudyGroup.getCoordinates());
            if (coordinatesID == -1) {
                logger.error("Error occurred while trying to add coordinates to studyGroup");
                return false;
            }

            preparedStatement.setInt(2, coordinatesID);
            preparedStatement.setTimestamp(3, new Timestamp(providedStudyGroup.getCreationDate().getTime()));

            if (providedStudyGroup.getStudentsCount() != null) preparedStatement.setLong(4, providedStudyGroup.getStudentsCount());
            else preparedStatement.setNull(4, Types.BIGINT);

            preparedStatement.setLong(5, providedStudyGroup.getShouldBeExpelled());
            preparedStatement.setObject(6, providedStudyGroup.getFormOfEducation().toString());
            preparedStatement.setObject(7, providedStudyGroup.getSemester().toString());

            int adminID = savePerson(providedStudyGroup.getGroupAdmin());
            if (adminID == -1) {
                logger.error("Error occurred while trying to add admin to studyGroup");
                return false;
            }

            preparedStatement.setInt(8, adminID);

            preparedStatement.setInt(9, id);
            preparedStatement.setString(10, providedStudyGroup.getCreator());

            preparedStatement.executeUpdate();
            return true;

        } catch (SQLException exception) {
            logger.error("Error occurred while trying to update studyGroup: " + exception.getMessage());
        }
        return false;
    }

    public static void loadCollection() {
        try (PreparedStatement preparedStatement = connection.prepareStatement(LOAD_STUDY_GROUP, Statement.RETURN_GENERATED_KEYS)) {
            LinkedList<StudyGroup> studyGroupCollection = new LinkedList<>();
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                StudyGroup studyGroup = new StudyGroup();
                studyGroup.setId(result.getInt("id"));
                studyGroup.setName(result.getString("name"));
                studyGroup.setCreationDate(result.getTimestamp("creation_date"));
                studyGroup.setCoordinates(loadCoordinates(result.getInt("coordinates_id")));
                studyGroup.setStudentsCount(result.getLong("students_count"));
                studyGroup.setShouldBeExpelled(result.getLong("should_be_expelled"));
                studyGroup.setFormOfEducation(FormOfEducation.valueOf(result.getString("form_of_education")));
                studyGroup.setSemester(Semester.valueOf(result.getString("semester")));
                studyGroup.setGroupAdmin(loadPerson(result.getInt("admin_id")));
                studyGroup.setCreator(loadCreator(result.getInt("creator_id")));

                studyGroupCollection.add(studyGroup);
            }
            CollectionService.getInstance().setCollection(studyGroupCollection);

            CollectionService.getInstance().setCollection(
                    CollectionService.getInstance().getCollection().stream()
                            .filter(studyGroup -> {
                                try {
                                    studyGroup.validateStudyGroup();
                                    logger.trace("Study group " + studyGroup.getName() + " validated");
                                    return true;
                                } catch (InvalidInputException exception) {
                                    logger.error("Error reading database: fiels is incorrect!");
                                    return false;
                                }
                            })
                            .collect(Collectors.toCollection(LinkedList<StudyGroup>::new))
            );

        } catch (SQLException exception) {
            logger.error("Error occurred while trying to load collection: " + exception.getMessage());
        }
    }

    private static String loadCreator(int id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(LOAD_CREATOR)) {
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) return result.getString("username");

        } catch (SQLException exception) {
            logger.error("Error occurred while trying to load creator: " + exception.getMessage());
        }
        return null;
    }

    private static Person loadPerson(int id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(LOAD_PERSON)) {
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();

            Person person = new Person();
            if (result.next()) {
                person.setName(result.getString("name"));
                person.setWeight(result.getLong("weight"));
                person.setPassportID(result.getString("passport_id"));

                String eye_color = result.getString("eye_color");
                if (eye_color != null && !eye_color.isBlank()) person.setEyeColor(Color.valueOf(eye_color));

                person.setLocation(loadLocation(result.getInt("location_id")));

                return person;
            }

        } catch (SQLException exception) {
            logger.error("Error occurred while trying to load person: " + exception.getMessage());
        }
        return null;
    }

    private static Location loadLocation(int id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(LOAD_LOCATION)) {
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();

            Location location = new Location();
            if (result.next()) {
                location.setName(result.getString("name"));
                location.setX(result.getDouble("x"));
                location.setY(result.getDouble("y"));
                location.setZ(result.getInt("z"));

                return location;
            }

        } catch (SQLException exception) {
            logger.error("Error occurred while trying to load location: " + exception.getMessage());
        }
        return null;
    }

    private static Coordinates loadCoordinates(int id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(LOAD_COORDINATES)) {
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();

            Coordinates coordinates = new Coordinates();
            if (result.next()) {
                coordinates.setX(result.getLong("x"));
                coordinates.setY(result.getDouble("y"));
                return coordinates;
            }

        } catch (SQLException exception) {
            logger.error("Error occurred while trying to load coordinates: " + exception.getMessage());
        }
        return null;
    }

    private static int saveLocation(Location location) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SAVE_LOCATION, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setDouble(1, location.getX());
            preparedStatement.setDouble(2, location.getY());
            preparedStatement.setInt(3, location.getZ());
            preparedStatement.setString(4, location.getName());
            preparedStatement.executeUpdate();

            ResultSet result = preparedStatement.getGeneratedKeys();
            if (result.next()) return result.getInt("id");

        } catch (SQLException exception) {
            logger.error("Error occurred while trying to add new location to database: " + exception.getMessage());
        }
        return -1;
    }

    private static int savePerson(Person person) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SAVE_PERSON, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, person.getName());
            preparedStatement.setLong(2, person.getWeight());
            preparedStatement.setString(3, person.getPassportID());

            if (person.getEyeColor() != null) preparedStatement.setObject(4, person.getEyeColor().toString());
            else preparedStatement.setNull(4, Types.OTHER);

            if (person.getLocation() != null) {
                logger.trace("Location is not null: trying to save");
                int locationID = saveLocation(person.getLocation());
                if (locationID == -1) {
                    logger.error("Error occurred while trying to add location to person");
                    return -1;
                }
                preparedStatement.setInt(5, locationID);
            } else preparedStatement.setNull(5, Types.INTEGER);
            preparedStatement.executeUpdate();

            ResultSet result = preparedStatement.getGeneratedKeys();
            if (result.next()) return result.getInt("id");

        } catch (SQLException exception) {
            logger.error("Error occurred while trying to add new person to database: " + exception.getMessage());
        }
        return -1;
    }

    private static int saveCoordinates(Coordinates coordinates) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SAVE_COORDINATES, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, coordinates.getX());
            preparedStatement.setDouble(2, coordinates.getY());
            preparedStatement.executeUpdate();

            ResultSet result = preparedStatement.getGeneratedKeys();
            if (result.next()) return result.getInt("id");

        } catch (SQLException exception) {
            logger.error("Error occurred while trying to add new coordinates to database: " + exception.getMessage());
        }
        return -1;
    }

    public static int saveStudyGroup(StudyGroup studyGroup) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SAVE_STUDY_GROUP, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, studyGroup.getName());

            int coordinatesID = saveCoordinates(studyGroup.getCoordinates());
            if (coordinatesID == -1) {
                logger.error("Error occurred while trying to add coordinates to studyGroup");
                return -1;
            }

            preparedStatement.setInt(2, coordinatesID);
            preparedStatement.setTimestamp(3, new Timestamp(studyGroup.getCreationDate().getTime()));
            preparedStatement.setLong(4, studyGroup.getStudentsCount());
            preparedStatement.setLong(5, studyGroup.getShouldBeExpelled());
            preparedStatement.setObject(6, studyGroup.getFormOfEducation().toString());
            preparedStatement.setObject(7, studyGroup.getSemester().toString());

            int adminID = savePerson(studyGroup.getGroupAdmin());
            if (adminID == -1) {
                logger.error("Error occurred while trying to add admin to studyGroup");
                return -1;
            }

            preparedStatement.setInt(8, adminID);
            preparedStatement.setString(9, studyGroup.getCreator());

            preparedStatement.executeUpdate();

            ResultSet result = preparedStatement.getGeneratedKeys();
            if (result.next()) return result.getInt("id");

        } catch (SQLException exception) {
            logger.error("Error occurred while trying to add new object to database: " + exception.getMessage());
        }
        return -1;
    }

    public static boolean checkUserExistence(String username) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CHECK_USER_EXISTENCE)) {
            preparedStatement.setString(1, username);
            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) return result.getBoolean(1);

        } catch (SQLException exception) {
            logger.error("Error occurred while trying to check user existence: " + exception.getMessage());
        }
        return false;
    }

    public static boolean saveUser(String username, String password) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER)) {
            preparedStatement.setString(1, username);

            String hashedPassword = getHashedPassword(password);
            if (hashedPassword == null) return false;

            preparedStatement.setString(2, hashedPassword);
            preparedStatement.executeUpdate();
            return true;

        } catch (SQLException exception) {
            logger.error("Error occurred while trying to add new user: " + exception.getMessage());
        }
        return false;
    }

    public static boolean validateUserPassword(String username, String password) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CHECK_USER_PASSWORD)) {
            preparedStatement.setString(1, username);
            ResultSet result = preparedStatement.executeQuery();
            String storedHashedPassword;

            if (result.next()) {
                storedHashedPassword = result.getString("hashed_password");
                String hashedProvidedPassword = getHashedPassword(password);

                if (hashedProvidedPassword == null) return false;
                return storedHashedPassword.equals(hashedProvidedPassword);
            }

        } catch (SQLException exception) {
            logger.error("Error occurred while trying to validate user password: " + exception.getMessage());
        }
        return false;
    }

    private static String getHashedPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] hashedPassword = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashedPassword) stringBuilder.append(String.format("%02x", b));
            return stringBuilder.toString();

        } catch (NoSuchAlgorithmException exception) {
            logger.error("Error occurred while trying to generate hashed password: " + exception.getMessage());
            return null;
        }
    }
}
