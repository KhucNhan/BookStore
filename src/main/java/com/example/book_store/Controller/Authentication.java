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

public class Authentication {
    private final ConnectDB connectDB = new ConnectDB();
    Connection connection = connectDB.connectionDB();
    private UserController userController = new UserController();

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
    private long startTime;
    private long endTime;

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        startTime = System.currentTimeMillis(); // Bắt đầu tính thời gian
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (isActive(username, password)) {
            currentUser = getUSerByUserNameAndPassword(username, password);
            showAlert(Alert.AlertType.INFORMATION, "Đăng nhập thành công", "Xin chào " + currentUser.getRole() + " " + currentUser.getName() + "!");
            if (currentUser.getRole().equalsIgnoreCase("admin")) {
                goToScene(event, "/com/example/book_store/view/home.fxml");
            } else {
                goToScene(event, "/com/example/book_store/view/homeUser.fxml");
            }
            endTime = System.currentTimeMillis(); // Kết thúc tính thời gian
            long duration = endTime - startTime;
            System.out.println("Thời gian thực thi: " + duration + " ms");
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

    public static boolean validateAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }

        return address.matches(".*[a-zA-Z]+.*");
    }

    @FXML
    private boolean signUp(ActionEvent event) throws IOException {
        String uName = name.getText();
        String uUsername = username.getText();
        String uPassword = password.getText();
        String uReEnter = reEnterPassword.getText();
        Date uDateOfBirth = Date.valueOf(dateOfBirth.getValue());
        String uGender = gender.getText();
        String uPhone = phone.getText();
        String uAddress = address.getText();
        String uEmail = email.getText();
        if (!validateUAndP(uUsername) || !validateUAndP(uPassword)) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Username and Password length must equal 8 or more");
            return false;
        }
        if (!uPassword.equals(uReEnter)) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Password incorrect");
            return false;
        }
        if (validateAddress(uAddress)) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Address can not filled just with number");
            return false;
        }
        if (userController.add(uName, uUsername, uPassword, uDateOfBirth, uGender, uPhone, uAddress, uEmail)) {
            showAlert(Alert.AlertType.INFORMATION, "Successful", "Sign up successful");
            createCart();
            goToScene(event, "/com/example/book_store/view/login.fxml");
            return true;
        }
        return false;
    }

    private boolean createCart() {
        String query = "insert into cart (UserID) values ((select UserID from users group by UserID order by UserID desc limit 1 ));";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            int row = preparedStatement.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void selectGender(javafx.event.ActionEvent event) {
        MenuItem selectedGender = (MenuItem) event.getSource();
        gender.setText(selectedGender.getText());
    }

    public void goToSignUp(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/register.fxml");
    }

    public void goToLogin(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/login.fxml");
    }

    private boolean validateUAndP(String value) {
        return value.length() >= 8;
    }

    @FXML
    public void goToScene(ActionEvent event, String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        root = loader.load();

        // Kiểm tra nguồn sự kiện
        if (event.getSource() instanceof Node) {
            // Nếu nguồn sự kiện là một Node (ví dụ như Button), thì lấy Stage từ Node
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            stage.setScene(scene);
//            stage.setFullScreen(true);
            stage.show();
        } else {
            // Ép kiểu nguồn sự kiện từ MenuItem (không thuộc về root) về Node
            Node node = ((MenuItem) event.getSource()).getParentPopup().getOwnerNode();
            Stage stage = (Stage) node.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            stage.setScene(scene);
//            stage.setFullScreen(true);
            stage.show();
        }
    }
}
