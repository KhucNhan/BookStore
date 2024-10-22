package com.example.book_store.Controller;
import com.example.book_store.ConnectDB;
import com.example.book_store.Entity.Book;
import com.example.book_store.Entity.CartItem;
import com.example.book_store.Entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.*;


public class BookOrderController {
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();
    private final User currentUser = Authentication.currentUser;

    @FXML
    private TableView<CartItem> cartTableView;

    @FXML
    private TableColumn<CartItem, String> title;

    @FXML
    private TableColumn<CartItem, String> image;

    @FXML
    private TableColumn<CartItem, Double> price;

    @FXML
    private TableColumn<CartItem, Integer> amount;

    @FXML
    private TableColumn<CartItem, Double> total;

    @FXML
    private Label totalCartLabel;

    @FXML
    private Button buyButton;

    private ObservableList<Book> cart;

    @FXML
    public void initialize() {
        title.setCellValueFactory(new PropertyValueFactory<CartItem, String>("title"));
        image.setCellValueFactory(new PropertyValueFactory<CartItem, String>("image"));
        image.setCellFactory(column -> new TableCell<CartItem, String>() {

            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(new Image(imagePath));
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);
                    setGraphic(imageView);
                }
            }
        });
        price.setCellValueFactory(new PropertyValueFactory<CartItem, Double>("price"));
        amount.setCellValueFactory(new PropertyValueFactory<CartItem, Integer>("amount"));
        total.setCellValueFactory(new PropertyValueFactory<CartItem, Double>("total"));

        loadCart();
    }

    private void loadCart() {
        ObservableList<CartItem> cart = FXCollections.observableArrayList();
        String query = "SELECT Books.Title, Books.Image, Books.Price, Books_Orders.Amount, Orders.TotalAmount FROM Books " +
                "inner join Books_Orders on Books.BookID = Books_Orders.BookID " +
                "inner join Orders on Books_Orders.OrderID = Orders.OrderID " +
                "inner join Bills on Orders.BillID = Bills.BillID " +
                "where Bills.UserID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, currentUser.getUserID());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                CartItem cartItem = new CartItem(
                        resultSet.getString("Title"),
                        resultSet.getString("Image"),
                        resultSet.getDouble("Price"),
                        resultSet.getInt("Amount"),
                        resultSet.getDouble("TotalAmount")
                );
                cart.add(cartItem);
            }

            System.out.println(cart);

            cartTableView.setItems(cart);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load book data");
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
    private void buySelectedBooks() {
        if (!cart.isEmpty()) {
            // Logic xử lý đặt hàng cho các sản phẩm trong giỏ
            System.out.println("Đặt hàng thành công cho " + cart.size() + " sản phẩm!");
        } else {
            System.out.println("Giỏ hàng trống!");
        }
    }
}