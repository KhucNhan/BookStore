package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;

import com.example.book_store.Entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class UserController {
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();
    private final User currentUser = Authentication.currentUser;
//    private BookController bookController = new BookController();

    private Stage stage;
    private Scene scene;
    private Parent root;

    public boolean emailValidator(String email) {
        String EMAIL_REGEX = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public boolean phoneValidator(String phone) {

        String PHONE_REGEX = "^(03|05|07|08|09)([0-9]{8})$";
        return Pattern.matches(PHONE_REGEX, phone);

    }

    public User getUSerByID(int userID) {
        String query = "select * from users where UserID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getDate(5), resultSet.getString(6), resultSet.getString(7), resultSet.getString(8), resultSet.getString(9), resultSet.getBoolean(10), resultSet.getString(11));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML

    public boolean deactivationUser(ActionEvent event) {
        String query = "update users set Status = false where UserID = ?";
        try {
            if (showConfirmation("Delete account", "Are you sure want to delete your account ?")) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, currentUser.getUserID());
                int row = preparedStatement.executeUpdate();
                connection.close();
                showAlert(Alert.AlertType.INFORMATION, "Successful", "Delete account successful, you'll move to login scene");
                goToScene(event, "/com/example/book_store/view/login.fxml");
                return row != 0;
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Delete Account", "Cancel");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> option = alert.showAndWait();
        return option.get() == ButtonType.OK;
    }

    @FXML
    public boolean updateUserInformation(ActionEvent event) {
        // Tạo các trường nhập liệu
        TextField nameField = new TextField(currentUser.getName());
        nameField.setPromptText("Enter new name");

        DatePicker dateOfBirthPicker = new DatePicker();
        dateOfBirthPicker.setPromptText(String.valueOf(currentUser.getDateOfBirth()));

        TextField genderField = new TextField(currentUser.getGender());
        genderField.setPromptText("Enter new gender");

        TextField phoneField = new TextField(currentUser.getPhone());
        phoneField.setPromptText("Enter new phone number");

        TextField addressField = new TextField(currentUser.getAddress());
        addressField.setPromptText("Enter new address");

        TextField emailField = new TextField(currentUser.getEmail());
        emailField.setPromptText("Enter new email");

        Button saveButton = new Button("Save");

        VBox vbox = new VBox(nameField, dateOfBirthPicker, genderField, phoneField, addressField, emailField, saveButton);
        vbox.setSpacing(10);
        Scene scene = new Scene(vbox, 300, 400);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Update User Information");
        stage.show();

        saveButton.setOnAction(e -> {
            try {
                if (nameField.getText().isEmpty() || dateOfBirthPicker.getValue() == null ||
                        genderField.getText().isEmpty() || phoneField.getText().isEmpty() ||
                        addressField.getText().isEmpty() || emailField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled!");
                    return;
                }

                if (!phoneValidator(phoneField.getText())) {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Enter a valid phone number!");
                    return;
                }

                if (!emailValidator(emailField.getText())) {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Enter a valid email address!");
                    return;
                }

                String query = "UPDATE users SET Name = ?, DateOfBirth = ?, Gender = ?, Phone = ?, Address = ?, Email = ? WHERE UserID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, nameField.getText());
                preparedStatement.setDate(2, Date.valueOf(dateOfBirthPicker.getValue()));
                preparedStatement.setString(3, genderField.getText());
                preparedStatement.setString(4, phoneField.getText());
                preparedStatement.setString(5, addressField.getText());
                preparedStatement.setString(6, emailField.getText());
                preparedStatement.setInt(7, currentUser.getUserID());

                int row = preparedStatement.executeUpdate();
                connection.close();

                if (row != 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Successful", "Update information successful");
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Failed", "Update information failed");
                }

                stage.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        return true;
    }


    @FXML
    public boolean updateUserPassword(ActionEvent event) {
        PasswordField oldPassword = new PasswordField();
        oldPassword.setPromptText("Enter old password");
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("Enter new password");
        PasswordField reEnterNewPassword = new PasswordField();
        reEnterNewPassword.setPromptText("Enter new password again");

        AtomicInteger row = new AtomicInteger();

        Button saveButton = new Button("Save");

        VBox vbox = new VBox(oldPassword, newPassword, reEnterNewPassword, saveButton);
        vbox.setSpacing(10);
        Scene scene = new Scene(vbox, 240, 480);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Change password");
        stage.show();

        saveButton.setOnAction(e -> {
            if (oldPassword.getText().isEmpty() || newPassword.getText().isEmpty() || reEnterNewPassword.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "No blank!");
                return;
            }
            if (!(oldPassword.getText()).equals(getUserPassword(currentUser.getUserID())) || !newPassword.getText().equals(reEnterNewPassword.getText())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Incorrect password");
                System.out.println(oldPassword.getText() + " " + getUserPassword(currentUser.getUserID()));
                return;
            }
            if (oldPassword.getText().equals(getUserPassword(currentUser.getUserID())) && newPassword.getText().length() < 8) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Password must have at least 8 letter or number");
            }
            String query = "update users set Password = ? where UserID = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, newPassword.getText());
                preparedStatement.setInt(2, currentUser.getUserID());
                row.set(preparedStatement.executeUpdate());
                connection.close();
                if (row.get() != 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Successful", "Change password successful, please login again");
                    goToScene(event, "/com/example/book_store/view/login.fxml");
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Failed", "Change password successful");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            stage.close();
        });
        return row.equals(0);
    }

    public String getUserPassword(int userID) {
        String query = "select * from users where UserID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Password");
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    public void logout(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/login.fxml");
    }

    @FXML
    public void goToScene(ActionEvent event, String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        root = loader.load();

        // Kiểm tra nguồn sự kiện
        if (event.getSource() instanceof Node) {
            // Nếu nguồn sự kiện là một Node (ví dụ như Button), thì lấy Stage từ Node
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            // Ép kiểu nguồn sự kiện từ MenuItem (không thuộc về root) về Node
            Node node = ((MenuItem) event.getSource()).getParentPopup().getOwnerNode();
            Stage stage = (Stage) node.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

}
