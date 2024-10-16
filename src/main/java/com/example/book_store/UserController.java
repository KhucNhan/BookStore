package com.example.book_store;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class UserController {
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();

    public boolean emailValidator(String email) {
        String EMAIL_REGEX = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        return Pattern.matches(EMAIL_REGEX, email);
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
                preparedStatement.setString(6, phone);
                preparedStatement.setString(7, address);
                if (emailValidator(email)) {
                    preparedStatement.setString(8, email);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Enter right email address!");
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
