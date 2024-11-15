package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;
import com.example.book_store.Entity.Book;
import com.example.book_store.Entity.Order;
import com.example.book_store.Entity.OrderItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static com.example.book_store.Controller.Authentication.currentUser;

public class OrderController implements Initializable {
    @FXML
    public HBox menuBar;
    @FXML
    public DatePicker first;
    @FXML
    public DatePicker second;
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

    private ObservableList<Order> orders = FXCollections.observableArrayList();
    private UserController userController = new UserController();
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (currentUser.getRole().equalsIgnoreCase("admin")) {
            menuBar.getChildren().remove(goToCart);
        } else {
            menuBar.getChildren().remove(goToUser);
        }
        orderID.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        userID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        status.setCellFactory(column -> new TableCell<Order, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    if (item.equalsIgnoreCase("Pending")) {
                        setText("Chờ thanh toán");
                    } else if (item.equalsIgnoreCase("Paid")) {
                        setText("Đã thanh toán");
                    } else {
                        setText("Hủy đơn");
                    }
                }
            }
        });
        action.setCellFactory(column -> new TableCell<>() {
            private final Button detail = new Button("Chi tiết");
            private final Button confirm = new Button("Xác nhận");
            private final Button cancelled = new Button("Hủy");

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
                        loadOrders();
                    });

                    cancelled.setOnAction(e -> {
                        cancelOrder(order);
                        loadOrders();
                    });
                    HBox hBox;
                    if (currentUser.getRole().equalsIgnoreCase("admin")) {
                        if (order.getStatus().equalsIgnoreCase("Pending")) {
                            hBox = new HBox(detail, confirm, cancelled);
                        } else {
                            hBox = new HBox(detail);
                        }
                    } else {
                        hBox = new HBox(detail);
                    }
                    hBox.setSpacing(10);
                    setGraphic(hBox);
                }
            }
        });

        loadOrders();
    }

    private void confirmOrder(Order order) {
        // Cập nhật trạng thái đơn hàng thành 'Paid'
        String updateOrderStatusQuery = "UPDATE Orders SET Status = 'Paid' WHERE OrderID = ?";

        // Tạo hóa đơn mới sau khi xác nhận đơn hàng
        String createBillQuery = "INSERT INTO Bills (OrderID, UserID, TotalAmount, BillDate) VALUES (?, ?, ?, NOW())";

        try {
            // Bước 1: Cập nhật trạng thái đơn hàng
            PreparedStatement updateStmt = connection.prepareStatement(updateOrderStatusQuery);
            updateStmt.setInt(1, order.getOrderID());
            updateStmt.executeUpdate();

            // Bước 2: Tính tổng tiền từ các OrderItem của Order
            double totalAmount = calculateTotalAmount(order.getOrderID());

            // Bước 3: Tạo hóa đơn mới với tổng tiền
            PreparedStatement createBillStmt = connection.prepareStatement(createBillQuery);
            createBillStmt.setInt(1, order.getOrderID());
            createBillStmt.setInt(2, order.getUserID());
            createBillStmt.setDouble(3, totalAmount);
            createBillStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to confirm order and create bill.", e);
        }
    }

    // Hàm hỗ trợ tính tổng tiền dựa trên OrderItem của Order
    private double calculateTotalAmount(int orderID) throws SQLException {
        String totalQuery = "SELECT SUM(Amount * Price) AS TotalAmount FROM OrderItems WHERE OrderID = ?";
        PreparedStatement totalStmt = connection.prepareStatement(totalQuery);
        totalStmt.setInt(1, orderID);

        ResultSet resultSet = totalStmt.executeQuery();
        if (resultSet.next()) {
            return resultSet.getDouble("TotalAmount");
        }
        return 0.0;
    }


    private void loadOrders() {
        String query;
        orders.clear();
        try {
            PreparedStatement preparedStatement;
            if (currentUser.getRole().equalsIgnoreCase("user")) {
                query = """
                            SELECT * FROM orders
                            WHERE UserID = ?
                        """;
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, currentUser.getUserID());
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
            } else {
                query = """
                            SELECT * FROM orders
                        """;
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.execute();
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
            orderIDCol.setCellValueFactory(new PropertyValueFactory<>("orderID"));

            TableColumn<OrderItem, Integer> bookIDCol = new TableColumn<>("Book ID");
            bookIDCol.setCellValueFactory(new PropertyValueFactory<>("bookID"));

            TableColumn<OrderItem, Integer> quantityCol = new TableColumn<>("Amount");
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

            TableColumn<OrderItem, Double> priceCol = new TableColumn<>("Price");
            priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

            TableColumn<OrderItem, Double> totalPriceCol = new TableColumn<>("Total Price");
            totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

            // Thêm các cột vào TableView
            if (currentUser.getRole().equalsIgnoreCase("admin")) {
                orderItemTableView.getColumns().addAll(itemIDCol, orderIDCol, bookIDCol, quantityCol, priceCol, totalPriceCol);
            } else {
                orderItemTableView.getColumns().addAll(itemIDCol, orderIDCol, bookIDCol, quantityCol, priceCol, totalPriceCol);
            }
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
        String cancelOrderQuery = "UPDATE orders SET Status = 'Cancelled' WHERE OrderID = ?";
        String getOrderItemsQuery = "SELECT OrderItemID, BookID, Amount FROM OrderItems WHERE OrderID = ?";
        String updateBookStockQuery = "UPDATE Books SET Amount = Amount + ? WHERE BookID = ?";
        String deleteOrderItemsQuery = "DELETE FROM OrderItems WHERE OrderID = ?";
        String deleteCartItemsQuery = "DELETE FROM CartItems WHERE OrderItemID = ?";

        try {
            // Bước 1: Cập nhật trạng thái đơn hàng thành "Cancelled"
            PreparedStatement cancelOrderStmt = connection.prepareStatement(cancelOrderQuery);
            cancelOrderStmt.setInt(1, order.getOrderID());
            int row = cancelOrderStmt.executeUpdate();

            if (row > 0) {
                // Bước 2: Truy xuất tất cả OrderItems trong đơn hàng bị hủy
                PreparedStatement getOrderItemsStmt = connection.prepareStatement(getOrderItemsQuery);
                getOrderItemsStmt.setInt(1, order.getOrderID());
                ResultSet resultSet = getOrderItemsStmt.executeQuery();

                // Bước 3: Cập nhật lại kho sách và lưu danh sách OrderItemID để xóa CartItems
                while (resultSet.next()) {
                    int orderItemID = resultSet.getInt("OrderItemID");
                    int bookID = resultSet.getInt("BookID");
                    int amount = resultSet.getInt("Amount");

                    // Cập nhật lại kho sách
                    PreparedStatement updateBookStockStmt = connection.prepareStatement(updateBookStockQuery);
                    updateBookStockStmt.setInt(1, amount);
                    updateBookStockStmt.setInt(2, bookID);
                    updateBookStockStmt.executeUpdate();

                    // Xóa các CartItem liên quan đến OrderItemID này
                    PreparedStatement deleteCartItemsStmt = connection.prepareStatement(deleteCartItemsQuery);
                    deleteCartItemsStmt.setInt(1, orderItemID);
                    deleteCartItemsStmt.executeUpdate();
                }

                // Bước 4: Xóa tất cả OrderItems của đơn hàng
                PreparedStatement deleteOrderItemsStmt = connection.prepareStatement(deleteOrderItemsQuery);
                deleteOrderItemsStmt.setInt(1, order.getOrderID());
                deleteOrderItemsStmt.executeUpdate();

                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
        if (currentUser.getRole().equalsIgnoreCase("admin")) {
            goToScene(event, "/com/example/book_store/view/home.fxml");
        } else {
            goToScene(event, "/com/example/book_store/view/homeUser.fxml");
        }
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
    public void goToCart(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/cart.fxml");
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
    public void search(ActionEvent event) {
        ObservableList<Order> filters = FXCollections.observableArrayList();
        for (Order order : orders) {
            LocalDate orderDate = LocalDate.parse(order.getDate());
            System.out.println(orderDate);
            if ((first.getValue().isEqual(orderDate) || first.getValue().isBefore(orderDate)) && (second.getValue().isEqual(orderDate) || second.getValue().isAfter(orderDate))) {
                filters.add(order);
            }
        }
        orderTable.setItems(filters);
    }
}
