package com.hanhtet.stumanpro;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.hanhtet.stumanpro.alert.CustomAlertBox;
import com.hanhtet.stumanpro.utils.Functions;
import com.hanhtet.stumanpro.utils.UserSession;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainWindow {
    private final Functions functions = new Functions();

    @FXML
    private ResourceBundle resources;
    @FXML
    private Label student_count;

    @FXML
    private URL location;

    @FXML
    private VBox sidebar;

    @FXML
    private Label route;
    @FXML
    private Label welcome_name;

    @FXML
    private GridPane home_page;

    @FXML
    private TableView<?> FrontTableView;

    @FXML
    private GridPane student_management_page;

    @FXML
    private GridPane inquire_student_page;

    @FXML
    void AnalyticPage(ActionEvent event) {

    }

    @FXML
    void Budget(ActionEvent event) {

    }

    @FXML
    void ExamPage(ActionEvent event) {

    }
    
    @FXML
    void HomePage(ActionEvent event) {
        autoFetch();
        home_page.toFront();
        route.setText("Dashboard/Home");
    }

    @FXML
    void HomeworkPage(ActionEvent event) {

    }

    @FXML
    void LogoutPage(ActionEvent event) {
        functions.LogoutUser();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Light English Class For All");
            stage.show();
            ((Node) event.getSource()).getScene().getWindow().hide();
        } catch (Exception e) {
            Node source = (Node) event.getSource();
            Stage primaryStage = (Stage) source.getScene().getWindow();
            CustomAlertBox.showAlert(Alert.AlertType.ERROR, primaryStage, "User can't log out!!", "User can't log out!", "User don't have permission to logout as user wish!");
        }
    }

    @FXML
    void SidebarToggle(ActionEvent event) {
        double newWidth = 80.0;
        double newBtnWidth=40.0;
        double oldWidth = 240.0;
        double oldBtnWidth = 220;

        ObservableList<Node> vBoxChildren = sidebar.getChildren();
        // change the width of sidebar pane
        if (sidebar.getPrefWidth() == newWidth) {
            sidebar.setPrefWidth(oldWidth);
        } else {
            sidebar.setPrefWidth(newWidth);
        }
        // change size of all children from sidebar
        for (Node node : vBoxChildren) {
            if (node instanceof Button) {
                Button button = (Button) node;
                if (sidebar.getPrefWidth() == newWidth) {
                    button.setPrefWidth(newBtnWidth);
                } else {
                    button.setPrefWidth(oldBtnWidth);
                }
            }
        }
    }

    @FXML
    void StudentInquire(ActionEvent event) {
        inquire_student_page.toFront();
        route.setText("Dashboard/Inquire Students");
    }

    @FXML
    void StudentManagement(ActionEvent event) {
        student_management_page.toFront();
        route.setText("Dashboard/Student Management");
    }

    @FXML
    void toggleTheme(ActionEvent event) {

    }

    @FXML
    void addCourse(ActionEvent event) {
        Stage courseStage = new Stage();
        courseStage.setTitle("Add Course");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("course_add.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            courseStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        courseStage.show();
    }

     @FXML
    void UserRegistration(ActionEvent event) {
        Stage userStage = new Stage();
        userStage.setTitle("Add User");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user_add.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            userStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        userStage.show();
    }

    @FXML
    void viewCourses(ActionEvent event){
        Stage courseStage = new Stage();
        courseStage.setTitle("View Course");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("course_view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            courseStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        courseStage.show();
    }

    private void autoFetch(){
        Task<Integer> countStudentsTask = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return functions.count_user("student");
            }
        };
        countStudentsTask.setOnSucceeded(e -> {
            student_count.setText(countStudentsTask.getValue().toString());
        });
        new Thread(countStudentsTask).start();
    }



    @FXML
    void initialize() {
        assert sidebar != null : "fx:id=\"sidebar\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert welcome_name != null : "fx:id=\"welcome_name\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert route != null : "fx:id=\"route\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert home_page != null : "fx:id=\"home_page\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert FrontTableView != null : "fx:id=\"FrontTableView\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert student_management_page != null : "fx:id=\"student_management_page\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert inquire_student_page != null : "fx:id=\"inquire_student_page\" was not injected: check your FXML file 'mainwindow.fxml'.";

        UserSession userSession = UserSession.getInstance();
        String name = userSession.getName();
        welcome_name.setText("Welcome back "+name+"!");
        autoFetch();

        functions.InitializeProject();
    }
}
