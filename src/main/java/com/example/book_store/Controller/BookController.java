package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.sql.*;
import java.util.Optional;

public class BookController {
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();
    @FXML
    public void getBooks(ActionEvent event) {
        String query = "select Title, Author, PublishedYear, Edition, Price, Amount, NameBookType, NamePublisher, Status from Books " +
                "join BookTypes on Books.BookTypeID = BookTypes.BookTypeID " +
                "join Publishers on Books.PublisherID = Publishers.PublisherID";

        try {
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
                                + "|| Trạng thái: " + resultSet.getBoolean("Status")
                );
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addBook(String title, String image, String author, int publishedYear, int edition, double price, int amount, int bookTypeID, int publisherID) {
        ConnectDB connectDB = new ConnectDB();
        String query = "insert into Books (Title, Image, Author, PublishedYear, Edition, Price, Amount, BookTypeID, PublisherID) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            if(String.valueOf(publishedYear).length() != 4) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Enter right year");
                return false;
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,title);
            preparedStatement.setString(2,image);
            preparedStatement.setString(3,author);
            preparedStatement.setInt(4, publishedYear);
            preparedStatement.setInt(5, edition);
            preparedStatement.setDouble(6,price);
            preparedStatement.setInt(7,amount);
            preparedStatement.setInt(8,bookTypeID);
            preparedStatement.setInt(9,publisherID);
            int row = preparedStatement.executeUpdate();
            connection.close();
            showAlert(Alert.AlertType.INFORMATION, "Successful", "Add book successful");
            return row != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    @FXML
    private boolean deactivationBook(ActionEvent event) {
        String query = "update books set Status = false where BookID = ?";
        try {
            if (showConfirmation("Delete book", "Are you sure want to delete this book ?")) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, 3);
                int row = preparedStatement.executeUpdate();
                connection.close();
                showAlert(Alert.AlertType.INFORMATION, "Successful", "Delete book successful");
                return row != 0;
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Failed", "Cancel");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> option = alert.showAndWait();
        return option.get() == ButtonType.OK;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}