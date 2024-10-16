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
        } catch (SQLException e) {

        }

        try {
            connection = DriverManager.getConnection(urlConnection, "root", "1234");
        } catch (SQLException e) {

        }
        return connection;
    }
}