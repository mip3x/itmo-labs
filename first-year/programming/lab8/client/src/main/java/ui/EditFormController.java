package ui;

import collection.data.*;
import command.Command;
import command.list.Add;
import command.list.Update;
import dto.CommandDto;
import dto.UserDto;
import exception.InvalidInputException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.Client;
import transfer.Response;
import validation.ValidationStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class EditFormController {
    @FXML
    private VBox root;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField xField;
    @FXML
    private TextField yField;
    @FXML
    private TextField studentsCountField;
    @FXML
    private TextField shouldBeExpelledField;
    @FXML
    private ComboBox<String> formOfEducationBox;
    @FXML
    private ComboBox<String> semesterBox;
    @FXML
    private TextField groupAdminNameField;
    @FXML
    private TextField groupAdminWeightField;
    @FXML
    private TextField groupAdminPassportIdField;
    @FXML
    private ComboBox<String> groupAdminEyeColorBox;
    @FXML
    private TextField locationNameField;
    @FXML
    private TextField locationXField;
    @FXML
    private TextField locationYField;
    @FXML
    private TextField locationZField;
    private StudyGroup studyGroup = null;
    private ResourceBundle bundle;
    private final Logger logger = LogManager.getLogger();
    private final Map<String, FormOfEducation> formOfEducationMap = new HashMap<>();
    private final Map<String, Semester> semesterMap = new HashMap<>();
    private final Map<String, Color> colorMap = new HashMap<>();
    private final Client client = Client.getInstance();
    private boolean addStudyGroupFlag;

    @FXML
    public void initialize() {
        addStudyGroupFlag = true;
        bundle = AuthController.getBundle();

        for (FormOfEducation form : FormOfEducation.values()) {
            String localizedValue = bundle.getString(form.name().toLowerCase());
            formOfEducationBox.getItems().add(localizedValue);
            formOfEducationMap.put(localizedValue, form);
        }

        for (Semester semester : Semester.values()) {
            String localizedValue = bundle.getString(semester.name().toLowerCase());
            semesterBox.getItems().add(localizedValue);
            semesterMap.put(localizedValue, semester);
        }

        for (Color color : Color.values()) {
            String localizedValue = bundle.getString(color.name().toLowerCase());
            groupAdminEyeColorBox.getItems().add(localizedValue);
            colorMap.put(localizedValue, color);
        }

        groupAdminEyeColorBox.getItems().add(null);

        saveButton.setOnAction(event -> handleSave());
        cancelButton.setOnAction(event -> handleCancel());

        root.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!event.getTarget().equals(cancelButton) &&
                    !event.getTarget().equals(saveButton) &&
                    !event.getTarget().equals(nameField) &&
                    !event.getTarget().equals(xField) &&
                    !event.getTarget().equals(yField) &&
                    !event.getTarget().equals(studentsCountField) &&
                    !event.getTarget().equals(shouldBeExpelledField) &&
                    !event.getTarget().equals(formOfEducationBox) &&
                    !event.getTarget().equals(semesterBox) &&
                    !event.getTarget().equals(groupAdminNameField) &&
                    !event.getTarget().equals(groupAdminWeightField) &&
                    !event.getTarget().equals(groupAdminPassportIdField) &&
                    !event.getTarget().equals(groupAdminEyeColorBox) &&
                    !event.getTarget().equals(locationNameField) &&
                    !event.getTarget().equals(locationXField) &&
                    !event.getTarget().equals(locationYField) &&
                    !event.getTarget().equals(locationZField)) {
                root.requestFocus();
            }
        });

        String longRegex = "-?([0-9]*)?";
        String doubleRegex = "-?([0-9]*[.,])?[0-9]*";
        addFieldValidation(xField, longRegex);
        addFieldValidation(yField, doubleRegex);
        addFieldValidation(studentsCountField, longRegex);
        addFieldValidation(shouldBeExpelledField, longRegex);
        addFieldValidation(groupAdminWeightField, longRegex);
        addFieldValidation(locationXField, doubleRegex);
        addFieldValidation(locationYField, doubleRegex);
        addFieldValidation(locationZField, longRegex);

        updateTexts();
    }

    private void addFieldValidation(TextField textField, String regex) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches(regex)) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    public void setStudyGroup(StudyGroup studyGroup) {
        this.studyGroup = studyGroup;

        if (studyGroup != null) {
            addStudyGroupFlag = false;

            nameField.setText(studyGroup.getName());
            xField.setText(String.valueOf(studyGroup.getCoordinates().getX()));
            yField.setText(String.valueOf(studyGroup.getCoordinates().getY()));
            studentsCountField.setText(String.valueOf(studyGroup.getStudentsCount()));
            shouldBeExpelledField.setText(String.valueOf(studyGroup.getShouldBeExpelled()));

            FormOfEducation formOfEducation = studyGroup.getFormOfEducation();
            formOfEducationBox.setValue(bundle.getString(formOfEducation.toString().toLowerCase()));

            Semester semester = studyGroup.getSemester();
            semesterBox.setValue(bundle.getString(semester.toString().toLowerCase()));

            groupAdminNameField.setText(studyGroup.getGroupAdmin().getName());
            groupAdminWeightField.setText(String.valueOf(studyGroup.getGroupAdmin().getWeight()));
            groupAdminPassportIdField.setText(studyGroup.getGroupAdmin().getPassportID());

            if (studyGroup.getGroupAdmin().getEyeColor() != null) {
                Color groupAdminEyeColor = studyGroup.getGroupAdmin().getEyeColor();
                groupAdminEyeColorBox.setValue(bundle.getString(groupAdminEyeColor.toString().toLowerCase()));
            }

            if (studyGroup.getGroupAdmin().getLocation() != null) {
                locationNameField.setText(studyGroup.getGroupAdmin().getLocation().getName());
                locationXField.setText(String.valueOf(studyGroup.getGroupAdmin().getLocation().getX()));
                locationYField.setText(String.valueOf(studyGroup.getGroupAdmin().getLocation().getY()));
                locationZField.setText(String.valueOf(studyGroup.getGroupAdmin().getLocation().getZ()));
            }
        }
    }

    @FXML
    private void handleSave() {
        try {
            if (studyGroup == null) studyGroup = new StudyGroup();

            studyGroup.setName(nameField.getText());

            Coordinates coordinates;
            if (studyGroup.getCoordinates() == null) coordinates = new Coordinates();
            else coordinates = studyGroup.getCoordinates();

            if (xField.getText().isEmpty()) throw new InvalidInputException("error.sg.x.empty");
            coordinates.setX(Long.parseLong(xField.getText()));

            if (yField.getText().isEmpty()) throw new InvalidInputException("error.sg.y.empty");
            coordinates.setY(Double.parseDouble(yField.getText()));

            studyGroup.setCoordinates(coordinates);

            if (studentsCountField.getText().isEmpty()) throw new InvalidInputException("error.sg.students_count.empty");
            studyGroup.setStudentsCount(Long.parseLong(studentsCountField.getText()));

            if (shouldBeExpelledField.getText().isEmpty()) throw new InvalidInputException("error.sg.should_be_expelled.empty");
            studyGroup.setShouldBeExpelled(Long.parseLong(shouldBeExpelledField.getText()));

            String selectedFormOfEducation = formOfEducationBox.getSelectionModel().getSelectedItem();
            studyGroup.setFormOfEducation(formOfEducationMap.get(selectedFormOfEducation));

            String selectedSemester = semesterBox.getSelectionModel().getSelectedItem();
            studyGroup.setSemester(semesterMap.get(selectedSemester));

            Person groupAdmin;
            if (studyGroup.getGroupAdmin() == null) groupAdmin = new Person();
            else groupAdmin = studyGroup.getGroupAdmin();

            groupAdmin.setName(groupAdminNameField.getText());

            if (groupAdminWeightField.getText().isEmpty()) throw new InvalidInputException("error.sg.ga.weight.empty");
            groupAdmin.setWeight(Long.parseLong(groupAdminWeightField.getText()));

            String oldPassportId = groupAdmin.getPassportID();
            if (!groupAdminPassportIdField.getText().equals(oldPassportId)) groupAdmin.setPassportID(groupAdminPassportIdField.getText());

            if (groupAdminEyeColorBox.getValue() == null) groupAdmin.setEyeColor(null);
            else {
                String selectedColor = groupAdminEyeColorBox.getSelectionModel().getSelectedItem();
                groupAdmin.setEyeColor(colorMap.get(selectedColor));
            }

            Location location = getLocation();

            groupAdmin.setLocation(location);
            studyGroup.setGroupAdmin(groupAdmin);

            logger.trace("ADD_STUDY_GROUP FLAG {}", addStudyGroupFlag);

            if (addStudyGroupFlag) {
                CommandDto addCommandDto = new CommandDto(new Add());
                addCommandDto.setStudyGroup(studyGroup);

                Response response = client.handleRequest(addCommandDto, new UserDto(client.getUser(), false));
                if (response == null) {
                    logger.error("RESPONSE IS NULL");
                    return;
                }

                if (response.getStatus().equals(ValidationStatus.EXECUTION_ERROR)) {
                    showErrorMessage(bundle.getString("error.save") + "\n" + bundle.getString("error.unique"));
                }

                if (response.getStatus().equals(ValidationStatus.SUCCESS)) {
                    showSuccessMessage(bundle.getString("success.collection_addition"));

                    studyGroup = null;
                    Stage stage = (Stage) nameField.getScene().getWindow();
                    stage.close();
                }
            }
            else {
                List<String> selectedGroupId = List.of(studyGroup.getId().toString());
                CommandDto editCommandDto = new CommandDto(new Update(), selectedGroupId);
                editCommandDto.setStudyGroup(studyGroup);

                Response response = client.handleRequest(editCommandDto, new UserDto(client.getUser(), false));
                if (response == null) {
                    logger.error("RESPONSE IS NULL");
                    return;
                }

                if (response.getStatus().equals(ValidationStatus.EXECUTION_ERROR))
                    if (response.getMessage().contains("id")) showErrorMessage(bundle.getString("error.save") + "\n" + bundle.getString("error.editing.permission_insufficient"));
                    else showErrorMessage(bundle.getString("error.save") + "\n" + response.getMessage());

                if (response.getStatus().equals(ValidationStatus.SUCCESS)) {
                    showSuccessMessage(bundle.getString("success.collection_edit"));

                    studyGroup = null;
                    Stage stage = (Stage) nameField.getScene().getWindow();
                    stage.close();
                }
            }
        }
        catch (InvalidInputException exception) {
            showErrorMessage(bundle.getString("error.save") + "\n" + bundle.getString(exception.getMessage()));
        }
    }

    private Location getLocation() {
        Location location = null;
        if (!locationNameField.getText().isBlank()) {
            location = new Location();
            location.setName(locationNameField.getText());

            if (locationXField.getText().isEmpty()) throw new InvalidInputException("error.sg.ga.location.x");
            location.setX(Double.parseDouble(locationXField.getText()));

            if (locationYField.getText().isEmpty()) throw new InvalidInputException("error.sg.ga.location.y");
            location.setY(Double.parseDouble(locationYField.getText()));

            if (locationZField.getText().isEmpty()) throw new InvalidInputException("error.sg.ga.location.z");
            location.setZ(Integer.parseInt(locationZField.getText()));
        }
        return location;
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString("success.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(bundle.getString("error_text"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void updateTexts() {
        saveButton.setText(bundle.getString("button.save"));
        cancelButton.setText(bundle.getString("button.cancel"));
    }
}
