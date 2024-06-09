package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.Client;
import transfer.Response;
import validation.ValidationStatus;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class AuthController {
    private Logger logger = LogManager.getLogger();
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
    private ResourceBundle bundle;
    private Locale currentLocale;
    private Stage stage;
    private boolean isLogin = true;
    private final Client client = new Client();

    @FXML
    public void initialize() {
        currentLocale = new Locale("ru", "RU");
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
            isLogin = !isLogin;
            updateTexts();
        });

        actionButton.setOnAction(event -> {
            if (!validateUsername(usernameField.getText()) || !validatePassword(passwordField.getText())) {
                errorLabel.setText(bundle.getString("error.fill.all.fields"));
                return;
            }

            if (!tryToConnect()) {
                errorLabel.setText(bundle.getString("error.server.unavailable"));
                return;
            }

            Response response;
            if (isLogin)
                response = client.handleUser(usernameField.getText(), passwordField.getText(), false);
            else response = client.handleUser(usernameField.getText(), passwordField.getText(), true);

            if (response == null) {
                errorLabel.setText(bundle.getString("error.server.unavailable"));
                return;
            }

            if (response.getStatus() == ValidationStatus.SUCCESS) openNewWindow();
            else if (response.getStatus() == ValidationStatus.USER_ALREADY_EXISTS)
                errorLabel.setText(bundle.getString("error.user.already.exists"));
            else if (response.getStatus() == ValidationStatus.INVALID_USER_DATA) {
                if (response.getStatusDescription() != null)
                    errorLabel.setText(bundle.getString("error.no.account.with.such.id"));
                else
                    errorLabel.setText(bundle.getString("error.invalid.user.data.provided"));
            }
        });

        updateTexts();
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
        errorLabel.setText(""); // Сбросить текст ошибки при обновлении
        if (isLogin) {
            titleLabel.setText(bundle.getString("auth.title"));
            switchPageButton.setText(bundle.getString("switch.to.register"));
            actionButton.setText(bundle.getString("login"));
        } else {
            titleLabel.setText(bundle.getString("register.title"));
            switchPageButton.setText(bundle.getString("switch.to.login"));
            actionButton.setText(bundle.getString("register"));
        }
        usernameField.setPromptText(bundle.getString("username.prompt"));
        passwordField.setPromptText(bundle.getString("password.prompt"));
        if (stage != null) {
            stage.setTitle(isLogin ? bundle.getString("auth.title") : bundle.getString("register.title"));
        }
    }

    private void openNewWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/your/package/new_window.fxml"));
            VBox newRoot = fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(newRoot));
            newStage.setTitle(bundle.getString("new.window.title"));
            newStage.show();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class LocaleListCell extends javafx.scene.control.ListCell<Locale> {
        private final ImageView imageView = new ImageView();

        @Override
        protected void updateItem(Locale item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setGraphic(null);
                setText(null);
            } else {
                try {
                    String iconPath = "/icons/" + item.getLanguage() + ".jpg";
                    Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
                    imageView.setImage(icon);
                    setText(item.getDisplayLanguage(item));
                    setGraphic(imageView);
                } catch (Exception e) {
                    setText(item.getDisplayLanguage(item) + " (icon not found)");
                    setGraphic(null);
                }
            }
        }
    }
}
