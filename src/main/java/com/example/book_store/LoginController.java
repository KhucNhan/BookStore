package com.example.book_store;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    private final ConnectDB connectDB = new ConnectDB();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    public void initialize() {
        // Khởi tạo nếu cần thiết
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (validateLogin(username, password) && isActive(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Đăng nhập thành công", "Xin chào, " + username + "!");
        } else if (validateLogin(username, password) && !isActive(username,password)){
            showAlert(Alert.AlertType.INFORMATION, "Đăng nhập thất bại", "Tài khoản đã bị hủy.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Đăng nhập thất bại", "Tên đăng nhập hoặc mật khẩu không chính xác.");
        }
    }

    private boolean isActive(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ? and status = true";
        return login(username, password, query);
    }

    private boolean validateLogin(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        return login(username, password, query);
    }

    private boolean login(String username, String password, String query) {
        try {
            Connection conn = connectDB.connectionDB();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể kết nối cơ sở dữ liệu.");
            return false;
        }
    }

    @FXML
    private void handleForgotPasswordLink() {
        showAlert(Alert.AlertType.INFORMATION, "Quên mật khẩu", "Vui lòng liên hệ quản trị viên để đặt lại mật khẩu.");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
