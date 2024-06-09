package ui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.ifmo.lab8.configuration.RuntimeConfiguration;
import se.ifmo.lab8.database.model.*;
import se.ifmo.lab8.database.service.*;
import se.ifmo.lab8.gui.stage.StageHandler;
import se.ifmo.lab8.localization.Localization;

import java.net.URL;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WorkspaceController {
    final OrganizationService organizationService;
    final CoordinatesService coordinatesService;
    final Localization localization = Localization.getInstance();
    final StageHandler stageHandler;
    final AddressService addressService;

    @FXML Label status;
    @FXML TableView<Organization> mainTable;
    @FXML Canvas canvas;

    @FXML Button add, info, logout, remove, removeAll;
    @FXML ChoiceBox<String> language;
    @FXML ToggleButton show_onlyredactable;
    @FXML Button removeGreater;
    @FXML Button removeLower;
    @FXML Button printUniqueAnnualTurnover;
    @FXML Button filterGreaterThanType;
    @FXML Button minByName;

    final Set<Long> alreadyDrawn = new HashSet<>();

    final ObservableList<Organization> organizations = FXCollections.observableArrayList();

    @SneakyThrows
    public void initialize() {
        language.setItems(FXCollections.observableArrayList(
                Arrays.stream(Localization.Type.values()).map(Localization.Type::getName).toList()));
        language.setValue(Localization.Type.DEFAULT.getName());

        language.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RuntimeConfiguration.setLocalizationType(Localization.Type.fromString(newValue));
                refreshUI();
            }
        });

        mainTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !mainTable.getSelectionModel().isEmpty() && isEditable(mainTable.getSelectionModel().getSelectedItem())) {
                showPopup(true);
            }
        });

        mainTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Organization item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty && WorkspaceController.isEditable(item)) {
                    setStyle("-fx-font-weight: bold; -fx-text-inner-color: red;");
                }
            }
        });

        organizations.addListener((ListChangeListener<Organization>) change -> {
            drawBands();
        });

        updateTable();
        refreshUI();
        drawBands();
    }

    private static boolean isEditable(Organization organization) {
        return organization.getUser().getId().equals(RuntimeConfiguration.getUser().map(User::getId).orElse(-1L));
    }

    private void refreshUI() {
        mainTable.getItems().clear();
        mainTable.getColumns().clear();
        initializeTableColumns();
        updateTable();

        add.setText(localization.get("button.add"));
        info.setText(localization.get("button.info"));
        logout.setText(localization.get("button.logout"));
        remove.setText(localization.get("button.remove"));
        removeAll.setText(localization.get("button.removeAll"));
        show_onlyredactable.setText(localization.get("toggle.showOnlyEditable"));

        removeGreater.setText(localization.get("button.removeGreater"));
        removeLower.setText(localization.get("button.removeLower"));

        printUniqueAnnualTurnover.setText(localization.get("button.printUniqueAnnualTurnover"));
        filterGreaterThanType.setText(localization.get("button.filterGreaterThanType"));
        minByName.setText(localization.get("button.minByName"));
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    private void updateTableLater() {
        if (RuntimeConfiguration.getUser().isPresent())
            Platform.runLater(this::updateTable);
    }

    private void updateTable() {
        status.setText(RuntimeConfiguration.getUsername());

        TableColumn<Organization, ?> sortColumn = mainTable.getSortOrder().isEmpty() ? null : mainTable.getSortOrder().get(0);
        TableColumn.SortType sortType = sortColumn == null ? null : sortColumn.getSortType();

        List<Organization> newMusicBands = organizationService.findAll();

        boolean hasNewEntries = false;
        if (newMusicBands.size() != mainTable.getItems().size()) {
            hasNewEntries = true;
        } else {
            for (Organization newBand : newMusicBands) {
                if (!mainTable.getItems().contains(newBand)) {
                    hasNewEntries = true;
                    break;
                }
            }
        }

        if (!hasNewEntries) return;

        organizations.clear();
        organizations.addAll(newMusicBands);

        mainTable.getColumns().clear();
        initializeTableColumns();

        if (show_onlyredactable.isSelected()) organizations.removeIf(musicBand -> !isEditable(musicBand));
        mainTable.setItems(organizations);

        if (sortColumn != null) {
            mainTable.getSortOrder().add(sortColumn);
            sortColumn.setSortType(sortType);
            sortColumn.setSortable(true);
        }

        mainTable.refresh();
        drawBands();
    }

    private void initializeTableColumns() {
        TableColumn<Organization, String> nameCol = new TableColumn<>(localization.get("col.name"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Organization, String> coordinatesCol = new TableColumn<>(localization.get("col.cords"));
        coordinatesCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                localization.getFormattedNumber(cellData.getValue().getCoordinates().getX()) + ", " +
                        localization.getFormattedNumber(cellData.getValue().getCoordinates().getY())
        ));

        TableColumn<Organization, String> creationDateCol = new TableColumn<>(localization.get("col.created"));
        creationDateCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                localization.getFormattedDate(Date.from(cellData.getValue().getCreationDate().toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()))
        ));

        TableColumn<Organization, String> annualTurnoverCol = new TableColumn<>(localization.get("col.annualTurnover"));
        annualTurnoverCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                localization.getFormattedNumber(cellData.getValue().getAnnualTurnover())
        ));

        TableColumn<Organization, String> organizationStringTableColumn = new TableColumn<>(localization.get("col.organizationType"));
        organizationStringTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                localization.get(cellData.getValue().getOrganizationType().toString())
        ));

        TableColumn<Organization, String> officialAddress = new TableColumn<>(localization.get("col.officialAddress"));
        officialAddress.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getOfficialAddress().getZipCode()
        ));

        mainTable.getColumns().addAll(nameCol, annualTurnoverCol, coordinatesCol, creationDateCol, organizationStringTableColumn, officialAddress);
    }

    private void drawBands() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Organization organization : organizations) {
            drawBand(gc, organization);
        }
    }

    private void drawBand(GraphicsContext gc, Organization band) {
        double size = band.getAnnualTurnover();
        double x = Math.max(0, Math.min(band.getCoordinates().getX(), canvas.getHeight() - size));
        double y = Math.max(0, Math.min(band.getCoordinates().getY(), canvas.getWidth() - size));
        Color color = getColorForUser(isEditable(band));

        gc.setFill(color);
        gc.fillOval(x, y, size, size);

        // Добавляем анимацию появления
        if (!alreadyDrawn.contains(band.getId())) {
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), canvas);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.play();
        }

        alreadyDrawn.add(band.getId());
    }

    private Color getColorForUser(boolean isEditable) {
        return isEditable ? Color.GREEN : Color.BLUE;
    }

    @FXML
    private void canvasClicked(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        for (Organization band : organizations) {
            double bandX = band.getCoordinates().getX();
            double bandY = band.getCoordinates().getY();
            double size = band.getAnnualTurnover();

            if (x >= bandX && x <= (bandX + size) && y >= bandY && y <= (bandY + size)) {
                if (isEditable(band)) {
                    mainTable.getSelectionModel().select(band);
                    showPopup(true);
                } else {
                    showInfoPopup(band);
                }
                break;
            }
        }
    }

    @FXML
    void add(ActionEvent event) {
        showPopup(false);
    }

    private TextField getField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        return textField;
    }

    private TextField getField(String prompt, String value) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setText(value);
        return textField;
    }

    private void showPopup(boolean isEdit) {
        Organization selected = isEdit ? mainTable.getSelectionModel().getSelectedItem() : null;

        Stage popup = new Stage();

        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(isEdit ? localization.get("title.editElement") : localization.get("title.addElement"));

        HBox infoBox = new HBox(5);
        TextField nameField = isEdit ? getField(localization.get("prompt.name"), selected.getName()) : getField(localization.get("prompt.name"));
        infoBox.getChildren().addAll(nameField);

        TextField annualTurnover = isEdit ? getField(localization.get("prompt.annualTurnover"), String.valueOf(selected.getAnnualTurnover())) : getField(localization.get("prompt.annualTurnover"));
        infoBox.getChildren().add(annualTurnover);

        HBox coordinatesBox = new HBox(5);
        TextField coordinatesX = isEdit ? getField(localization.get("prompt.cord.x"), String.valueOf(selected.getCoordinates().getX())) : getField(localization.get("prompt.cord.x"));
        TextField coordinatesY = isEdit ? getField(localization.get("prompt.cord.y"), String.valueOf(selected.getCoordinates().getY())) : getField(localization.get("prompt.cord.y"));
        coordinatesBox.getChildren().addAll(coordinatesX, coordinatesY);

        HBox addressBox = new HBox(5);
        TextField zipCode = isEdit ? getField(localization.get("prompt.zipCode"), String.valueOf(Optional.ofNullable(selected.getOfficialAddress()).orElse(new Address(0L, "")).getZipCode())) : getField(localization.get("prompt.zipCode"));
        addressBox.getChildren().add(zipCode);

        VBox selectors = new VBox(5);
        HBox musicGenreSelector = new HBox(5);
        Label label = new Label(localization.get("prompt.organizationType"));

        ComboBox<OrganizationType> organizationTypeComboBox = new ComboBox<>();
        organizationTypeComboBox.setItems(FXCollections.observableArrayList(Arrays.asList(OrganizationType.values())));
        if (isEdit) organizationTypeComboBox.setValue(selected.getOrganizationType());

        musicGenreSelector.getChildren().addAll(label, organizationTypeComboBox);
        selectors.getChildren().addAll(musicGenreSelector);

        Button saveButton = new Button(isEdit ? localization.get("button.edit") : localization.get("button.save"));

        VBox layout = new VBox(5, infoBox, coordinatesBox, selectors, addressBox, saveButton);

        TextField amount = getField(localization.get("label.amount"), "1");

        Label cordsRangeLabel = new Label(localization.get("label.cordsRand"));
        cordsRangeLabel.setStyle("-fx-font-size: 15");
        CheckBox cordsRangeCheckbox = new CheckBox();

        saveButton.setOnAction(e -> {
            try {
                ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                Validator validator = factory.getValidator();

                for (int counter = 0; counter < Integer.parseInt(amount.getText()); counter++) {
                    Organization organization = new Organization();
                    if (isEdit) organization.setId(selected.getId());
                    organization.setUser(RuntimeConfiguration.getUser().orElseThrow());

                    organization.setName(nameField.getText());
                    organization.setAnnualTurnover(Float.valueOf(annualTurnover.getText()));
                    organization.setOrganizationType(organizationTypeComboBox.getValue());

                    // Создание нового уникального адреса и валидация
                    Address address = Address.builder()
                            .zipCode(zipCode.getText() + "-" + counter) // Добавление счетчика для уникальности
                            .build();

                    Set<ConstraintViolation<Address>> violations = validator.validate(address);
                    if (!violations.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (ConstraintViolation<Address> violation : violations) {
                            sb.append(violation.getMessage()).append("\n");
                        }
                        showAlert(Alert.AlertType.ERROR, localization.get("error.invalidInput") + ": " + sb.toString());
                        return;
                    }

                    address = addressService.save(address);
                    organization.setOfficialAddress(address);

                    // Создание новых уникальных координат
                    Coordinates coordinates = new Coordinates();
                    coordinates.setX(coordinatesX.getText().equals("R") ? (long) (Math.random() * 300 + counter) : Long.valueOf(coordinatesX.getText()));
                    coordinates.setY(coordinatesY.getText().equals("R") ? (int) (float) (Math.random() * 300 + counter) : Integer.parseInt(coordinatesY.getText()));

                    coordinates = coordinatesService.save(coordinates);
                    organization.setCoordinates(coordinates);

                    // Сохранение организации
                    organizationService.save(organization);
                }

                popup.close();
                updateTable();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, localization.get("error.invalidInput") + ": " + ex.getMessage());
            }
        });

        if (!isEdit) {
            HBox cordsRangeBox = new HBox(5);

            cordsRangeCheckbox.setOnMouseClicked((ignored) -> {
                if (cordsRangeCheckbox.isSelected()) {
                    coordinatesY.setDisable(true);
                    coordinatesX.setDisable(true);
                    coordinatesX.setText("R");
                    coordinatesY.setText("R");
                } else {
                    coordinatesY.setDisable(false);
                    coordinatesX.setDisable(false);
                    coordinatesX.setText("");
                    coordinatesY.setText("");
                }
            });

            cordsRangeBox.getChildren().addAll(cordsRangeLabel, cordsRangeCheckbox);
            layout.getChildren().addAll(amount, cordsRangeBox);
        }

        layout.setPadding(new Insets(10, 10, 10, 10));
        popup.setScene(new Scene(layout, 500, 400));
        popup.showAndWait();
    }

    private void handleValidationException(se.ifmo.lab8.database.model.ValidationException ex) {
        StringBuilder errorMsg = new StringBuilder(localization.get("error.validation.failed") + ":\n");
        ex.getConstraintViolations().forEach(violation -> {
            errorMsg.append(localization.get(violation.getMessageTemplate())).append("\n");
        });
        showAlert(Alert.AlertType.ERROR, errorMsg.toString());
    }

    private void showInfoPopup(Organization organization) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(localization.get("title.info"));

        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(10));
        Label nameLabel = new Label(localization.get("prompt.name") + organization.getName());
        Label participantsLabel = new Label(localization.get("prompt.coordinates") + organization.getCoordinates().toString());
        Label coordinatesLabel = new Label(localization.get("prompt.creationDate") + organization.getCoordinates().getX() + ", " + organization.getCoordinates().getY());
        Label singlesLabel = new Label(localization.get("prompt.annualTurnover") + organization.getAnnualTurnover());
        Label genreLabel = new Label(localization.get("prompt.organizationType") + organization.getOrganizationType());
        Label bestAlbumLabel = new Label(localization.get("prompt.address") + organization.getOfficialAddress().getZipCode());

        vBox.getChildren().addAll(nameLabel, participantsLabel, coordinatesLabel, singlesLabel, genreLabel, bestAlbumLabel);

        Button okButton = new Button("OK");
        okButton.setOnAction(e -> popup.close());

        vBox.getChildren().add(okButton);

        Scene scene = new Scene(vBox);
        popup.setScene(scene);
        popup.show();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }

    @SneakyThrows
    @FXML
    void logout(ActionEvent event) {
        RuntimeConfiguration.setUser(Optional.empty());
        stageHandler.showLoginScene();
    }

    @FXML
    void removeAll(ActionEvent event) {
        organizationService.removeAllByUser(RuntimeConfiguration.getUser().orElseThrow());
        updateTable();
    }

    @FXML
    void removeSelected(ActionEvent event) {
        Organization selected = mainTable.getSelectionModel().getSelectedItem();
        organizationService.removeByIdAndUser(selected.getId(), RuntimeConfiguration.getUser().orElseThrow());
        updateTable();
    }

    @FXML
    void setViewOnlyRedactable(ActionEvent event) {
        if (show_onlyredactable.isSelected()) {
            organizations.clear();
            organizations.addAll(organizationService.findAll());
            organizations.removeIf(flat -> !isEditable(flat));
        } else {
            updateTable();
        }
    }

    @FXML
    void showInfo(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, localization.get("information.content"));
    }

    @FXML
    void removeGreater(ActionEvent event) {
        try {
            organizationService.removeByIdAndUser(organizationService.findAllByUser(RuntimeConfiguration.getUser().orElseThrow()).stream()
                    .max(Comparator.comparing(Organization::getAnnualTurnover)).map(Organization::getId).orElse(null), RuntimeConfiguration.getUser().orElseThrow());
            updateTable();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }

    @FXML
    void removeLower(ActionEvent event) {
        try {
            organizationService.removeByIdAndUser(organizationService.findAllByUser(RuntimeConfiguration.getUser().orElseThrow()).stream()
                    .min(Comparator.comparing(Organization::getAnnualTurnover)).map(Organization::getId).orElse(null), RuntimeConfiguration.getUser().orElseThrow());
            updateTable();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }

    public void printUniqueAnnualTurnoverAction(ActionEvent actionEvent) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);

        TextArea textField = new TextArea();
        textField.setText(organizationService.findAll().stream().map(Organization::getAnnualTurnover).distinct().map(String::valueOf).collect(Collectors.joining("\n")));

        VBox vBox = new VBox(10);
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> popup.close());

        vBox.getChildren().add(textField);
        vBox.getChildren().add(okButton);

        Scene scene = new Scene(vBox);
        popup.setScene(scene);
        popup.show();

        popup.show();
    }

    public void filterGreaterThanTypeAction(ActionEvent actionEvent) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);

        ComboBox<OrganizationType> organizationTypeComboBox = new ComboBox<>();
        organizationTypeComboBox.setItems(FXCollections.observableArrayList(Arrays.asList(OrganizationType.values())));

        TextArea filtered = new TextArea();
        filtered.setText("select type!");

        VBox vBox = new VBox(10);

        Button filterButton = new Button(localization.get("button.filter"));
        filterButton.setOnAction(e -> {
            filtered.setText(organizationService.findAll().stream().filter(organization -> organization.getOrganizationType().equals(organizationTypeComboBox.getValue())).map(Organization::toString).collect(Collectors.joining("\n")));
        });

        Button exitButton = new Button(localization.get("button.exit"));
        exitButton.setOnAction(e -> popup.close());

        vBox.getChildren().add(organizationTypeComboBox);
        vBox.getChildren().add(filterButton);
        vBox.getChildren().add(filtered);
        vBox.getChildren().add(exitButton);

        Scene scene = new Scene(vBox);
        popup.setScene(scene);
        popup.show();

        popup.show();
    }

    public void minByNameAction(ActionEvent actionEvent) {

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);

        TextArea min = new TextArea(organizationService.findAll().stream().min(Comparator.comparing(Organization::getName)).map(Organization::toString).orElse("-"));

        Button exitButton = new Button(localization.get("button.exit"));
        exitButton.setOnAction(e -> popup.close());

        VBox vBox = new VBox(10);

        vBox.getChildren().add(min);
        vBox.getChildren().add(exitButton);

        Scene scene = new Scene(vBox);
        popup.setScene(scene);
        popup.show();

        popup.show();
    }
}