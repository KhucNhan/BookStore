package com.example.book_store;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.*;

public class BookController {
    @FXML
    public static void getBooks(ActionEvent event) {
        ConnectDB connectDB = new ConnectDB();
        String query = "select Title, Author, PublishedYear, Edition, Price, Amount, NameBookType, NamePublisher from Books " +
                "join BookTypes on Books.BookTypeID = BookTypes.BookTypeID " +
                "join Publishers on Books.PublisherID = Publishers.PublisherID";
        Connection connection;

        try {
            connection = connectDB.connectionDB();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                System.out.println(
                        "Tên sách: " + resultSet.getString(2)
                                + "|| Tác giả: " + resultSet.getString(3)
                                + "|| Năm xuất bản: " + resultSet.getInt(4)
                                + "|| Lần xuất bản: " + resultSet.getInt(5)
                                + "|| Giá bán: " + resultSet.getDouble(6)
                                + "|| Loại sách: " + resultSet.getString("NameBookType")
                                + "|| Nhà xuất bản: " + resultSet.getString("NamePublisher")
                );
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addBook(String title, String author, int publishedYear, int edition, double price, int amount, int bookTypeID, int publisherID) {
        ConnectDB connectDB = new ConnectDB();
        String query = "insert into Books (Title, Author, PublishedYear, Edition, Price, Amount, BookTypeID, PublisherID) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection connection;

        try {
            connection = connectDB.connectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,title);
            preparedStatement.setString(2,author);
            preparedStatement.setInt(3, publishedYear);
            preparedStatement.setInt(4, edition);
            preparedStatement.setDouble(5,price);
            preparedStatement.setInt(6,amount);
            preparedStatement.setInt(7,bookTypeID);
            preparedStatement.setInt(8,publisherID);
            int row = preparedStatement.executeUpdate();
            if (row != 0) {
                return true;
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}