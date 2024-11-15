package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;

import com.example.book_store.Entity.Bill;
import com.example.book_store.Entity.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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
    @FXML
    public Button goToCart;
    @FXML
    public Button goToUser;
    @FXML
    public Button goToOrder;

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
    @FXML
    public TableColumn<User, Void> action;

    private ObservableList<User> userList = FXCollections.observableArrayList();

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
        action.setCellFactory(column -> new TableCell<>() {
            private Button edit = new Button("Sửa");
            private Button delete = new Button("Xóa");

            @Override
            protected void updateItem(Void act, boolean empty) {
                super.updateItem(act, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    edit.setOnAction(e -> {
                        updateUserInformation(user);
                    });

                    delete.setOnAction(e -> {
                        deactivationUser(user.getUserID());
                    });
                    setGraphic(new HBox(10, edit, delete));
                }
            }
        });
        action.setCellValueFactory(cellData -> new SimpleObjectProperty<>(null));

        loadUserData();
        userTable.setItems(userList);
    }

    private void loadUserData() {
        String query = "SELECT * FROM users ";
        userList.clear();

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int userID = rs.getInt("UserID");
                String name = rs.getString("Name");
                String username = rs.getString("Username");
                String password = rs.getString("Password");
                java.util.Date dateOfBirth = rs.getDate("DateOfbirth");
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
                ;
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

    private boolean validateUAndP(String value) {
        return value.length() >= 8;
    }

    protected boolean add(String name, String username, String password, Date dateOfBirth, String gender, String phone, String address, String email) {
        String query = "insert into users (Name, Username, Password, DateOfBirth, Gender, Phone, Address, Email)" +
                "values (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            if (!validateUAndP(username) || !validateUAndP(password)) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Username and Password length must equal 8 or more");
                return false;
            }
            if (!phoneValidator(phone)) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Enter right phone number!");
                return false;
            }
            if (!emailValidator(email)) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Enter right email address!");
                return false;
            }
            if (!validateAddress(address)) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Address can not filled just with number");
                return false;
            }
            if (!validateName(name)) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Text only");
                return false;
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setDate(4, dateOfBirth);
            preparedStatement.setString(5, gender);
            preparedStatement.setString(6, phone);
            preparedStatement.setString(7, address);
            preparedStatement.setString(8, email);

            int row = preparedStatement.executeUpdate();
            return row != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean deactivationUser(int userID) {
        String query = "update users set Status = false where UserID = ?";
        try {
            if (showConfirmation("Delete account", "Are you sure want to delete your account ?")) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, userID);
                int row = preparedStatement.executeUpdate();
                ;
                showAlert(Alert.AlertType.INFORMATION, "Successful", "Delete account successful, you'll move to login scene");
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


    public boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> option = alert.showAndWait();
        return option.get() == ButtonType.OK;
    }

    private boolean updateUserInformation(User user) {
        TextField nameField = new TextField(user.getName());
        nameField.setPromptText("Enter new name");

        DatePicker dateOfBirthPicker = new DatePicker();
        dateOfBirthPicker.setValue(LocalDate.parse(String.valueOf(user.getDateOfBirth())));

        TextField genderField = new TextField(user.getGender());
        genderField.setPromptText("Enter new gender");

        TextField phoneField = new TextField(user.getPhone());
        phoneField.setPromptText("Enter new phone number");

        TextField addressField = new TextField(user.getAddress());
        addressField.setPromptText("Enter new address");

        TextField emailField = new TextField(user.getEmail());
        emailField.setPromptText("Enter new email");

        Button saveButton = new Button("Lưu");

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
                if (!validateBirthdate(dateOfBirthPicker.getValue())) {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Enter right value");
                    return;
                }
                if (!validateAddress(addressField.getText())) {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Enter address again");
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
                if (!validateName(nameField.getText())) {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Text only");
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
                preparedStatement.setInt(7, user.getUserID());

                int row = preparedStatement.executeUpdate();

                if (row != 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Successful", "Update information successful");
                    loadUserData();
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

    public static boolean validateName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        return Pattern.matches("[\\p{L}\\s]+", name);
    }

    public static boolean validateBirthdate(LocalDate birthdate) {
        if (birthdate == null) {
            return false;
        }

        LocalDate currentDate = LocalDate.now();

        return !birthdate.isAfter(currentDate);
    }

    @FXML
    public boolean updateUserInformation(ActionEvent event) {
        updateUserInformation(currentUser);
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
                ;
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

    @FXML
    public void goToHome(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/home.fxml");
    }

    @FXML
    public void goToCart(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/cart.fxml");
    }


    @FXML
    public void logout(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/login.fxml");
    }

    @FXML
    public void goToUser(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/user.fxml");
    }

    @FXML
    public void goToOrder(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/order.fxml");
    }

    @FXML
    public void goToHistory(ActionEvent actionEvent) throws IOException {
        goToScene(actionEvent, "/com/example/book_store/view/bill.fxml");
    }

    @FXML
    public void goToTop5(ActionEvent actionEvent) throws IOException {
        goToScene(actionEvent, "/com/example/book_store/view/statistical.fxml");
    }

    @FXML
    public void add(ActionEvent actionEvent) {
        TextField name = new TextField();
        name.setPromptText("Nhập họ tên");
        TextField username = new TextField();
        username.setPromptText("Tên đăng nhập");
        TextField password = new TextField();
        password.setPromptText("Mật khẩu");
        TextField email = new TextField();
        email.setPromptText("Email");
        DatePicker dateOfBirth = new DatePicker();
        dateOfBirth.setPromptText("Ngày sinh");
        TextField gender = new TextField();
        gender.setPromptText("Giới tính");
        TextField phone = new TextField();
        phone.setPromptText("Số điện thoại");
        TextField address = new TextField();
        address.setPromptText("Địa chỉ");
        Button saveButton = new Button("Thêm");

        VBox vbox = new VBox(name, username, password, email, dateOfBirth, gender, phone, address, saveButton);
        vbox.setSpacing(10);
        Scene scene = new Scene(vbox, 240, 480);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Thêm user");
        stage.show();

        saveButton.setOnAction(e -> {
            add(name.getText(), username.getText(), password.getText(), Date.valueOf(dateOfBirth.getValue()), gender.getText(), phone.getText(), address.getText(), email.getText());
            stage.close(); // Đóng cửa sổ thêm sách
        });
    }

    public static boolean validateAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }

        return address.matches(".*[a-zA-Z]+.*");
    }
}
