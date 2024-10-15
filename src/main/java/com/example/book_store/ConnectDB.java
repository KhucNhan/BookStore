package com.example.book_store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    private static final String urlConnection = "jdbc:mysql://127.0.0.1:3306/book_store";

    public Connection connectionDB() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(urlConnection, "root", "nhan771026");
            if (connection == null) {
                connection = DriverManager.getConnection(urlConnection, "root", "1234");
                System.out.println("Successful! It's Trang.");
            } else {
                System.out.println("Successful! It's Nhan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}