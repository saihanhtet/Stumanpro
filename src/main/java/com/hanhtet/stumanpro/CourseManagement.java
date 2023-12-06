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
    private TableColumn<Course, String> course_name;
    @FXML
    private TableColumn<Course, String> course_per_price; 
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    @FXML
    private TableColumn<Course, String> course_id;

    @FXML
    private TextField course_input;
    @FXML
    private TextField course_price;

    @FXML
    private TableView<Course> course_table;


    @FXML
    void ADDCourse(ActionEvent event) {
        String courseName = course_input.getText();
        String price = course_price.getText().toString();
        Course course = new Course(null,courseName,price);
        boolean result = functions.addCourse(course);
        if (result){
            URL soundUrl = getClass().getResource(DATA.SUCCESS_SOUND);
            functions.playAudio(soundUrl);
            course_input.clear();
            course_price.clear();
        }else{
            Node source = (Node) event.getSource();
            Stage primaryStage = (Stage) source.getScene().getWindow();
            CustomAlertBox.showAlert(Alert.AlertType.ERROR, primaryStage, "Course ADD Error", "Course can't add!", "User don't have permission to add the course or Course might be already added!");
        }
    }

    public void addDataTable(){
        List<Course> coursesFromSheet = functions.getCoursesFromSheet();
        if (coursesFromSheet != null && !coursesFromSheet.isEmpty()) {
            course_table.getItems().clear();
            course_table.getItems().addAll(coursesFromSheet);
            setCellFactoryForColumns();
        }
    }
    private void setCellFactoryForColumns() {
        course_name.setCellFactory(TextFieldTableCell.forTableColumn());
        course_per_price.setCellFactory(TextFieldTableCell.forTableColumn());

        course_name.setOnEditCommit(event -> {
            Course course = event.getRowValue();
            course.setName(event.getNewValue());
            course_table.getItems().set(event.getTablePosition().getRow(), course);
            edit(course);
        });
        course_per_price.setOnEditCommit(event -> {
            Course course = event.getRowValue();
            course.setPrice(event.getNewValue());
            course_table.getItems().set(event.getTablePosition().getRow(), course);
            edit(course);
        });

        course_name.setEditable(true);
        course_per_price.setEditable(true);
    }

    private void edit(Course course){
        List<Object> newData = new ArrayList<>();
        newData.add(Integer.parseInt(course.getId()));
        newData.add(course.getName());
        newData.add(Integer.parseInt(course.getPrice()));
        String updatedId = course.getId().toString();
        try {
            SheetUtils.editDataInLocalFile(updatedId, newData, DATA.DOWNLOAD_XLXS_FOLDER_PATH +"\\"+"lcfa_courses.xlsx");
            //SheetUtils.updateDataInSheet(updatedId, newData, DATA.COURSE_TABLE_ID, DATA.COURSE_TABLE_RANGE);
        } catch (Exception e) {
            System.err.println("Something went wrong with editing the course!"+e);
        }
    }

    @FXML
    void initialize() {
        assert course_input != null : "fx:id=\"course_input\" was not injected: check your FXML file 'course_add.fxml'.";
        try {
            course_name.setCellValueFactory(new PropertyValueFactory<>("name"));
            course_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            course_per_price.setCellValueFactory(new PropertyValueFactory<>("price"));
            addDataTable();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
