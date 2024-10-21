package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;
import com.example.book_store.Entity.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.example.book_store.Controller.LoginController.currentUser;

public class BookController {
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();
    @FXML
    private TableView bookTable;
    @FXML
    public TableColumn idColumn;
    @FXML
    public TableColumn imageColumn;
    @FXML
    public TableColumn titleColumn;
    @FXML
    public TableColumn authorColumn;
    @FXML
    public TableColumn publishedYearColumn;
    @FXML
    public TableColumn editionColumn;
    @FXML
    public TableColumn priceColumn;
    @FXML
    public TableColumn amountColumn;
    @FXML
    public TableColumn bookTypeIDColumn;
    @FXML
    public TableColumn publisherIDColumn;
    @FXML
    public TableColumn statusColumn;

    public boolean addBook(String title, String image, String author, int publishedYear, int edition, double price, int amount, int bookTypeID, int publisherID) {
        ConnectDB connectDB = new ConnectDB();
        String query = "insert into Books (Title, Image, Author, PublishedYear, Edition, Price, Amount, BookTypeID, PublisherID) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            if (String.valueOf(publishedYear).length() != 4) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Enter right year");
                return false;
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, image);
            preparedStatement.setString(3, author);
            preparedStatement.setInt(4, publishedYear);
            preparedStatement.setInt(5, edition);
            preparedStatement.setDouble(6, price);
            preparedStatement.setInt(7, amount);
            preparedStatement.setInt(8, bookTypeID);
            preparedStatement.setInt(9, publisherID);
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
    private Stage stage;
    private Scene scene;
    private Parent root;
    public void goToScene(ActionEvent event, String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}