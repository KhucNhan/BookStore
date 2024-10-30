package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.example.book_store.Entity.Bill;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class AdminOrderController {

    @FXML
    private Button goToHome;
    @FXML
    private Button goToCart;
    @FXML
    private Button searchButton;
    @FXML
    private MenuButton settingsMenuButton;
    @FXML
    private TableView<Bill> billTable;

    private ObservableList<Bill> billList;
    private UserController userController = new UserController();
    private BillController billController = new BillController();
    private OrderController orderController = new OrderController();
    private CartController cartController = new CartController();
    private BookController bookController = new BookController();
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();

    @FXML
    public void initialize() {
        billList = FXCollections.observableArrayList();
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
        userController.deactivationUser(event);
    }

    @FXML
    public void updateUserPassword(ActionEvent event) {
        userController.updateUserPassword(event);
    }

    @FXML
    public void updateUserInformation(ActionEvent event) {
        userController.updateUserInformation(event);
    }

    @FXML
    public void goToHome(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/home.fxml");
    }

}
