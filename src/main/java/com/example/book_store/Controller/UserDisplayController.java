package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;
import com.example.book_store.Entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Date;

public class UserDisplayController {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> userID;
    @FXML
    private TableColumn<User, String> name;
    @FXML
    private TableColumn<User, String> username;
    @FXML
    private TableColumn<User, String> email;
    @FXML
    private TableColumn<User, String> dateOfBirth;
    @FXML
    private TableColumn<User, String> gender;
    @FXML
    private TableColumn<User, String> phone;
    @FXML
    private TableColumn<User, String> address;
    @FXML
    private TableColumn<User, String> status;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();

    // Phương thức khởi tạo
    public void initialize() {

        userID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        dateOfBirth.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        gender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadUserData();
        userTable.setItems(userList);
    }

    private void loadUserData() {
        String query = "SELECT * FROM users ";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int userID = rs.getInt("UserID");
                String name = rs.getString("Name");
                String username = rs.getString("Username");
                String password = rs.getString("Password");
                Date dateOfBirth = rs.getDate("DateOfbirth");
                String gender = rs.getString("Gender");
                String phone = rs.getString("Phone");
                String address = rs.getString("Address");
                String email = rs.getString("Email");
                boolean status = rs.getBoolean("Status");

                userList.add(new User(userID, name, username, password, dateOfBirth, gender, phone, address, email, status));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Stage stage;
    private Scene scene;
    private Parent root;

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

    @FXML
    public void logout(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/login.fxml");
    }

    @FXML
    public void deactivationUser(ActionEvent event) {
        // Thực hiện mã để vô hiệu hóa người dùng
    }

    @FXML
    public void updateUserPassword(ActionEvent event) {
        // Thực hiện mã để cập nhật mật khẩu người dùng
    }

    @FXML
    public void updateUserInformation(ActionEvent event) {
        // Thực hiện mã để cập nhật thông tin người dùng
    }

    @FXML
    public void showAddBookDialog(ActionEvent event) {
        // Thực hiện mã để hiển thị hộp thoại thêm sách
    }

    @FXML
    public void goToHome(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/home.fxml");
    }

    @FXML
    public void goToCart(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/cart.fxml");
    }

    @FXML
    public void goToAddBookScene(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/addBook.fxml");
    }
}
