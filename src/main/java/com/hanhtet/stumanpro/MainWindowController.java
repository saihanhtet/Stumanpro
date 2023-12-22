package com.hanhtet.stumanpro;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.hanhtet.stumanpro.alert.CustomAlertBox;
import com.hanhtet.stumanpro.utils.DATA;
import com.hanhtet.stumanpro.utils.Functions;
import com.hanhtet.stumanpro.utils.SyncManagerCustom;
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

public class MainWindowController {
    private final Functions functions = new Functions();
    @FXML
    private Label applicationLabel;

    @FXML
    private ResourceBundle resources;

    @FXML
    private Label studentCount;

    @FXML
    private URL location;

    @FXML
    private VBox sidebar;

    @FXML
    private Label route;
    @FXML
    private Label welcomeName;

    @FXML
    private GridPane homePage;

    @FXML
    private TableView<?> frontTableView;

    @FXML
    private GridPane managementPage;

    @FXML
    private GridPane studentManagePage;

    @FXML
    private void analyticPageFunction(ActionEvent event) {
        // need to add analytic function
    }

    @FXML
    private void budgetFunction(ActionEvent event) {
        // need to add budgetFunction
    }

    @FXML
    private void examPageFunction(ActionEvent event) {
        // need to add exam function
    }
    
    @FXML
    private void homePageFunction(ActionEvent event) {
        autoFetch();
        homePage.toFront();
        route.setText("Dashboard/Home");
    }

    @FXML
    private void homeworkPageFunction(ActionEvent event) {
        // add home work page function
    }

    @FXML
    private void logoutPageFunction(ActionEvent event) {
        functions.LogoutUser();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(DATA.LOGIN_FXML));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(DATA.APPLICATION_NAME);
            stage.show();
            ((Node) event.getSource()).getScene().getWindow().hide();
        } catch (Exception e) {
            Node source = (Node) event.getSource();
            Stage primaryStage = (Stage) source.getScene().getWindow();
            CustomAlertBox.showAlert(Alert.AlertType.ERROR, primaryStage, "User can't log out!!", "User can't log out!", "User don't have permission to logout as user wish!");
        }
    }

    @FXML
    private void sidebarToggle(ActionEvent event) {
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
    private void studentManagementPageFunction(ActionEvent event) {
        studentManagePage.toFront();
        route.setText("Dashboard/Student Management & Monitoring");
    }

    @FXML
    private void managementPageFunction(ActionEvent event) {
        managementPage.toFront();
        route.setText("Dashboard/Management");
    }

    @FXML
    private void toggleTheme(ActionEvent event) {
      // toggle theme if possible
    }

    @FXML
    private void addCourse(ActionEvent event) {
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
    private void userRegistration(ActionEvent event) {
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
    private void viewCourses(ActionEvent event){
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

    @FXML
    private void syncFunction(ActionEvent event){
        boolean result = SyncManagerCustom.startAutoSync();
        if (!result){
            Node source = (Node) event.getSource();
            Stage primaryStage = (Stage) source.getScene().getWindow();
            CustomAlertBox.showAlert(Alert.AlertType.ERROR, primaryStage, "Error at syncing!!", "Error occurred at Uploading the Local data to Google Sheet!", "Check your internet connection and try again. If not contact the Developer!");
        } else{
            System.out.println("Successfully synced!");
        }
    }

    private void autoFetch(){
        Task<Integer> countStudentsTask = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return functions.count_user("student");
            }
        };
        countStudentsTask.setOnSucceeded(e -> {
            studentCount.setText(countStudentsTask.getValue().toString());
        });

        new Thread(countStudentsTask).start();

        Task<Boolean> syncTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return SyncManagerCustom.startAutoSync();
            }
        };
        
        new Thread(syncTask).start();
    }



    @FXML
    private void initialize() {
        
        assert sidebar != null : "fx:id=\"sidebar\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert welcomeName != null : "fx:id=\"welcomeName\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert route != null : "fx:id=\"route\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert homePage != null : "fx:id=\"homePage\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert frontTableView != null : "fx:id=\"frontTableView\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert managementPage != null : "fx:id=\"managementPage\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert studentManagePage != null : "fx:id=\"studentManagePage\" was not injected: check your FXML file 'mainwindow.fxml'.";

        applicationLabel.setText(DATA.APPLICATION_NAME);
        UserSession userSession = UserSession.getInstance();
        String name = userSession.getName();
        welcomeName.setText("Welcome back "+name+"!");
        autoFetch();
    }
}
