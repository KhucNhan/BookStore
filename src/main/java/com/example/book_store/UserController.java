package com.example.book_store;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.*;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserController {
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();
    private final User currentUser = LoginController.currentUser;

    public boolean emailValidator(String email) {
        String EMAIL_REGEX = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public boolean phoneValidator(String phone) {
        String EMAIL_REGEX = "/(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)/g";
        return Pattern.matches(EMAIL_REGEX, phone);
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

    private boolean addUser(String name, String username, String password, String reEnterPassword, Date dateOfBirth, String gender, String phone, String address, String email) {
        String query = "insert into user (Name, Username, Password, DateOfBirth, Gender, Phone, Address, Email, Status)" +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, username);
            if (password.equals(reEnterPassword)) {
                preparedStatement.setString(3, password);
                preparedStatement.setDate(4, dateOfBirth);
                preparedStatement.setString(5, gender);
                if (phoneValidator(phone)) {
                    preparedStatement.setString(6, phone);
                    preparedStatement.setString(7, address);
                    if (emailValidator(email)) {
                        preparedStatement.setString(8, email);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Failed", "Enter right email address!");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Enter right phone number!");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Incorrect password");
            }
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
