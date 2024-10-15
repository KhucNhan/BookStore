package com.example.book_store;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;

public class LoginController {

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

        // Xử lý đăng nhập
        if (validateLogin(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Đăng nhập thành công", "Xin chào, " + username + "!");
            ConnectDB connectDB = new ConnectDB();
            Connection connection = connectDB.connectionDB();
        } else {
            showAlert(Alert.AlertType.ERROR, "Đăng nhập thất bại", "Tên đăng nhập hoặc mật khẩu không chính xác.");
        }
    }

    private boolean validateLogin(String username, String password) {
        return true;
    }

    @FXML
    private void handleForgotPassword() {
        // Xử lý quên mật khẩu
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
