package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class AuthApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Locale.setDefault(new Locale("ru", "RU"));
            ResourceBundle bundle = ResourceBundle.getBundle("locale", Locale.getDefault());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("auth.fxml"), bundle);
            VBox root = loader.load();

            AuthController authController = loader.getController();
            authController.setStage(primaryStage);

            Scene scene = new Scene(root, 600, 400);
            primaryStage.setScene(scene);
            primaryStage.setTitle(bundle.getString("auth.title"));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
