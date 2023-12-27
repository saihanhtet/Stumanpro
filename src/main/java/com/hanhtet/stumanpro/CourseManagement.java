package com.hanhtet.stumanpro;

import com.hanhtet.stumanpro.alert.CustomAlertBox;
import com.hanhtet.stumanpro.entity.Course;
import com.hanhtet.stumanpro.utils.CRUD;
import com.hanhtet.stumanpro.utils.DATA;
import com.hanhtet.stumanpro.utils.Functions;
import com.hanhtet.stumanpro.utils.LOG;
import com.hanhtet.stumanpro.utils.OffSheetWriter;
import com.hanhtet.stumanpro.utils.UserSuggestionHandler;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CourseManagement {

  private final Functions functions = new Functions();

  @FXML
  private TableColumn<Course, String> courseName;

  @FXML
  private TableColumn<Course, String> coursePerPrice;

  @FXML
  private ResourceBundle resources;

  @FXML
  private URL location;

  @FXML
  private TableColumn<Course, String> courseID;

  @FXML
  private TextField courseInput;

  @FXML
  private TextField coursePrice;

  @FXML
  private TableView<Course> courseTable;

  @FXML
  private VBox suggestionContainer;

  @FXML
  private TextField searchField;

  @FXML
  private ScrollPane scrollPane;

  @FXML
  private Label courseNameLabel;

  private Course currentSelectedCourse;

  private UserSuggestionHandler<Course> suggestionHandler = new UserSuggestionHandler<>(
    suggestionContainer,
    scrollPane
  );

  private CRUD<Course> crud = new CRUD<>();

  @FXML
  private void deleteCourse(ActionEvent event) {
    try {
      if (crud.delete(currentSelectedCourse)) {
        LOG.logInfo("Successfully deleted the course!");
        currentSelectedCourse = null;
        suggestionHandler.clearSearchSuggestions();
        searchField.clear();
        perfromSearchAction(true);
      } else {
        LOG.logWarn("Can't delete the course.");
      }
    } catch (IOException e) {
      LOG.logMe(Level.WARNING, "Error occurred during at deleting course!", e);
    }
  }

  private ObservableList<Course> performSearch(String searchText) {
    List<Course> allCourses = functions.getCoursesFromSheet();
    ObservableList<Course> filteredCourses = FXCollections.observableArrayList();

    for (Course course : allCourses) {
      // Customize this condition based on your search criteria
      if (
        course.getName().toLowerCase().contains(searchText.toLowerCase()) ||
        course.getId().toLowerCase().contains(searchText.toLowerCase())
      ) {
        filteredCourses.add(course);
      }
    }
    return filteredCourses;
  }

  private void handleSuggestionClick(Course selectedCourse) {
    courseNameLabel.setText(
      "You have selected the course: " + selectedCourse.getName()
    );
    currentSelectedCourse = selectedCourse;
    suggestionHandler.clearSearchSuggestions();

    Label selectedLabel = new Label(selectedCourse.getName());
    selectedLabel.getStyleClass().add("label-suggestion");
    selectedLabel.setMaxWidth(Double.MAX_VALUE);
    selectedLabel.getStyleClass().add("selected-course-label");
    suggestionContainer.getChildren().add(selectedLabel);
    searchField.setText(selectedCourse.getName().trim());
  }

  private void porpulateSuggestions(
    ObservableList<Course> filteredCourses,
    boolean empty
  ) {
    if (empty) {
      suggestionHandler.populateSearchSuggestions(
        performSearch(""),
        this::handleSuggestionClick
      );
    } else {
      suggestionHandler.populateSearchSuggestions(
        filteredCourses,
        this::handleSuggestionClick
      );
    }
  }

  private void perfromSearchAction(boolean deleteMethod) {
    try {
      if (deleteMethod) {
        courseNameLabel.setText("You haven't selected the course yet.");
      }
      searchField
        .textProperty()
        .addListener((observable, oldValue, newValue) -> {
          if (!newValue.isEmpty()) {
            ObservableList<Course> filteredCourses = performSearch(newValue);
            if (deleteMethod) {
              porpulateSuggestions(filteredCourses, false);
            } else {
              courseTable.getItems().clear();
              courseTable.getItems().addAll(filteredCourses);
            }
          } else {
            if (deleteMethod) {
              suggestionHandler.clearSearchSuggestions();
              porpulateSuggestions(null, true);
            } else {
              reClearTable();
            }
          }
        });
    } catch (Exception e) {
      LOG.logWarn("Course delete can't be loaded.");
    }
  }

  private void reClearTable() {
    courseTable.getItems().clear();
    addDataTable();
  }

  @FXML
  private void clearTable(ActionEvent event) {
    reClearTable();
  }

  @FXML
  private void addCourse(ActionEvent event) {
    String courseInputName = courseInput.getText();
    String price = coursePrice.getText();
    Course course = new Course(null, courseInputName, price);
    boolean result = functions.addCourse(course);
    if (result) {
      courseInput.clear();
      coursePrice.clear();
    } else {
      Node source = (Node) event.getSource();
      Stage primaryStage = (Stage) source.getScene().getWindow();
      CustomAlertBox.showAlert(
        Alert.AlertType.ERROR,
        primaryStage,
        "Course ADD Error",
        "Course can't add!",
        "Course don't have permission to add the course or Course might be already added!"
      );
    }
  }

  private void addDataTable() {
    List<Course> coursesFromSheet = functions.getCoursesFromSheet();
    if (coursesFromSheet != null && !coursesFromSheet.isEmpty()) {
      courseTable.getItems().clear();
      courseTable.getItems().addAll(coursesFromSheet);
      setCellFactoryForColumns();
    }
  }

  private void setCellFactoryForColumns() {
    courseName.setCellFactory(TextFieldTableCell.forTableColumn());
    coursePerPrice.setCellFactory(TextFieldTableCell.forTableColumn());
    // add function when edit
    courseName.setOnEditCommit(event -> {
      Course course = event.getRowValue();
      course.setName(event.getNewValue());
      courseTable.getItems().set(event.getTablePosition().getRow(), course);
      edit(course);
    });
    coursePerPrice.setOnEditCommit(event -> {
      Course course = event.getRowValue();
      course.setPrice(event.getNewValue());
      courseTable.getItems().set(event.getTablePosition().getRow(), course);
      edit(course);
    });
    // set the column to editable
    courseName.setEditable(true);
    coursePerPrice.setEditable(true);
  }

  private void edit(Course course) {
    List<Object> newData = new ArrayList<>();
    newData.add(Integer.parseInt(course.getId()));
    newData.add(course.getName());
    newData.add(Integer.parseInt(course.getPrice()));
    String updatedId = course.getId();
    try {
      OffSheetWriter.editDataById(
        updatedId,
        newData,
        DATA.DOWNLOAD_XLXS_FOLDER_PATH + "\\" + "lcfa_courses.xlsx"
      );
    } catch (Exception e) {
      LOG.logMe(
        Level.WARNING,
        "Something went wrong with the editing the course!",
        e
      );
    }
  }

  @FXML
  private void initialize() {
    assert courseInput !=
    null : "fx:id=\"courseInput\" was not injected: check your FXML file 'course_add.fxml'.";
    try {
      courseName.setCellValueFactory(new PropertyValueFactory<>("name"));
      courseID.setCellValueFactory(new PropertyValueFactory<>("id"));
      coursePerPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
      addDataTable();
      perfromSearchAction(false);
    } catch (Exception e) {
      LOG.logWarn("course table can't be loaded.");
    }
    try {
      perfromSearchAction(true);
    } catch (Exception e) {
      LOG.logWarn("Course delete can't be loaded.");
    }
  }
}
