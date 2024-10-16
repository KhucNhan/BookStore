package com.example.book_store;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.*;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserController {
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();
    private final User currentUser = LoginController.currentUser;
    @FXML
    public TextField name;
    @FXML
    public TextField username;
    @FXML
    public TextField password;
    @FXML
    public TextField reEnterPassword;
    @FXML
    public DatePicker dateOfBirth;
    @FXML
    public TextField gender;
    @FXML
    public TextField phone;
    @FXML
    public TextField address;
    @FXML
    public TextField email;

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
    private boolean deactivationUser() {
        String query = "update users set Status = false where UserID = ?";
        try {
            if (showConfirmation(Alert.AlertType.CONFIRMATION, "Delete account", "Are you sure want to delete your account ?")) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, currentUser.getUserID());
                int row = preparedStatement.executeUpdate();
                return row != 0;
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Delete Account", "Cancel");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showConfirmation(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> option = alert.showAndWait();
        return option.get() == ButtonType.OK;
    }



    @FXML
    private boolean addUser(ActionEvent event) {
        String query = "insert into users (Name, Username, Password, DateOfBirth, Gender, Phone, Address, Email)" +
                "values (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            if (!password.getText().equals(reEnterPassword.getText())) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Incorrect password");
                return false;
            }
            if (!phoneValidator(phone.getText())) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Enter right phone number!");
                return false;
            }
            if (!emailValidator(email.getText())) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Enter right email address!");
                return false;
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name.getText());
            preparedStatement.setString(2, username.getText());
            preparedStatement.setString(3, password.getText());
            preparedStatement.setDate(4, Date.valueOf(dateOfBirth.getValue()));
            preparedStatement.setString(5, gender.getText());
            preparedStatement.setString(6, phone.getText());
            preparedStatement.setString(7, address.getText());
            preparedStatement.setString(8, email.getText());
            int row = preparedStatement.executeUpdate();
            return row != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
