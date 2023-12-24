package com.hanhtet.stumanpro;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.hanhtet.stumanpro.alert.CustomAlertBox;
import com.hanhtet.stumanpro.entity.Course;
import com.hanhtet.stumanpro.entity.User;
import com.hanhtet.stumanpro.utils.DATA;
import com.hanhtet.stumanpro.utils.Functions;

import com.hanhtet.stumanpro.utils.InternetConnectionChecker;
import com.hanhtet.stumanpro.utils.SheetUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class UserManagementController {
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
    private TableColumn<User, String> firstName;
    @FXML
    private TableColumn<User, String> lastName;
    @FXML
    private TableColumn<User, String> userADDR;
    @FXML
    private TableColumn<User, String> userEmail;
    @FXML
    private TableColumn<User, String> userPass;
    @FXML
    private TableColumn<User, String> userID;
    @FXML
    private TableColumn<User, String> userPH;
    @FXML
    private TableColumn<User, String> userPic;
    @FXML
    private TableColumn<User, String> userRole;
    @FXML
    private TableView<User> userTable;

    @FXML
    void ADDUser(ActionEvent event) {
        System.out.println("Adding the User");
        User user = getUser();
        boolean result = functions.RegisterUser(user, true);
        if (result){
            //URL soundUrl = getClass().getResource(DATA.SUCCESS_SOUND);
            //functions.playAudio(soundUrl);
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

    private void addDataTable(){
        List<User> usersFromSheet = functions.getUsersFromSheet();
        if (usersFromSheet != null && !usersFromSheet.isEmpty()) {
            userTable.getItems().clear();
            userTable.getItems().addAll(usersFromSheet);
            setCellFactoryForColumns();
        }
    }

    private void setCellFactoryForColumns() {
        userID.setCellFactory(TextFieldTableCell.forTableColumn());
        firstName.setCellFactory(TextFieldTableCell.forTableColumn());
        lastName.setCellFactory(TextFieldTableCell.forTableColumn());
        userEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        userPass.setCellFactory(TextFieldTableCell.forTableColumn());
        userPH.setCellFactory(TextFieldTableCell.forTableColumn());
        userADDR.setCellFactory(TextFieldTableCell.forTableColumn());
        userRole.setCellFactory(TextFieldTableCell.forTableColumn());
        // state the editable or not
        userID.setEditable(false);
        firstName.setEditable(true);
        lastName.setEditable(true);
        userEmail.setEditable(true);
        // add actions
        firstName.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setFirstname(event.getNewValue());
            userTable.getItems().set(event.getTablePosition().getRow(), user);
            edit(user);
        });
        lastName.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setLastname(event.getNewValue());
            userTable.getItems().set(event.getTablePosition().getRow(), user);
            edit(user);
        });
        userEmail.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setName(event.getNewValue());
            userTable.getItems().set(event.getTablePosition().getRow(), user);
            edit(user);
        });
    }

    private void edit(User user){
        List<Object> newData = new ArrayList<>();
        newData.add(user.getId());
        newData.add(user.getFirstname());
        newData.add(user.getFirstname());
        newData.add(user.getEmail());
        newData.add(user.getPassword());
        newData.add(user.getPhone_no());
        newData.add(user.getPicture());
        newData.add(user.getAddress());
        newData.add(user.getRole());
        String updatedId = user.getId().toString();
        try {
            SheetUtils.editDataInLocalFile(updatedId, newData, DATA.DOWNLOAD_XLXS_FOLDER_PATH +"\\"+"lcfa_users.xlsx");
        } catch (Exception e) {
            System.err.println("Something went wrong with editing the course!"+e);
        }
    }

    @NotNull
    private User getUser() {
        String firstName = first_name_input.getText();
        String lastName = last_name_input.getText().toString();
        String email = email_input.getText().toString();
        String passString = "P@ssw0rd2006";
        String phString = phno_input.getText().toString();
        String address = (String) address_input.getText().toString();
        String user_type = user_type_input.getValue().toString().toLowerCase();
        User user = new User(firstName, lastName, email, passString, phString,"null",address,user_type);
        return user;
    }

    @FXML
    void initialize() {
        assert first_name_input != null : "fx:id=\"first_name_input\" was not injected: check your FXML file 'user_add.fxml'.";
        assert last_name_input != null : "fx:id=\"last_name_input\" was not injected: check your FXML file 'user_add.fxml'.";
        assert email_input != null : "fx:id=\"email_input\" was not injected: check your FXML file 'user_add.fxml'.";
        assert phno_input != null : "fx:id=\"phno_input\" was not injected: check your FXML file 'user_add.fxml'.";
        assert user_type_input != null : "fx:id=\"user_type_input\" was not injected: check your FXML file 'user_add.fxml'.";
        assert address_input != null : "fx:id=\"address_input\" was not injected: check your FXML file 'user_add.fxml'.";
        
        try{
            ObservableList<String> userTypes = FXCollections.observableArrayList(
                    "Admin",
                    "Teacher",
                    "Student"
            );
            user_type_input.setItems(userTypes);
        } catch (Exception e){
            System.err.println(e);
        }

        try {
            userID.setCellValueFactory(new PropertyValueFactory<>("id"));
            firstName.setCellValueFactory(new PropertyValueFactory<>("firstname"));
            lastName.setCellValueFactory(new PropertyValueFactory<>("lastname"));
            userEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            userPass.setCellValueFactory(new PropertyValueFactory<>("password"));
            userPH.setCellValueFactory(new PropertyValueFactory<>("phone_no"));
            userPic.setCellValueFactory(new PropertyValueFactory<>("picture"));
            userADDR.setCellValueFactory(new PropertyValueFactory<>("address"));
            userRole.setCellValueFactory(new PropertyValueFactory<>("role"));
            addDataTable();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
