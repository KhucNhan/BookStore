package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderController {
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();
    public boolean addOrder(int billID, int bookID, int amount, double price) {
        String query = "insert into Orders (BillID, BookID, Amount, Price) select ?, ?, ?, ? from Books where BookID = ?";
        int row = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, billID);
            preparedStatement.setInt(2, bookID);
            preparedStatement.setInt(3, amount);
            preparedStatement.setDouble(4, price);
            preparedStatement.setInt(5, bookID);
            row = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return row != 0;
    }
}
