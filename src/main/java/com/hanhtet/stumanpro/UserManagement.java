package com.hanhtet.stumanpro;

import java.net.URL;
import java.util.ResourceBundle;

import com.hanhtet.stumanpro.alert.CustomAlertBox;
import com.hanhtet.stumanpro.entity.User;
import com.hanhtet.stumanpro.utils.DATA;
import com.hanhtet.stumanpro.utils.Functions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserManagement {
    private final Functions functions = new Functions();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField first_name_input;

    @FXML
    private TextField last_name_input;

    @FXML
    private TextField email_input;

    @FXML
    private TextField phno_input;

    @FXML
    private ComboBox<String> user_type_input;

    @FXML
    private TextArea address_input;

    @FXML
    void ADDUser(ActionEvent event) {
        System.out.println("Adding the User");
        String firstName = first_name_input.getText();
        String lastName = last_name_input.getText().toString();
        String email = email_input.getText().toString();
        String passString = "P@ssw0rd2006";
        String phString = phno_input.getText().toString();
        String address = (String) address_input.getText().toString();
        String user_type = user_type_input.getValue().toString().toLowerCase();
        User user = new User(firstName, lastName, email, passString, phString,"null",address,user_type);
        boolean result = functions.RegisterUser(user);
        if (result){
            URL soundUrl = getClass().getResource(DATA.SUCCESS_SOUND);
            functions.playAudio(soundUrl);
            first_name_input.clear();
            last_name_input.clear();
            email_input.clear();
            phno_input.clear();
            address_input.clear();
            user_type_input.getSelectionModel().clearSelection();
        }else{
            Node source = (Node) event.getSource();
            Stage primaryStage = (Stage) source.getScene().getWindow();
            CustomAlertBox.showAlert(Alert.AlertType.ERROR, primaryStage, "User ADD Error", "User can't register!", "User don't have permission to add the user or User might be already added!");
        }
    }

    @FXML
    void initialize() {
        assert first_name_input != null : "fx:id=\"first_name_input\" was not injected: check your FXML file 'user_add.fxml'.";
        assert last_name_input != null : "fx:id=\"last_name_input\" was not injected: check your FXML file 'user_add.fxml'.";
        assert email_input != null : "fx:id=\"email_input\" was not injected: check your FXML file 'user_add.fxml'.";
        assert phno_input != null : "fx:id=\"phno_input\" was not injected: check your FXML file 'user_add.fxml'.";
        assert user_type_input != null : "fx:id=\"user_type_input\" was not injected: check your FXML file 'user_add.fxml'.";
        assert address_input != null : "fx:id=\"address_input\" was not injected: check your FXML file 'user_add.fxml'.";
        
        ObservableList<String> userTypes = FXCollections.observableArrayList(
                "Admin",
                "Teacher",
                "Student"
        );
        user_type_input.setItems(userTypes);
    }
}
