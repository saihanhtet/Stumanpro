package com.hanhtet.stumanpro;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.hanhtet.stumanpro.alert.CustomAlertBox;
import com.hanhtet.stumanpro.entity.Login;
import com.hanhtet.stumanpro.utils.DATA;
import com.hanhtet.stumanpro.utils.Functions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private Label ApplicationLabel;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField Email_input;

    @FXML
    private PasswordField Password_input;


    private Functions functions;

    @FXML
    private void LoginFunction(ActionEvent event) {
        Login user = new Login(Email_input.getText(), Password_input.getText());
        //Login user = new Login("hanhtetsan13@gmail.com", "P@ssw0rd2006");
        boolean result = functions.LoginUser(user);
        if (result){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("mainwindow.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle(DATA.APPLICATION_NAME);
                stage.show();
                ((Node) event.getSource()).getScene().getWindow().hide();
            } catch (IOException e) {
                Node source = (Node) event.getSource();
                Stage primaryStage = (Stage) source.getScene().getWindow();
                CustomAlertBox.showAlert(Alert.AlertType.INFORMATION, primaryStage, "File Not existed", "The MainWindow Not Existed!", "MainWindow does not existed maybe it lost in compiling?");
            }
        } else {
            Node source = (Node) event.getSource();
            Stage primaryStage = (Stage) source.getScene().getWindow();
            CustomAlertBox.showAlert(Alert.AlertType.WARNING, primaryStage, "User not exist", "User does not exist or wrong password!", "Please check your password or email!");
        }

    }

    @FXML
    private void dont_have_account(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage primaryStage = (Stage) source.getScene().getWindow();
        CustomAlertBox.showAlert(Alert.AlertType.INFORMATION, primaryStage, "New User", "User Registration!", "Please contact us lightecfa@gmail.com");
    }

    @FXML
    private void forgot_password(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage primaryStage = (Stage) source.getScene().getWindow();
        CustomAlertBox.showAlert(Alert.AlertType.INFORMATION, primaryStage, "Forgot Password?", "Forgot Password?", "Please contact us lightecfa@gmail.com");
    }

    @FXML
    private void initialize() {
        ApplicationLabel.setText(DATA.APPLICATION_NAME);
        assert Email_input != null : "fx:id=\"Email_input\" was not injected: check your FXML file 'Login.fxml'.";
        assert Password_input != null : "fx:id=\"Password_input\" was not injected: check your FXML file 'Login.fxml'.";
        functions = new Functions();
        functions.InitializeProject();
    }
}
