package ui;

import collection.CollectionService;
import collection.data.StudyGroup;
import command.list.Info;
import command.list.RemoveById;
import dto.CommandDto;
import dto.UserDto;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.Client;
import tcp.Updater;
import transfer.Response;
import validation.ValidationStatus;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController {
    @FXML
    private ComboBox<Locale> languageSelector;
    @FXML
    private TableView<StudyGroup> tableView;
    @FXML
    private TableColumn<StudyGroup, Integer> idColumn;
    @FXML
    private TableColumn<StudyGroup, String> nameColumn;
    @FXML
    private TableColumn<StudyGroup, String> xColumn;
    @FXML
    private TableColumn<StudyGroup, String> yColumn;
    @FXML
    private TableColumn<StudyGroup, String> creationDateColumn;
    @FXML
    private TableColumn<StudyGroup, Long> studentsCountColumn;
    @FXML
    private TableColumn<StudyGroup, Long> shouldBeExpelledColumn;
    @FXML
    private TableColumn<StudyGroup, String> formOfEducationColumn;
    @FXML
    private TableColumn<StudyGroup, String> semesterColumn;
    @FXML
    private TableColumn<StudyGroup, String> groupAdminNameColumn;
    @FXML
    private TableColumn<StudyGroup, Long> groupAdminWeightColumn;
    @FXML
    private TableColumn<StudyGroup, String> groupAdminPassportIdColumn;
    @FXML
    private TableColumn<StudyGroup, String> groupAdminEyeColorColumn;
    @FXML
    private TableColumn<StudyGroup, String> locationNameColumn;
    @FXML
    private TableColumn<StudyGroup, String> locationXColumn;
    @FXML
    private TableColumn<StudyGroup, String> locationYColumn;
    @FXML
    private TableColumn<StudyGroup, Integer> locationZColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button deleteAllLowerButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button animationsButton;
    @FXML
    private Button infoButton;
    @FXML
    private Button tableButton;
    @FXML
    private Button deleteAnyByFOE;
    private MenuItem editItem;
    private MenuItem deleteItem;
    private final Logger logger = LogManager.getLogger();
    private final LinkedList<StudyGroup> collection = new LinkedList<>();
    private ResourceBundle bundle;
    private Locale currentLocale;
    private DateFormat dateFormat;
    private NumberFormat numberFormat;
    private final Client client = Client.getInstance();

    @FXML
    public void initialize() {
        collection.addAll(CollectionService.getInstance().getCollection());

        bundle = AuthController.getBundle();
        addButton.setOnAction(event -> handleAdd());
        editButton.setOnAction(event -> handleEdit());
        deleteButton.setOnAction(event -> handleDelete());
        deleteAllLowerButton.setOnAction(event -> handleDeleteAllLower());
        exitButton.setOnAction(event -> handleExit());
        infoButton.setOnAction(event -> handleInfo());
        deleteAnyByFOE.setOnAction(event -> handleDeleteAnyByFOE());
        animationsButton.setOnAction(event -> showAnimationsPage());

        currentLocale = bundle.getLocale();
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
            AuthController.setBundle(bundle);
            updateTexts();
        });

        initializeTable();
        initializeContextMenu();

        addButton.setTooltip(new Tooltip(bundle.getString("tooltip.add_button")));
        editButton.setTooltip(new Tooltip(bundle.getString("tooltip.edit_button")));
        deleteButton.setTooltip(new Tooltip(bundle.getString("tooltip.delete_button")));
        deleteAllLowerButton.setTooltip(new Tooltip(bundle.getString("tooltip.delete_all_lower_button")));
        tableButton.setTooltip(new Tooltip(bundle.getString("tooltip.table_button")));
        animationsButton.setTooltip(new Tooltip(bundle.getString("tooltip.animation_button")));
        infoButton.setTooltip(new Tooltip(bundle.getString("tooltip.info_button")));
        exitButton.setTooltip(new Tooltip(bundle.getString("tooltip.exit_button")));
        deleteAnyByFOE.setTooltip(new Tooltip(bundle.getString("tooltip.delete_any_by_foe_button")));

        updateTexts();
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getName()));
        xColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(numberFormat.format(cellData.getValue().getCoordinates().getX())));
        yColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(numberFormat.format(cellData.getValue().getCoordinates().getY())));
        creationDateColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(dateFormat.format(cellData.getValue().getCreationDate())));
        studentsCountColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getStudentsCount()));
        shouldBeExpelledColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getShouldBeExpelled()));

        formOfEducationColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(bundle.getString(cellData.getValue().getFormOfEducation().toString().toLowerCase())));
        semesterColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(bundle.getString(cellData.getValue().getSemester().toString().toLowerCase())));

        groupAdminNameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getGroupAdmin().getName()));
        groupAdminWeightColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getGroupAdmin().getWeight()));
        groupAdminPassportIdColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getGroupAdmin().getPassportID()));

        groupAdminEyeColorColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                cellData.getValue().getGroupAdmin().getEyeColor() != null ? bundle.getString(cellData.getValue().getGroupAdmin().getEyeColor().toString().toLowerCase()) : null));

        locationNameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                cellData.getValue().getGroupAdmin().getLocation() != null && cellData.getValue().getGroupAdmin().getLocation().getName() != null ?
                        cellData.getValue().getGroupAdmin().getLocation().getName() : null));
        locationXColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                cellData.getValue().getGroupAdmin().getLocation() != null && cellData.getValue().getGroupAdmin().getLocation().getX() != null ? numberFormat.format(cellData.getValue().getGroupAdmin().getLocation().getX()) : null));
        locationYColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                cellData.getValue().getGroupAdmin().getLocation() != null && cellData.getValue().getGroupAdmin().getLocation().getY() != null ? numberFormat.format(cellData.getValue().getGroupAdmin().getLocation().getY()) : null));
        locationZColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                cellData.getValue().getGroupAdmin().getLocation() != null && cellData.getValue().getGroupAdmin().getLocation().getZ() != null ? cellData.getValue().getGroupAdmin().getLocation().getZ() : null));

        tableView.setItems(FXCollections.observableList(collection));

        tableView.setRowFactory(tv -> {
            TableRow<StudyGroup> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    handleRowClick(row.getItem());
                }
            });
            return row;
        });
    }

    private void initializeContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        editItem = new MenuItem(bundle.getString("button.edit"));
        deleteItem = new MenuItem(bundle.getString("button.delete"));

        editItem.setOnAction(event -> handleEdit());
        deleteItem.setOnAction(event -> handleDelete());

        contextMenu.getItems().addAll(editItem, deleteItem);
        tableView.setContextMenu(contextMenu);
    }


    @FXML
    private void handleInfo() {
        CommandDto commandDto = new CommandDto(new Info());

        Response response = client.handleRequest(commandDto, new UserDto(client.getUser(), false));
        if (response == null) {
            logger.error("RESPONSE IS NULL");
            return;
        }

        StringBuilder result = new StringBuilder();

        Pattern pattern = Pattern.compile("^(.*?): (.*)$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(response.getMessage());

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);

            switch (key) {
                case "Collection type" -> result.append(bundle.getString("collection.type")).append(": ").append(value).append("\n");
                case "Collection initialization time" -> {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                    try {
                        String date = dateFormat.format(simpleDateFormat.parse(value));
                        result.append(bundle.getString("collection.time")).append(": ").append(date).append("\n");
                    } catch (Exception exception) {
                        logger.error("Error parsing date" + exception.getMessage());
                    }
                }
                case "Size of collection" -> result.append(bundle.getString("collection.size")).append(": ").append(value);
            }
        }

        showSuccessMessage(result.toString());
    }

    @FXML
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("exit.confirmation"));
        alert.setHeaderText(null);
        alert.setContentText(bundle.getString("exit.confirmation.message"));

        if (alert.showAndWait().get() == ButtonType.OK) {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));

            FXMLLoader authLoader = new FXMLLoader(getClass().getResource("auth.fxml"), bundle);
            VBox root;
            try {
                root = authLoader.load();
                client.setUser(null);
            } catch (IOException exception) {
                showErrorMessage(bundle.getString("error.auth_window_loading"));
                logger.error("Error loading authorization window: " + exception.getMessage());
                return;
            }


            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 600, 400));
            newStage.setTitle(bundle.getString("auth.title"));
            newStage.setResizable(false);
            newStage.show();

            AuthController authController = authLoader.getController();
            authController.setStage(newStage);
            stage.close();
        }
    }

    @FXML
    private void handleTableClick(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            StudyGroup selectedGroup = tableView.getSelectionModel().getSelectedItem();
            if (selectedGroup != null) {
                handleRowClick(selectedGroup);
            }
        }
    }

    private void handleRowClick(StudyGroup selectedGroup) {
        refreshCollection();
        logger.trace("Clicked on " + selectedGroup);
    }

    private void handleEdit() {
        StudyGroup selectedGroup = tableView.getSelectionModel().getSelectedItem();
        if (selectedGroup != null) {
            openEditForm(selectedGroup);
        }
    }

    private void handleAdd() {
        openEditForm(null);
    }

    private void handleDelete() {
        StudyGroup selectedGroup = tableView.getSelectionModel().getSelectedItem();
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
    @FXML
    private void changeLanguage() {
        logger.trace("CHANGE LANGUAGE");
    }

    @FXML
    private void showTablePage() {
    }

    @FXML
    private void showUtilsPage() {
    }

    @FXML
    private void showAnimationsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("animation_view.fxml"), bundle);
            BorderPane root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle(bundle.getString("animation.window.title"));
            Updater.getInstance().setAnimationController(loader.getController());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void refreshCollectionLater() {
        Platform.runLater(this::refreshCollection);
    }

    private void refreshCollection() {
        collection.clear();
        collection.addAll(CollectionService.getInstance().getCollection());
        tableView.setItems(FXCollections.observableList(collection));
        tableView.refresh();
    }

    private void updateTexts() {
        dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, bundle.getLocale());
        numberFormat = NumberFormat.getNumberInstance(bundle.getLocale());

        addButton.setText(bundle.getString("button.add"));
        editButton.setText(bundle.getString("button.edit"));
        deleteButton.setText(bundle.getString("button.delete"));
        deleteAllLowerButton.setText(bundle.getString("button.delete_all_lower"));
        exitButton.setText(bundle.getString("button.exit"));
        tableButton.setText(bundle.getString("button.table"));
        animationsButton.setText(bundle.getString("button.animations"));
        infoButton.setText(bundle.getString("button.info"));
        deleteAnyByFOE.setText(bundle.getString("button.delete_any_by_foe"));

        idColumn.setText(bundle.getString("column.id"));
        nameColumn.setText(bundle.getString("column.name"));
        xColumn.setText(bundle.getString("column.coordinates_X"));
        yColumn.setText(bundle.getString("column.coordinates_Y"));
        creationDateColumn.setText(bundle.getString("column.creationDate"));
        studentsCountColumn.setText(bundle.getString("column.studentsCount"));
        shouldBeExpelledColumn.setText(bundle.getString("column.shouldBeExpelled"));
        formOfEducationColumn.setText(bundle.getString("column.formOfEducation"));
        semesterColumn.setText(bundle.getString("column.semester"));
        groupAdminNameColumn.setText(bundle.getString("column.groupAdmin_name"));
        groupAdminWeightColumn.setText(bundle.getString("column.groupAdmin_weight"));
        groupAdminPassportIdColumn.setText(bundle.getString("column.groupAdmin_passportID"));
        groupAdminEyeColorColumn.setText(bundle.getString("column.groupAdmin_eyeColor"));
        locationNameColumn.setText(bundle.getString("column.groupAdmin_location_Name"));
        locationXColumn.setText(bundle.getString("column.groupAdmin_location_X"));
        locationYColumn.setText(bundle.getString("column.groupAdmin_location_Y"));
        locationZColumn.setText(bundle.getString("column.groupAdmin_location_Z"));

        editItem.setText(bundle.getString("button.edit"));
        deleteItem.setText(bundle.getString("button.delete"));
    }
}
