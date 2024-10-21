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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.regex.Pattern;

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

        // Khởi tạo nếu cần thiết

    }

    public static User currentUser;

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (validateLogin(username, password) && isActive(username, password)) {
            currentUser = getUSerByUserNameAndPassword(username, password);
            showAlert(Alert.AlertType.INFORMATION, "Đăng nhập thành công", "Xin chào " + currentUser.getRole() + " " + currentUser.getName() + "!");
            goToScene(event, "/com/example/book_store/view/home.fxml");
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
    public MenuButton gender;
    @FXML
    public TextField phone;
    @FXML
    public TextField address;
    @FXML
    public TextField email;

    @FXML
    private boolean signUp(ActionEvent event) {
        String query = "insert into users (Name, Username, Password, DateOfBirth, Gender, Phone, Address, Email)" +
                "values (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            if (password.getText().equals(reEnterPassword.getText())) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Incorrect password");
                return false;
            }
            if (phoneValidator(phone.getText())) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Enter right phone number!");
                return false;
            }
            if (emailValidator(email.getText())) {
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

    public boolean emailValidator(String email) {
        String EMAIL_REGEX = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public boolean phoneValidator(String phone) {

        String PHONE_REGEX = "^(03|05|07|08|09)([0-9]{8})$";
        return Pattern.matches(PHONE_REGEX, phone);

    }

    public void goToSignUp(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/register.fxml");
    }

    public void goToScene(ActionEvent event, String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
