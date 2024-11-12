package ui;

import command.list.RemoveLower;
import dto.CommandDto;
import dto.UserDto;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.Client;
import transfer.Response;

import java.util.List;
import java.util.ResourceBundle;

public class DeleteAllLowerController {
    @FXML
    private VBox root;
    @FXML
    private Label enterNumber;
    @FXML
    private TextField numberField;

    @FXML
    private Button deleteButton;

    @FXML
    private Button cancelButton;
    private ResourceBundle bundle;
    private final Client client = Client.getInstance();
    private final Logger logger = LogManager.getLogger();

    @FXML
    public void initialize() {
        bundle = AuthController.getBundle();

        deleteButton.setOnAction(event -> handleDelete());
        cancelButton.setOnAction(event -> handleCancel());

        numberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                numberField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        root.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!event.getTarget().equals(cancelButton) &&
                    !event.getTarget().equals(enterNumber) &&
                    !event.getTarget().equals(deleteButton) &&
                    !event.getTarget().equals(cancelButton)) {
                root.requestFocus();
            }
        });

        updateTexts();
    }

    @FXML
    private void handleDelete() {
        String input = numberField.getText();

        if (input.isEmpty()) {
            showErrorMessage(bundle.getString("error.number.empty"));
            return;
        }
        int number = Integer.parseInt(input);
        CommandDto deleteLowerCommandDto = new CommandDto(new RemoveLower(), List.of(String.valueOf(number)));

        Response response = client.handleRequest(deleteLowerCommandDto, new UserDto(client.getUser(), false));
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
        enterNumber.setText(bundle.getString("delete_all_lower.number.prompt"));
    }
}
