package ui;

import collection.data.FormOfEducation;
import command.list.RemoveAnyByFormOfEducation;
import dto.CommandDto;
import dto.UserDto;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.Client;
import transfer.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class DeleteAnyByFOEController {
    @FXML
    private VBox root;
    @FXML
    private Label enterFOE;
    @FXML
    private ComboBox<String> formOfEducationBox;
    @FXML
    private Button deleteButton;
    private final Map<String, FormOfEducation> formOfEducationMap = new HashMap<>();
    @FXML
    private Button cancelButton;
    private ResourceBundle bundle;
    private final Client client = Client.getInstance();
    private final Logger logger = LogManager.getLogger();

    @FXML
    public void initialize() {
        bundle = AuthController.getBundle();

        for (FormOfEducation form : FormOfEducation.values()) {
            String localizedValue = bundle.getString(form.name().toLowerCase());
            formOfEducationBox.getItems().add(localizedValue);
            formOfEducationMap.put(localizedValue, form);
        }

        deleteButton.setOnAction(event -> handleDelete());
        cancelButton.setOnAction(event -> handleCancel());

        root.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!event.getTarget().equals(cancelButton) &&
                    !event.getTarget().equals(formOfEducationBox) &&
                    !event.getTarget().equals(enterFOE) &&
                    !event.getTarget().equals(deleteButton) &&
                    !event.getTarget().equals(cancelButton)) {
                root.requestFocus();
            }
        });

        updateTexts();
    }

    @FXML
    private void handleDelete() {
        String selectedFormOfEducation = formOfEducationBox.getSelectionModel().getSelectedItem();

        if (selectedFormOfEducation.isEmpty()) {
            showErrorMessage(bundle.getString("error.sg.foe.empty"));
            return;
        }
        FormOfEducation formOfEducation = formOfEducationMap.get(selectedFormOfEducation);
        CommandDto deleteAnyByFOE = new CommandDto(new RemoveAnyByFormOfEducation(), List.of(String.valueOf(formOfEducation)));

        Response response = client.handleRequest(deleteAnyByFOE, new UserDto(client.getUser(), false));
        if (response == null) {
            logger.error("RESPONSE IS NULL");
            return;
        }

        showSuccessMessage(response.getMessage());
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
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

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void updateTexts() {
        deleteButton.setText(bundle.getString("delete_all_lower.delete_button"));
        cancelButton.setText(bundle.getString("delete_all_lower.cancel_button"));
        enterFOE.setText(bundle.getString("delete_foe.text"));
    }
}
