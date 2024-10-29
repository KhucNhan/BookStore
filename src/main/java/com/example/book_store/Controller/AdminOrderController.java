package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.example.book_store.Entity.Bill;

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
    @FXML
    private TableColumn<Bill, Integer> billId;
    @FXML
    private TableColumn<Bill, Integer> customerId;
    @FXML
    private TableColumn<Bill, Date> date;
    @FXML
    private TableColumn<Bill, Double> totalAmount;
    @FXML
    private TableColumn<Bill, Boolean> status;
    @FXML
    private TableColumn<Bill, String> action;

    private ObservableList<Bill> billList;
    private UserController userController = new UserController();
    private BillController billController = new BillController();
    private OrderController orderController = new OrderController();
    private BookOrderController bookOrderController = new BookOrderController();
    private BookController bookController = new BookController();
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();

    @FXML
    public void initialize() {
        billList = FXCollections.observableArrayList();
        setTableColumns();
        loadBillDataFromDatabase();
        billTable.setItems(billList);
    }

    private void setTableColumns() {
        billId.setCellValueFactory(new PropertyValueFactory<>("billID"));
        customerId.setCellValueFactory(new PropertyValueFactory<>("userID"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        totalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        // Add a cell factory for action column if needed
    }

    private void loadBillDataFromDatabase() {
        String query = "SELECT BillID, Date, TotalAmount, UserID, Status FROM bills WHERE Status = 'pending'";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int billID = resultSet.getInt("BillID");
                int userID = resultSet.getInt("UserID");
                Date date = resultSet.getDate("Date");
                double totalAmount = resultSet.getDouble("TotalAmount");
                String status = resultSet.getString("Status");

                Bill bill = new Bill(billID,date , totalAmount, userID, status);
                billList.add(bill);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
    }


    @FXML
    private void goToHome(ActionEvent event) {

    }

    @FXML
    private void goToOrder(ActionEvent event) {

    }

    @FXML
    private void searchOrders(ActionEvent event) {

    }

    @FXML
    private void updateUserInformation(ActionEvent event) {

    }

    @FXML
    private void updateUserPassword(ActionEvent event) {

    }

    @FXML
    private void deactivationUser(ActionEvent event) {

    }

    @FXML
    private void logout(ActionEvent event) {

    }
}
