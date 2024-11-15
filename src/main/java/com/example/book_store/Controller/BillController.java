package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;
import com.example.book_store.Entity.Bill;
import com.example.book_store.Entity.Order;
import com.example.book_store.Entity.OrderItem;
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

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class BillController {
    @FXML
    public Button goToCart;
    @FXML
    public Button goToUser;
    @FXML
    public Button goToOrder;
    @FXML
    public HBox menuBar;
    private UserController userController = new UserController();
    @FXML
    private TableView<Bill> billTable;

    @FXML
    private TableColumn<Bill, Integer> orderId;
    @FXML
    private TableColumn<Bill, Integer> billID;
    @FXML
    private TableColumn<Bill, Integer> userID;

    @FXML
    private TableColumn<Bill, String> date;

    @FXML
    private TableColumn<Bill, Double> totalAmount;

    @FXML
    private TableColumn<Bill, Void> act;


    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();
    private final User currentUser = Authentication.currentUser;

    private ObservableList<Bill> bills = FXCollections.observableArrayList();

    @FXML

    public void initialize() {
        if (currentUser.getRole().equalsIgnoreCase("admin")) {
            menuBar.getChildren().remove(goToCart);
        } else {
            menuBar.getChildren().remove(goToUser);
        }
        billID.setCellValueFactory(new PropertyValueFactory<>("billID"));
        orderId.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        userID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        totalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        act.setCellFactory(column -> new TableCell<>() {
            private Button detail = new Button("Chi tiết");

            @Override
            protected void updateItem(Void act, boolean empty) {
                super.updateItem(act, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Bill bill = getTableView().getItems().get(getIndex());
                    detail.setOnAction(e -> {
                        showBillDetail(bill.getBillID());
                    });
                    setGraphic(detail);
                }
            }
        });

        act.setCellValueFactory(cellData -> new SimpleObjectProperty<>(null));
        loadBills();
    }

    private void showBillDetail(int billID) {
        ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
        String query = """
                select oi.OrderItemID,oi.OrderID, oi.BookID, oi.Amount, oi.Price, oi.TotalPrice
                From orderItems oi
                join bills b on oi.OrderID = b.OrderID
                where b.BillID= ?
                """;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, billID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orderItems.add(new OrderItem(
                        resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getInt(4),
                        resultSet.getDouble(5),
                        resultSet.getDouble(6)
                ));
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
            orderItemTableView.getColumns().addAll(itemIDCol, orderIDCol, bookIDCol, quantityCol, priceCol, totalPriceCol);
            Stage detailStage = new Stage();
            detailStage.setTitle("Bill Details - Bill #" + billID);

            VBox vbox = new VBox(orderItemTableView);
            Scene scene = new Scene(vbox);
            detailStage.setScene(scene);
            detailStage.show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBills() {
        try {
            String query;
            PreparedStatement preparedStatement;
            if (currentUser.getRole().equalsIgnoreCase("admin")) {
                query = "SELECT * FROM bills";
                preparedStatement = connection.prepareStatement(query);
            } else {
                query = "select * from bills where UserID = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, currentUser.getUserID());
            }
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                bills.add(new Bill(
                        resultSet.getInt(1),
                        resultSet.getDate(5).toString(),
                        resultSet.getDouble(4),
                        resultSet.getInt(2),
                        resultSet.getInt(3)

                ));
            }

            billTable.setItems(bills);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public DatePicker first;
    @FXML
    public DatePicker second;

    @FXML
    public void search(ActionEvent event) {
        ObservableList<Bill> filters = FXCollections.observableArrayList();
        for (Bill bill : bills) {
            LocalDate billDate = LocalDate.parse(bill.getDate());
            if ((first.getValue().isEqual(billDate) || first.getValue().isBefore(billDate)) && (second.getValue().isEqual(billDate) || second.getValue().isAfter(billDate))) {
                filters.add(bill);
            }
        }
        billTable.setItems(filters);
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
        if (currentUser.getRole().equalsIgnoreCase("admin")) {
            goToScene(event, "/com/example/book_store/view/home.fxml");
        } else {
            goToScene(event, "/com/example/book_store/view/homeUser.fxml");
        }
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
