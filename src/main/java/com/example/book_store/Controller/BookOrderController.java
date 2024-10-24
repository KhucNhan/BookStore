package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;
import com.example.book_store.Entity.Book;
import com.example.book_store.Entity.CartItem;
import com.example.book_store.Entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;


public class BookOrderController {
    private UserController userController = new UserController();
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();
    private final User currentUser = Authentication.currentUser;
    public TableColumn<CartItem, Boolean> select;
    public CheckBox selectAllBooks;

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
        select.setCellValueFactory(new PropertyValueFactory<CartItem, Boolean>("select"));
        select.setCellValueFactory(cellData -> {
            CartItem item = cellData.getValue();
            return item.selectedProperty();
        });

        select.setCellFactory(column -> new TableCell<CartItem, Boolean>() {
            private final CheckBox selected = new CheckBox();

            @Override
            protected void updateItem(Boolean selectedValue, boolean empty) {
                super.updateItem(selectedValue, empty);
                if (empty || selectedValue == null) {
                    setGraphic(null);
                } else {
                    selected.setSelected(selectedValue);

                    selected.setOnAction(event -> {
                        CartItem item = getTableView().getItems().get(getIndex());
                        item.setSelected(selected.isSelected());
                        updateTotalCartLabel();
                    });
                    setGraphic(selected);
                }
            }
        });
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
        amount.setCellFactory(column -> new TableCell<CartItem, Integer>() {
            private final Button minus = new Button("-");
            private final Label currentAmount = new Label();
            private final Button plus = new Button("+");

            @Override
            protected void updateItem(Integer amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setGraphic(null);
                } else {
                    CartItem cartItem = getTableView().getItems().get(getIndex());
                    currentAmount.setText(cartItem.getAmount() + "");
                    minus.setOnAction(e -> {
                        minusAmount(cartItem.getIdOrder(), cartItem.getIdBook());
                        loadCart();
                        updateTotalCartLabel();
                    });

                    plus.setOnAction(e -> {
                        plusAmount(cartItem.getIdOrder(), cartItem.getIdBook());
                        loadCart();
                        updateTotalCartLabel();
                    });

                    HBox hBox = new HBox(minus, currentAmount, plus);
                    hBox.setAlignment(Pos.valueOf("CENTER"));
                    hBox.setSpacing(10);
                    setGraphic(hBox);
                }
            }
        });
        amount.setCellValueFactory(new PropertyValueFactory<CartItem, Integer>("amount"));
        total.setCellValueFactory(new PropertyValueFactory<CartItem, Double>("total"));

        loadCart();
    }

    private void minusAmount(int orderID, int bookID) {
        String query = "update books_orders set Amount = Amount - 1 where OrderID = ? and BookID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, orderID);
            preparedStatement.setInt(2, bookID);
            int row = preparedStatement.executeUpdate();
            if (row != 0) {
                query = "UPDATE Orders " +
                        "SET Amount = (" +
                        "    SELECT SUM(Amount) " +
                        "    FROM books_orders " +
                        "    WHERE OrderID = ?" +
                        ") " +
                        "WHERE OrderID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, orderID);
                preparedStatement.setInt(2, orderID);
                preparedStatement.executeUpdate();
            } else {
                System.out.println("Failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void plusAmount(int orderID, int bookID) {
        String query = "update books_orders set Amount = Amount + 1 where OrderID = ? and BookID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, orderID);
            preparedStatement.setInt(2, bookID);
            int row = preparedStatement.executeUpdate();
            if (row != 0) {
                query = "UPDATE Orders " +
                        "SET Amount = (" +
                        "    SELECT SUM(Amount) " +
                        "    FROM books_orders " +
                        "    WHERE OrderID = ?" +
                        ") " +
                        "WHERE OrderID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, orderID);
                preparedStatement.setInt(2, orderID);
                preparedStatement.executeUpdate();
            } else {
                System.out.println("Failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateNewAmountInCart(int value) {
        String query = "update orders set Amount = Amount - ? where ";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void selectAllBooks(ActionEvent event) {
        double total = 0.0;
        if (selectAllBooks.isSelected()) {
            for (CartItem item : cartTableView.getItems()) {
                item.setSelected(true);
                total += item.getPrice() * item.getAmount();
            }
            totalCartLabel.setText(String.format("$%.2f", total));
        } else {
            for (CartItem item : cartTableView.getItems()) {
                item.setSelected(false);
            }
            totalCartLabel.setText(String.format("$%.2f", total));
        }
    }

    private void updateTotalCartLabel() {
        double total = 0.0;
        for (CartItem item : cartTableView.getItems()) {
            if (item.isSelected()) {
                total += item.getPrice() * item.getAmount();
            }
        }
        totalCartLabel.setText(String.format("$%.2f", total));
    }

    private void loadCart() {
        ObservableList<CartItem> cart = FXCollections.observableArrayList();
        String query = "SELECT Books_Orders.OrderID, Books_Orders.BookID, Books.Title, Books.Image, Books.Price, Books_Orders.Amount, Orders.TotalAmount FROM Books " +
                "inner join Books_Orders on Books.BookID = Books_Orders.BookID " +
                "inner join Orders on Books_Orders.OrderID = Orders.OrderID " +
                "inner join Bills on Orders.BillID = Bills.BillID " +
                "where Bills.UserID = ? and Bills.Status = 'Pending'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, currentUser.getUserID());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                CartItem cartItem = new CartItem(
                        resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getString("Title"),
                        resultSet.getString("Image"),
                        resultSet.getDouble("Price"),
                        resultSet.getInt("Amount"),
                        resultSet.getDouble("TotalAmount")
                );
                cart.add(cartItem);
            }

            cartTableView.setItems(cart);

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
    public void goToHome(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/home.fxml");
    }

    @FXML
    public void goToCart(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/cart.fxml");
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
    public void logout(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/login.fxml");
    }
}