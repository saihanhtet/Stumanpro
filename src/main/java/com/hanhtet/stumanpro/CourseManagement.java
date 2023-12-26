package com.hanhtet.stumanpro;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.hanhtet.stumanpro.alert.CustomAlertBox;
import com.hanhtet.stumanpro.entity.Course;
import com.hanhtet.stumanpro.utils.DATA;
import com.hanhtet.stumanpro.utils.Functions;
import com.hanhtet.stumanpro.utils.SheetUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
    private void addCourse(ActionEvent event) {
        String courseInputName = courseInput.getText();
        String price = coursePrice.getText();
        Course course = new Course(null,courseInputName,price);
        boolean result = functions.addCourse(course);
        if (result){
            courseInput.clear();
            coursePrice.clear();
        }else{
            Node source = (Node) event.getSource();
            Stage primaryStage = (Stage) source.getScene().getWindow();
            CustomAlertBox.showAlert(Alert.AlertType.ERROR, primaryStage, "Course ADD Error", "Course can't add!", "User don't have permission to add the course or Course might be already added!");
        }
    }

    private void addDataTable(){
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

    private void edit(Course course){
        List<Object> newData = new ArrayList<>();
        newData.add(Integer.parseInt(course.getId()));
        newData.add(course.getName());
        newData.add(Integer.parseInt(course.getPrice()));
        String updatedId = course.getId();
        try {
            SheetUtils.editDataInLocalFile(updatedId, newData, DATA.DOWNLOAD_XLXS_FOLDER_PATH +"\\"+"lcfa_courses.xlsx");
            //SheetUtils.updateDataInSheet(updatedId, newData, DATA.courseTable_ID, DATA.courseTable_RANGE);
        } catch (Exception e) {
            System.err.println("Something went wrong with editing the course!"+e);
        }
    }

    @FXML
    private void initialize() {
        assert courseInput != null : "fx:id=\"courseInput\" was not injected: check your FXML file 'course_add.fxml'.";
        try {
            courseName.setCellValueFactory(new PropertyValueFactory<>("name"));
            courseID.setCellValueFactory(new PropertyValueFactory<>("id"));
            coursePerPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
            addDataTable();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
