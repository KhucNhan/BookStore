package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;
import com.example.book_store.Entity.Book;
import com.example.book_store.Entity.CartItem;
import com.example.book_store.Entity.User;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import java.util.ArrayList;
import java.util.List;


public class CartController {
    public TableColumn<CartItem, Void> act;
    private UserController userController = new UserController();
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();
    private final User currentUser = Authentication.currentUser;
    public TableColumn<CartItem, Boolean> selected;
    public CheckBox selectAllBooks;

    @FXML
    private TableView<CartItem> cartTableView;

    @FXML
    private TableColumn<CartItem, Integer> cartItemID;
    @FXML
    private TableColumn<CartItem, Integer> cardID;
    @FXML
    private TableColumn<CartItem, Integer> bookID;

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
    private TableColumn<CartItem, Boolean> status;

    @FXML
    private Label totalCartLabel;

    @FXML
    private Button buyButton;
    @FXML
    public Button goToCart;
    @FXML
    public Button goToUser;
    @FXML
    public Button goToOrder;
    @FXML
    public Button goToOrderConfirm;

    private ObservableList<Book> cart;

    @FXML
    public void initialize() {

        cartItemID.setCellValueFactory(new PropertyValueFactory<>("cartItemID"));
        cartItemID.setVisible(false);

        cardID.setCellValueFactory(new PropertyValueFactory<>("cartID"));
        cardID.setVisible(false);

        bookID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        bookID.setVisible(false);

        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        status.setVisible(false);

        selected.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isSelected()));
        selected.setCellFactory(column -> new TableCell<>() {
            private final CheckBox selectedCheckbox = new CheckBox();

            @Override
            protected void updateItem(Boolean selectedValue, boolean empty) {
                super.updateItem(selectedValue, empty);
                if (empty || selectedValue == null) {
                    setGraphic(null);
                } else {
                    selectedCheckbox.setSelected(selectedValue);

                    selectedCheckbox.setOnAction(event -> {
                        CartItem item = getTableView().getItems().get(getIndex());

                        item.setSelected(selectedCheckbox.isSelected());

                        updateSelectedCartItem(item.isSelected(), item.getCartItemID());

                        updateSelectAllBooks();
                        updateTotalCartLabel();
                    });
                    setGraphic(selectedCheckbox);
                    setAlignment(Pos.valueOf("CENTER"));
                }
            }
        });
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        image.setCellValueFactory(new PropertyValueFactory<>("image"));
        image.setCellFactory(column -> new TableCell<>() {

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
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        amount.setCellFactory(column -> new TableCell<>() {
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
                        if (!isOutOfStock(cartItem, 1)) {
                            minusAmount(cartItem);
                            loadCart();
                            updateTotalCartLabel();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Failed", "This book is out of stock");
                        }
                    });

                    plus.setOnAction(e -> {
                        if (!isOutOfStock(cartItem, 1)) {
                            plusAmount(cartItem);
                            loadCart();
                            updateTotalCartLabel();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Failed", "This book is out of stock");
                        }
                    });

                    HBox hBox = new HBox(minus, currentAmount, plus);
                    hBox.setAlignment(Pos.valueOf("CENTER"));
                    hBox.setSpacing(10);
                    setGraphic(hBox);
                }
            }
        });
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        act.setCellFactory(column -> new TableCell<>() {
            private final Button delete = new Button("Xóa");

            @Override
            protected void updateItem(Void act, boolean empty) {
                super.updateItem(act, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    CartItem cartItem = getTableView().getItems().get(getIndex()); // Lấy item từ hàng hiện tại
                    delete.setOnAction(e -> {
                        deleteFromCart(cartItem.getCartItemID());
                        getTableView().getItems().remove(cartItem);
                    });
                    setGraphic(delete);
                }
            }
        });

        act.setCellValueFactory(cellData -> new SimpleObjectProperty<>(null));
        total.setCellValueFactory(new PropertyValueFactory<>("total"));

        loadCart();
    }

    private void updateSelectedCartItem(boolean value, int cartItemID) {
        String query = "update cartitems set Selected = ? where CartItemID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setBoolean(1, value);
            preparedStatement.setInt(2, cartItemID);
            preparedStatement.executeUpdate();
            loadCart();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateSelectAllBooks() {
        boolean allSelected = cartTableView.getItems().stream()
                .allMatch(CartItem::isSelected);
        selectAllBooks.setSelected(allSelected);
    }

    @FXML
    public boolean deleteFromCart(int cartItemID) {
        String query = "DELETE FROM CartItems WHERE CartItemID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cartItemID);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isOutOfStock(CartItem cartItem, int amount) {
        String query = "select Amount from books where BookID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, cartItem.getbookID());
            ResultSet resultSet = preparedStatement.executeQuery();
            int bookAmount = 0;
            while (resultSet.next()) {
                bookAmount = resultSet.getInt(1);
            }
            return amount > bookAmount;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean minusAmount(CartItem cartItem) {
        String query = "UPDATE CartItems " +
                "SET Amount = Amount - 1 " +
                "WHERE CartItemID = ? AND Amount > 0";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, cartItem.getCartItemID());
            int row = preparedStatement.executeUpdate();

            if (row > 0) {
                query = "update books set Amount = Amount + 1 where BookID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, cartItem.getbookID());
                row = preparedStatement.executeUpdate();
                return row > 0;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean plusAmount(CartItem cartItem) {
        String query = "UPDATE CartItems " +
                "SET Amount = Amount + 1 " +
                "WHERE CartItemID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, cartItem.getCartItemID());
            int row = preparedStatement.executeUpdate();

            if (row > 0) {
                query = "update books set Amount = Amount - 1 where BookID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, cartItem.getbookID());
                row = preparedStatement.executeUpdate();
                return row > 0;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    public void selectAllBooks(ActionEvent event) {
        double total = 0.0;
        if (selectAllBooks.isSelected()) {
            for (CartItem item : cartTableView.getItems()) {
                item.setSelected(true);
                updateSelectedCartItem(true, item.getCartItemID());
                total += item.getPrice() * item.getAmount();
            }
            totalCartLabel.setText(String.format("$%.2f", total));
        } else {
            for (CartItem item : cartTableView.getItems()) {
                item.setSelected(false);
                updateSelectedCartItem(false, item.getCartItemID());
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
        String query = """
                    SELECT ci.CartItemID, ci.Selected, ci.CartID, ci.BookID, b.Image, b.Title, 
                           ci.Price, ci.Amount AS Amount, ci.TotalPrice, ci.Status
                    FROM CartItems ci
                    JOIN Cart c ON ci.CartID = c.CartID
                    JOIN Books b ON ci.BookID = b.BookID
                    WHERE c.UserID = ? and ci.Status = false
                """;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, currentUser.getUserID());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                CartItem cartItem = new CartItem(
                        resultSet.getInt("CartItemID"),
                        resultSet.getBoolean("Selected"),
                        resultSet.getInt("CartID"),
                        resultSet.getInt("BookID"),
                        resultSet.getString("Image"),
                        resultSet.getString("Title"),
                        resultSet.getDouble("Price"),
                        resultSet.getInt("Amount"),
                        resultSet.getDouble("TotalPrice"),
                        resultSet.getBoolean("Status")
                );

                cart.add(cartItem);
            }

            cartTableView.setItems(cart);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load cart data");
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
        sendOrder();
    }

    private void sendOrder() {
        List<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : cartTableView.getItems()) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }

        if (selectedItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "No items selected for order.");
            return;
        }

        // 1. Tạo đơn hàng mới
        String orderQuery = "INSERT INTO Orders (UserID, OrderDate) VALUES (?, CURRENT_TIMESTAMP)";
        try {
            PreparedStatement orderStmt = connection.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, currentUser.getUserID());
            int affectedRows = orderStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            int orderID;
            if (generatedKeys.next()) {
                orderID = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating order failed, no ID obtained.");
            }

            // 2. Tạo từng OrderItem cho các sản phẩm đã chọn và lấy OrderItemID
            String orderItemQuery = "INSERT INTO OrderItems (OrderID, BookID, Amount, Price) VALUES (?, ?, ?, ?)";
            PreparedStatement orderItemStmt = connection.prepareStatement(orderItemQuery, Statement.RETURN_GENERATED_KEYS);

            // 3. Cập nhật OrderItemID trong CartItems cho mỗi sản phẩm
            String updateCartItemQuery = "UPDATE CartItems SET Status = true WHERE CartItemID = ?";
            PreparedStatement updateCartItemStmt = connection.prepareStatement(updateCartItemQuery);

            for (CartItem item : selectedItems) {
                // Thêm sản phẩm vào OrderItems
                orderItemStmt.setInt(1, orderID);
                orderItemStmt.setInt(2, item.getbookID());
                orderItemStmt.setInt(3, item.getAmount());
                orderItemStmt.setDouble(4, item.getPrice());
                int row = orderItemStmt.executeUpdate();

                if (row > 0) {
                    // Cập nhật
                    updateCartItemStmt.setInt(1, item.getCartItemID());
                    updateCartItemStmt.executeUpdate();
                }
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "Order created successfully.");
            loadCart(); // Làm mới giao diện giỏ hàng

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create order.");
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
            Scene scene = new Scene(root,1280,800);
            stage.setScene(scene);
//            stage.setFullScreen(true);
            stage.show();
        } else {
            // Ép kiểu nguồn sự kiện từ MenuItem (không thuộc về root) về Node
            Node node = ((MenuItem) event.getSource()).getParentPopup().getOwnerNode();
            Stage stage = (Stage) node.getScene().getWindow();
            Scene scene = new Scene(root,1280,800);
            stage.setScene(scene);
//            stage.setFullScreen(true);
            stage.show();
        }
    }

    @FXML
    public void goToHome(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/homeUser.fxml");
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
}