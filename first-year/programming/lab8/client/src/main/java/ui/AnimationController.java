package ui;

import collection.CollectionService;
import collection.data.StudyGroup;
import command.list.RemoveById;
import dto.CommandDto;
import dto.UserDto;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.Client;
import transfer.Response;
import validation.ValidationStatus;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AnimationController {
    @FXML
    private Button deleteAnyByFOE;
    @FXML
    private Button deleteAllLowerButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;
    @FXML
    private Button addButton;
    @FXML
    private Canvas canvas;

    private final LinkedList<StudyGroup> collection = new LinkedList<>();
    private final Map<String, Color> userColors = new HashMap<>();
    private final Logger logger = LogManager.getLogger();
    private ResourceBundle bundle;
    private StudyGroup selectedGroup;
    private boolean initialize;
    private boolean clickedOnObject;
    private final Client client = Client.getInstance();

    @FXML
    public void initialize() {
        initialize = true;
        bundle = AuthController.getBundle();
        collection.addAll(CollectionService.getInstance().getCollection());

        addButton.setOnAction(event -> handleAdd());
        editButton.setOnAction(event -> handleEdit());
        deleteButton.setOnAction(event -> handleDelete());
        deleteAllLowerButton.setOnAction(event -> handleDeleteAllLower());
        deleteAnyByFOE.setOnAction(event -> handleDeleteAnyByFOE());

        setUserColors();
        drawObjects();
        initialize = false;
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleCanvasClick);
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, this::handleCanvasHover);

        updateTexts();
    }

    public void refreshCollectionLater() {
        Platform.runLater(this::refreshCollection);
    }

    private void refreshCollection() {
        collection.clear();
        collection.addAll(CollectionService.getInstance().getCollection());
        initialize = true;
        drawObjects();
        initialize = false;
    }

    private void handleDeleteAnyByFOE() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("delete_any_by_foe.fxml"), bundle);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 250, 150));
            stage.setTitle(bundle.getString("delete_any_by_foe.title"));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(deleteAllLowerButton.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteAllLower() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("delete_all_lower.fxml"), bundle);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 250, 150));
            stage.setTitle(bundle.getString("delete_all_lower.title"));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(deleteAllLowerButton.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete() {
        if (selectedGroup != null) {
            if (showDeleteConfirmation()) {
                List<String> selectedGroupId = List.of(selectedGroup.getId().toString());
                CommandDto commandDto = new CommandDto(new RemoveById(), selectedGroupId);

                Response response = client.handleRequest(commandDto, new UserDto(client.getUser(), false));
                if (response == null) {
                    logger.error("RESPONSE IS NULL");
                    return;
                }
                if (response.getStatus() == ValidationStatus.EXECUTION_ERROR) showErrorMessage(bundle.getString("error.deletion.permission_insufficient"));
                else showSuccessMessage(bundle.getString("success.collection_deletion"));
            }
        }
    }

    private void handleEdit() {
        if (selectedGroup != null) {
            openEditForm(selectedGroup);
        }
    }

    private void handleAdd() {
        openEditForm(null);
    }

    private void openEditForm(StudyGroup studyGroup) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("edit_form.fxml"), bundle);
            VBox root = loader.load();

            EditFormController controller = loader.getController();
            controller.setStudyGroup(studyGroup);

            Stage stage = new Stage();
            stage.setTitle(studyGroup == null ? bundle.getString("add_study_group")
                    : bundle.getString("edit_study_group"));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception exception) {
            logger.error("Error opening edit form " + exception.getMessage());
        }
    }

    private boolean showDeleteConfirmation() {
        ButtonType approve = new ButtonType(bundle.getString("button.approve"), ButtonType.OK.getButtonData());
        ButtonType disapprove = new ButtonType(bundle.getString("button.disapprove"), ButtonType.CANCEL.getButtonData());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                bundle.getString("delete.confirmation_message"),
                approve,
                disapprove);
        alert.setTitle(bundle.getString("delete.title"));
        alert.setHeaderText(null);

        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(disapprove) == approve;
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

    private double scaleX(double x, double minX, double maxX) {
        return ((x - minX) / (maxX - minX)) * canvas.getWidth();
    }

    private double scaleY(double y, double minY, double maxY) {
        return ((y - minY) / (maxY - minY)) * canvas.getHeight();
    }

    private void drawObjects() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double minX = collection.stream().mapToDouble(g -> g.getCoordinates().getX()).min().orElse(0);
        double minY = collection.stream().mapToDouble(g -> g.getCoordinates().getY()).min().orElse(0);
        double maxX = collection.stream().mapToDouble(g -> g.getCoordinates().getX()).max().orElse(1);
        double maxY = collection.stream().mapToDouble(g -> g.getCoordinates().getY()).max().orElse(1);

        for (StudyGroup studyGroup : collection) {
            Color color = userColors.getOrDefault(studyGroup.getCreator(), Color.BROWN);

            double scaledX = scaleX(studyGroup.getCoordinates().getX(), minX, maxX);
            double scaledY = scaleY(studyGroup.getCoordinates().getY(), minY, maxY);
            double size = Math.max(10, Math.min(50, studyGroup.getStudentsCount() != null ? studyGroup.getStudentsCount() : 20));

            if (initialize) animateObject(scaledX, scaledY, size, color);
            else {
                gc.setFill(color);
                gc.fillOval(scaledX - size / 2, scaledY - size / 2, size, size);
            }
        }
    }

    private void animateObject(double x, double y, double size, Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        DoubleProperty alpha = new javafx.beans.property.SimpleDoubleProperty(0);
        alpha.addListener((obs, oldVal, newVal) -> {
            gc.setFill(new Color(color.getRed(), color.getGreen(), color.getBlue(), newVal.doubleValue()));
            gc.fillOval(x - size / 2, y - size / 2, size, size);
        });

        double delay = ThreadLocalRandom.current().nextDouble(0, 0.5);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(delay), new KeyValue(alpha, 0)),
                new KeyFrame(Duration.seconds(delay + 1), new KeyValue(alpha, 1))
        );

        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);
        timeline.play();
    }

    private void showAlertWithGroupInfo(StudyGroup group) {
        String info = MessageFormat.format(bundle.getString("name"), group.getName()) + "\n" +
                MessageFormat.format(bundle.getString("coordinates"), group.getCoordinates().getX(), group.getCoordinates().getY()) + "\n" +
                MessageFormat.format(bundle.getString("students_count"), group.getStudentsCount()) + "\n" +
                MessageFormat.format(bundle.getString("should_be_expelled"), group.getShouldBeExpelled()) + "\n" +
                MessageFormat.format(bundle.getString("form_of_education"), bundle.getString(group.getFormOfEducation().toString().toLowerCase())) + "\n" +
                MessageFormat.format(bundle.getString("semester"), bundle.getString(group.getSemester().toString().toLowerCase())) + "\n" +
                MessageFormat.format(bundle.getString("group_admin_name"), group.getGroupAdmin().getName()) + "\n" +
                MessageFormat.format(bundle.getString("group_admin_weight"), group.getGroupAdmin().getWeight()) + "\n" +
                MessageFormat.format(bundle.getString("group_admin_passport_id"), group.getGroupAdmin().getPassportID());

        if (group.getGroupAdmin().getEyeColor() != null) info += "\n" + MessageFormat.format(bundle.getString("group_admin_eye_color"), bundle.getString(group.getGroupAdmin().getEyeColor().toString().toLowerCase()));

        if (group.getGroupAdmin().getLocation() != null) {
            info += "\n" + MessageFormat.format(bundle.getString("location_name"), group.getGroupAdmin().getLocation().getName()) + "\n" +
                    MessageFormat.format(bundle.getString("location_coordinates"), group.getGroupAdmin().getLocation().getX(), group.getGroupAdmin().getLocation().getY(), group.getGroupAdmin().getLocation().getZ());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString("study_group_info"));
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.showAndWait();
    }

    private void handleCanvasClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        double minX = collection.stream().mapToDouble(g -> g.getCoordinates().getX()).min().orElse(0);
        double minY = collection.stream().mapToDouble(g -> g.getCoordinates().getY()).min().orElse(0);
        double maxX = collection.stream().mapToDouble(g -> g.getCoordinates().getX()).max().orElse(1);
        double maxY = collection.stream().mapToDouble(g -> g.getCoordinates().getY()).max().orElse(1);

        for (StudyGroup studyGroup : collection) {
            double scaledX = scaleX(studyGroup.getCoordinates().getX(), minX, maxX);
            double scaledY = scaleY(studyGroup.getCoordinates().getY(), minY, maxY);
            double size = Math.max(10, Math.min(50, studyGroup.getStudentsCount() != null ? studyGroup.getStudentsCount() : 20));

            if (Math.abs(x - scaledX) < size / 2 && Math.abs(y - scaledY) < size / 2) {
                selectedGroup = studyGroup;
                if (event.getClickCount() == 2) {
                    showAlertWithGroupInfo(studyGroup);
                } else {
                    drawObjects();
                    highlightObject(scaledX, scaledY, size);
                }
                clickedOnObject = true;
                break;
            }
        }

        if (!clickedOnObject) selectedGroup = null;
    }

    private void handleCanvasHover(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        double minX = collection.stream().mapToDouble(g -> g.getCoordinates().getX()).min().orElse(0);
        double minY = collection.stream().mapToDouble(g -> g.getCoordinates().getY()).min().orElse(0);
        double maxX = collection.stream().mapToDouble(g -> g.getCoordinates().getX()).max().orElse(1);
        double maxY = collection.stream().mapToDouble(g -> g.getCoordinates().getY()).max().orElse(1);

        boolean hovering = false;
        for (StudyGroup studyGroup : collection) {
            double scaledX = scaleX(studyGroup.getCoordinates().getX(), minX, maxX);
            double scaledY = scaleY(studyGroup.getCoordinates().getY(), minY, maxY);
            double size = Math.max(10, Math.min(50, studyGroup.getStudentsCount() != null ? studyGroup.getStudentsCount() : 20));

            if (Math.abs(x - scaledX) < size / 2 && Math.abs(y - scaledY) < size / 2) {
                hovering = true;
                break;
            }
        }

        if (hovering) {
            canvas.setCursor(Cursor.HAND);
        } else {
            canvas.setCursor(Cursor.DEFAULT);
        }
    }

    private void highlightObject(double x, double y, double size) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);
        gc.strokeOval(x - size / 2, y - size / 2, size, size);
    }

    public void setUserColors() {
        Random random = new Random();

        for (StudyGroup studyGroup : collection) {
            float hue = random.nextFloat();
            float saturation = random.nextFloat(0.0f, 1.0f); // насыщенность - можно выбрать любое значение между 0.0 и 1.0
            float brightness = random.nextFloat(0.0f, 1.0f); // яркость - можно выбрать любое значение между 0.0 и 1.0
            Color randomColor = Color.hsb(hue, saturation, brightness);
            userColors.put(studyGroup.getCreator(), randomColor);
        }
    }

    private void updateTexts() {
        addButton.setText(bundle.getString("button.add"));
        editButton.setText(bundle.getString("button.edit"));
        deleteButton.setText(bundle.getString("button.delete"));
        deleteAllLowerButton.setText(bundle.getString("button.delete_all_lower"));
        deleteAnyByFOE.setText(bundle.getString("button.delete_any_by_foe"));
    }
}
