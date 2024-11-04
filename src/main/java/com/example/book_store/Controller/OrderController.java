package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;
import com.example.book_store.Entity.CartItem;
import com.example.book_store.Entity.Order;
import com.example.book_store.Entity.OrderItem;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.example.book_store.Entity.Bill;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import static com.example.book_store.Controller.Authentication.currentUser;

public class OrderController implements Initializable {
    @FXML
    private TableView<Order> orderTable;
    @FXML
    public TableColumn<Order, Integer> orderID;
    @FXML
    public TableColumn<Order, Integer> userID;
    @FXML
    public TableColumn<Order, String> date;
    @FXML
    public TableColumn<Order, String> status;
    @FXML
    public TableColumn<Order, Void> action;
    @FXML
    private Button goToHome;
    @FXML
    public Button goToCart;
    @FXML
    public Button goToUser;
    @FXML
    public Button goToOrder;
    @FXML
    public Button goToOrderConfirm;
    @FXML
    private Button searchButton;
    @FXML
    private MenuButton settingsMenuButton;

    private ObservableList<Order> orders;
    private UserController userController = new UserController();
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {

        orderID.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        userID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        action.setCellFactory(column -> new TableCell<>() {
            private final Button detail = new Button("Order detail");
            private final Button confirm = new Button("Confirm");
            private final Button cancelled = new Button("Cancelled");

            @Override
            protected void updateItem(Void act, boolean empty) {
                super.updateItem(act, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex()); // Lấy item từ hàng hiện tại
                    detail.setOnAction(e -> {
                        showOrderDetail(order);
                    });

                    confirm.setOnAction(e -> {
                        confirmOrder(order);
                    });

                    cancelled.setOnAction(e -> {
                        cancelOrder(order);
                    });
                    if (order.getStatus().equalsIgnoreCase("Pending")) {
                        HBox hBox = new HBox(detail, confirm, cancelled);
                        hBox.setSpacing(10);
                        setGraphic(hBox);
                    } else {
                        HBox hBox = new HBox(detail);
                        hBox.setSpacing(10);
                        setGraphic(hBox);
                    }
                }
            }
        });

        loadOrders();
    }

    private void confirmOrder(Order order) {
        String query = "update orders set Status = 'Paid' where OrderID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, order.getUserID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadOrders() {
        ObservableList<Order> orders = FXCollections.observableArrayList();
        String query = """
                SELECT * FROM orders
                WHERE Status = 'Pending'
            """;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Order order = new Order(
                        resultSet.getInt("OrderID"),
                        resultSet.getInt("UserID"),
                        resultSet.getDate("OrderDate").toString(),
                        resultSet.getString("Status")
                );
                orders.add(order);
            }
            orderTable.setItems(orders);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load order data");
        }
    }


    private void showOrderDetail(Order selectedOrder) {
        if (selectedOrder == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an order to view details.");
            return;
        }

        int orderID = selectedOrder.getOrderID();
        ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();

        String query = """
        SELECT oi.OrderItemID, oi.OrderID, oi.BookID, oi.Amount, oi.Price, oi.TotalPrice 
        FROM OrderItems oi
        JOIN Books b ON oi.BookID = b.BookID
        WHERE oi.OrderID = ?
    """;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, orderID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                OrderItem orderItem = new OrderItem(
                        resultSet.getInt("OrderItemID"),
                        resultSet.getInt("OrderID"),
                        resultSet.getInt("BookID"),
                        resultSet.getInt("Amount"),
                        resultSet.getDouble("Price"),
                        resultSet.getDouble("TotalPrice")
                );

                orderItems.add(orderItem);
            }

            // Tạo TableView mới cho OrderItem
            TableView<OrderItem> orderItemTableView = new TableView<>(orderItems);

            // Định nghĩa các cột cho TableView
            TableColumn<OrderItem, Integer> itemIDCol = new TableColumn<>("Order Item ID");
            itemIDCol.setCellValueFactory(new PropertyValueFactory<>("orderItemID"));

            TableColumn<OrderItem, Integer> orderIDCol = new TableColumn<>("Order ID");
            itemIDCol.setCellValueFactory(new PropertyValueFactory<>("orderID"));

            TableColumn<OrderItem, Integer> bookIDCol = new TableColumn<>("Book ID");
            itemIDCol.setCellValueFactory(new PropertyValueFactory<>("bookID"));

            TableColumn<OrderItem, Integer> quantityCol = new TableColumn<>("Amount");
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

            TableColumn<OrderItem, Double> priceCol = new TableColumn<>("Price");
            priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

            TableColumn<OrderItem, Double> totalPriceCol = new TableColumn<>("Total Price");
            totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

            // Thêm các cột vào TableView
            orderItemTableView.getColumns().addAll(itemIDCol, orderIDCol, bookIDCol, quantityCol, priceCol, totalPriceCol);

            // Tạo cửa sổ mới hiển thị TableView
            Stage detailStage = new Stage();
            detailStage.setTitle("Order Details - Order #" + orderID);

            VBox vbox = new VBox(orderItemTableView);
            Scene scene = new Scene(vbox);
            detailStage.setScene(scene);
            detailStage.show();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load order details.");
        }
    }



    private boolean cancelOrder(Order order) {
        String query = "update orders set Status = 'Cancelled' where OrderID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, order.getOrderID());
            int row = preparedStatement.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    @FXML
    public void goToUser(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/user.fxml");
    }

    @FXML
    public void goToOrderConfirm(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/adminConfirmOrder.fxml");
    }

    @FXML
    public void goToOrder(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/cart.fxml");
    }

    @FXML
    public void goToHistory(ActionEvent actionEvent) throws IOException {
        goToScene(actionEvent, "/com/example/book_store/view/bill.fxml");
    }
}
