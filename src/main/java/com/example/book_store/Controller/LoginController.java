package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;
import com.example.book_store.Entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    private final ConnectDB connectDB = new ConnectDB();
    Connection connection = connectDB.connectionDB();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink forgotPasswordLink;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public void initialize() {

    }

    public static User currentUser;

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (validateLogin(username, password) && isActive(username, password)) {
            currentUser = getUSerByUserNameAndPassword(username, password);
            showAlert(Alert.AlertType.INFORMATION, "Đăng nhập thành công", "Xin chào " + currentUser.getRole() + " " + currentUser.getName() + "!");
            Parent root = FXMLLoader.load(getClass().getResource("test.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root, 720, 480);
            stage.setScene(scene);
            stage.show();
        } else if (validateLogin(username, password) && !isActive(username, password)) {
            showAlert(Alert.AlertType.ERROR, "Đăng nhập thất bại", "Tài khoản đã bị hủy.");
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

    public User getUSerByUserNameAndPassword(String username, String password) {
        String query = "select * from users where Username = ? and Password = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
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

    private boolean login(String username, String password, String query) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
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
