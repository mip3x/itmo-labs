package ui;

import collection.data.StudyGroup;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Date;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class MainController {
    @FXML private TableView<StudyGroup> tableView;
    @FXML private TableColumn<StudyGroup, Integer> idColumn;
    @FXML private TableColumn<StudyGroup, String> nameColumn;
    @FXML private TableColumn<StudyGroup, Long> xColumn;
    @FXML private TableColumn<StudyGroup, Double> yColumn;
    @FXML private TableColumn<StudyGroup, Date> creationDateColumn;
    @FXML private TableColumn<StudyGroup, Long> studentsCountColumn;
    @FXML private TableColumn<StudyGroup, Long> shouldBeExpelledColumn;
    @FXML private TableColumn<StudyGroup, String> formOfEducationColumn;
    @FXML private TableColumn<StudyGroup, String> semesterColumn;
    @FXML private TableColumn<StudyGroup, String> groupAdminNameColumn;
    @FXML private TableColumn<StudyGroup, Long> groupAdminWeightColumn;
    @FXML private TableColumn<StudyGroup, String> groupAdminPassportIdColumn;
    @FXML private TableColumn<StudyGroup, String> groupAdminEyeColorColumn;
    @FXML private TableColumn<StudyGroup, Double> groupAdminXColumn;
    @FXML private TableColumn<StudyGroup, Double> groupAdminYColumn;
    @FXML private TableColumn<StudyGroup, Integer> groupAdminZColumn;
    private LinkedList<StudyGroup> studyGroups;
    private ObservableList<StudyGroup> collection;
    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        collection = loadStudyGroups();
        bundle = AuthController.getBundle();
//        tableView.getItems().addAll(loadStudyGroups());
        initializeTable(collection);
    }

    private void initializeTable(ObservableList<StudyGroup> collection) {
        idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getName()));
        xColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCoordinates().getX()));
        yColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCoordinates().getY()));
        creationDateColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCreationDate()));
        studentsCountColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getStudentsCount()));
        shouldBeExpelledColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getShouldBeExpelled()));
        formOfEducationColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getFormOfEducation().toString()));
        semesterColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getSemester().toString()));
        groupAdminNameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getGroupAdmin().getName()));
        groupAdminWeightColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getGroupAdmin().getWeight()));
        groupAdminPassportIdColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getGroupAdmin().getPassportID()));
        groupAdminEyeColorColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getGroupAdmin().getEyeColor().toString()));
        groupAdminXColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getGroupAdmin().getLocation().getX()));
        groupAdminYColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getGroupAdmin().getLocation().getY()));
        groupAdminZColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getGroupAdmin().getLocation().getZ()));

        tableView.setItems(collection);
    }

    @FXML
    private void showTablePage() {
        // Логика переключения на страницу с таблицей
    }

    @FXML
    private void showUtilsPage() {
        // Логика переключения на страницу с утилитами
    }

    @FXML
    private void showAnimationsPage() {
        // Логика переключения на страницу с анимациями
    }

    private ObservableList<StudyGroup> loadStudyGroups() {
        return FXCollections.observableList(studyGroups);
    }

    public void setCollection(LinkedList<StudyGroup> studyGroups) {
        this.studyGroups = studyGroups;
    }
}
