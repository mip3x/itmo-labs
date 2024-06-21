package ui;

import collection.CollectionService;
import collection.data.StudyGroup;
import collection.data.User;
import dto.UserDto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.Client;
import tcp.Updater;
import transfer.Response;
import validation.ValidationStatus;

import java.io.IOException;
import java.util.*;

public class AuthController {
    private final Logger logger = LogManager.getLogger();
    @FXML
    private VBox root;
    @FXML
    private Label titleLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button switchPageButton;
    @FXML
    private Button actionButton;
    @FXML
    private ComboBox<Locale> languageSelector;
    @FXML
    private Label errorLabel;
    private static ResourceBundle bundle;
    private static Locale currentLocale;
    private Stage stage;
    private boolean registrationRequired = false;
    private final Client client = Client.getInstance();
    private final Updater updater = Updater.getInstance();
    private final String HOSTNAME = "localhost";
    private final int SERVER_PORT = 1488;
    private final int UPDATER_PORT = 1489;

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static void setBundle(ResourceBundle newBundle) {
        bundle = newBundle;
    }

    @FXML
    public void initialize() {
        client.setHostname(HOSTNAME);
        client.setPort(SERVER_PORT);

        if (currentLocale == null) currentLocale = new Locale("ru", "RU");
        bundle = ResourceBundle.getBundle("locale", currentLocale);

        languageSelector.getItems().addAll(
                new Locale("ru", "RU"),
                new Locale("ro", "RO"),
                new Locale("hr", "HR"),
                new Locale("es", "CO")
        );

        languageSelector.setCellFactory(param -> new LocaleListCell());
        languageSelector.setButtonCell(new LocaleListCell());
        languageSelector.getSelectionModel().select(currentLocale);

        languageSelector.setOnAction(event -> {
            currentLocale = languageSelector.getValue();
            bundle = ResourceBundle.getBundle("locale", currentLocale);
            updateTexts();
        });

        root.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!event.getTarget().equals(usernameField) &&
                    !event.getTarget().equals(passwordField) &&
                    !event.getTarget().equals(switchPageButton) &&
                    !event.getTarget().equals(actionButton) &&
                    !event.getTarget().equals(languageSelector)) {
                root.requestFocus();
            }
        });

        switchPageButton.setOnAction(event -> {
            registrationRequired = !registrationRequired;
            updateTexts();
        });

        actionButton.setOnAction(event -> handleAuthAction());

        updateTexts();
    }

    private void handleAuthAction() {
        if (!validateUsername(usernameField.getText()) || !validatePassword(passwordField.getText())) {
            errorLabel.setText(bundle.getString("error.fill.all.fields"));
            return;
        }

        if (!tryToConnect()) {
            errorLabel.setText(bundle.getString("error.server.unavailable"));
            return;
        }

        client.setUser(new User(usernameField.getText(), passwordField.getText()));

        Response response;
        response = client.handleRequest(null, new UserDto(client.getUser(), registrationRequired));

        if (response == null) {
            logger.error("RESPONSE IS NULL");
            errorLabel.setText(bundle.getString("error.server.unavailable"));
            return;
        }

        if (response.getStatus() == ValidationStatus.SUCCESS) openNewWindow(response.getCollection());
        else if (response.getStatus() == ValidationStatus.USER_ALREADY_EXISTS)
            errorLabel.setText(bundle.getString("error.user.already.exists"));
        else if (response.getStatus() == ValidationStatus.INVALID_USER_DATA) {
            if (response.getStatusDescription() != null)
                errorLabel.setText(bundle.getString("error.no.account.with.such.id"));
            else
                errorLabel.setText(bundle.getString("error.invalid.user.data.provided"));
        }
    }

    private boolean validateUsername(String username) {
        return username != null && !username.trim().isBlank();
    }

    private boolean validatePassword(String password) {
        return password != null && !password.trim().isBlank();
    }

    private boolean tryToConnect() {
        try {
            client.connect();
            logger.info("Connected to the server successfully");

            return true;
        } catch (IOException exception) {
            client.closeConnection();
            logger.error("Couldn't connect to the server");
            return false;
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void updateTexts() {
        errorLabel.setText("");
        if (registrationRequired) {
            titleLabel.setText(bundle.getString("register.title"));
            switchPageButton.setText(bundle.getString("switch.to.login"));
            actionButton.setText(bundle.getString("register"));
        } else {
            titleLabel.setText(bundle.getString("auth.title"));
            switchPageButton.setText(bundle.getString("switch.to.register"));
            actionButton.setText(bundle.getString("login"));
        }
        usernameField.setPromptText(bundle.getString("username.prompt"));
        passwordField.setPromptText(bundle.getString("password.prompt"));
        if (stage != null) {
            stage.setTitle(registrationRequired ? bundle.getString("auth.title") : bundle.getString("register.title"));
        }
    }

    private void openNewWindow(LinkedList<StudyGroup> collection) {
        try {
            CollectionService.getInstance().setCollection(collection);
            logger.trace("CS collection size from AC: " + collection.size());

            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("main.fxml"));
            mainLoader.load();

            updater.setHostname(HOSTNAME);
            updater.setPort(UPDATER_PORT);
            updater.setMainController(mainLoader.getController());

            logger.error("PROBLEM WITH UPDATER");
            Thread updaterThread = new Thread(updater, "UPDATER");
            try {
                updater.connect();
            } catch (IOException exception) {
                logger.error("Error occurred while trying to connect updater: " + exception.getMessage());
            }
            updaterThread.start();

            Parent newRoot = mainLoader.getRoot();
            Stage newStage = new Stage();

            newStage.setScene(new Scene(newRoot));
            newStage.setTitle(bundle.getString("table.window.title"));
            newStage.setResizable(false);
            newStage.show();
            stage.close();
        } catch (IOException exception) {
            logger.error("Error occurred while trying to open main windows: " + exception.getMessage());
        }
    }

}
