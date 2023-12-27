package com.hanhtet.stumanpro;

import com.hanhtet.stumanpro.alert.CustomAlertBox;
import com.hanhtet.stumanpro.entity.User;
import com.hanhtet.stumanpro.utils.CRUD;
import com.hanhtet.stumanpro.utils.DATA;
import com.hanhtet.stumanpro.utils.Functions;
import com.hanhtet.stumanpro.utils.LOG;
import com.hanhtet.stumanpro.utils.OffSheetWriter;
import com.hanhtet.stumanpro.utils.UserSuggestionHandler;
import java.io.File;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class UserManagementController {

  private final Functions functions = new Functions();

  @FXML
  private ResourceBundle resources;

  @FXML
  private URL location;

  @FXML
  private TextField firstNameInput;

  @FXML
  private TextField lastNameInput;

  @FXML
  private TextField emailInput;

  @FXML
  private TextField phonoInput;

  @FXML
  private ComboBox<String> userTypeInput;

  @FXML
  private TextArea addressInput;

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
  private Label userNameLabel;

  @FXML
  private VBox suggestionContainer;

  @FXML
  private TextField searchField;

  @FXML
  private ScrollPane scrollPane;

  private User currentSelectedUser;

  private UserSuggestionHandler<User> suggestionHandler;
  private CRUD<User> crud = new CRUD<>();

  @FXML
  private void deleteUser(ActionEvent event) {
    try {
      if (crud.delete(currentSelectedUser)) {
        LOG.logInfo("Successfully deleted the user!");
        currentSelectedUser = null;
        suggestionHandler.clearSearchSuggestions();
        searchField.clear();
        perfromSearchAction(true);
      } else {
        LOG.logWarn("Can't delete the user.");
      }
    } catch (IOException e) {
      LOG.logMe(Level.WARNING, "Error occurred during at deleting user!", e);
    }
  }

  @FXML
  void addUser(ActionEvent event) {
    User user = getUser();
    boolean result = functions.registerUser(user, true);
    if (result) {
      firstNameInput.clear();
      lastNameInput.clear();
      emailInput.clear();
      phonoInput.clear();
      addressInput.clear();
      userTypeInput.getSelectionModel().clearSelection();
    } else {
      Node source = (Node) event.getSource();
      Stage primaryStage = (Stage) source.getScene().getWindow();
      CustomAlertBox.showAlert(
        Alert.AlertType.ERROR,
        primaryStage,
        "User ADD Error",
        "User can't register!",
        "User don't have permission to add the user or User might be already added!"
      );
    }
  }

  private void addDataTable() {
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

  private void edit(User user) {
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
    String updatedId = user.getId();
    try {
      OffSheetWriter.editDataById(
        updatedId,
        newData,
        DATA.DOWNLOAD_XLXS_FOLDER_PATH + File.separator + "lcfa_users.xlsx"
      );
    } catch (Exception e) {
      LOG.logMe(
        Level.WARNING,
        "Something went wrong with editing the course",
        e
      );
    }
  }

  @NotNull
  private User getUser() {
    String firstname = firstNameInput.getText();
    String lastname = lastNameInput.getText();
    String email = emailInput.getText();
    String passString = "P@ssw0rd2006";
    String phString = phonoInput.getText();
    String address = addressInput.getText();
    String userType = userTypeInput.getValue().toLowerCase();
    return new User(
      firstname,
      lastname,
      email,
      passString,
      phString,
      "null",
      address,
      userType
    );
  }

  private ObservableList<User> performSearch(String searchText) {
    List<User> allUsers = functions.getUsersFromSheet();
    ObservableList<User> filteredUsers = FXCollections.observableArrayList();

    for (User user : allUsers) {
      if (
        user.getName().toLowerCase().contains(searchText.toLowerCase()) ||
        user.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
        user.getId().toLowerCase().contains(searchText.toLowerCase())
      ) {
        filteredUsers.add(user);
      }
    }
    return filteredUsers;
  }

  private void porpulateSuggestions(
    ObservableList<User> filteredUsers,
    boolean empty
  ) {
    if (empty) {
      suggestionHandler.populateSearchSuggestions(
        performSearch(""),
        this::handleSuggestionClick
      );
    } else {
      suggestionHandler.populateSearchSuggestions(
        filteredUsers,
        this::handleSuggestionClick
      );
    }
  }

  private void handleSuggestionClick(User selectedUser) {
    userNameLabel.setText(
      "You have selected the user: " + selectedUser.getName()
    );
    currentSelectedUser = selectedUser;
    suggestionHandler.clearSearchSuggestions();

    Label selectedLabel = new Label(selectedUser.getName());
    selectedLabel.getStyleClass().add("label-suggestion");
    selectedLabel.setMaxWidth(Double.MAX_VALUE);
    selectedLabel.getStyleClass().add("selected-user-label");
    suggestionContainer.getChildren().add(selectedLabel);
    searchField.setText(selectedUser.getName().trim());
  }

  private void perfromSearchAction(boolean deleteMethod) {
    try {
      if (deleteMethod) {
        userNameLabel.setText("You haven't selected the user yet.");
      }
      searchField
        .textProperty()
        .addListener((observable, oldValue, newValue) -> {
          if (!newValue.isEmpty()) {
            ObservableList<User> filteredUsers = performSearch(newValue);
            if (deleteMethod) {
              porpulateSuggestions(filteredUsers, false);
            } else {
              userTable.getItems().clear();
              userTable.getItems().addAll(filteredUsers);
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
      LOG.logWarn("User delete can't be loaded.");
    }
  }

  private void reClearTable() {
    userTable.getItems().clear();
    addDataTable();
  }

  @FXML
  void initialize() {
    assert firstNameInput !=
    null : "fx:id=\"firstNameInput\" was not injected: check your FXML file 'user_add.fxml'.";
    assert lastNameInput !=
    null : "fx:id=\"lastNameInput\" was not injected: check your FXML file 'user_add.fxml'.";
    assert emailInput !=
    null : "fx:id=\"emailInput\" was not injected: check your FXML file 'user_add.fxml'.";
    assert phonoInput !=
    null : "fx:id=\"phonoInput\" was not injected: check your FXML file 'user_add.fxml'.";
    assert userTypeInput !=
    null : "fx:id=\"userTypeInput\" was not injected: check your FXML file 'user_add.fxml'.";
    assert addressInput !=
    null : "fx:id=\"addressInput\" was not injected: check your FXML file 'user_add.fxml'.";

    suggestionHandler =
      new UserSuggestionHandler<>(suggestionContainer, scrollPane);
    try {
      ObservableList<String> userTypes = FXCollections.observableArrayList(
        "Admin",
        "Teacher",
        "Student"
      );
      userTypeInput.setItems(userTypes);
    } catch (Exception e) {
      LOG.logWarn("User add can't be loaded.");
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
      perfromSearchAction(false);
    } catch (Exception e) {
      LOG.logWarn("User Table can't be loaded.");
    }

    try {
      perfromSearchAction(true);
    } catch (Exception e) {
      LOG.logWarn("User delete can't be loaded.");
    }
  }
}
