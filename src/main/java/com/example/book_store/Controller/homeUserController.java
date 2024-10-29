package com.example.book_store.Controller;

import com.example.book_store.Entity.Book;
import com.example.book_store.ConnectDB;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class homeUserController {

    @FXML
    private FlowPane booksContainer;

    @FXML
    private TextField searchBookField;

    private List<Book> booksList = new ArrayList<>();
    @FXML
    public Button addBook;
    public Button goToHome;
    public Button goToCart;
    private UserController userController = new UserController();
    private BillController billController = new BillController();
    private OrderController orderController = new OrderController();
    private BookOrderController bookOrderController = new BookOrderController();
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();

    public void initialize() {
        loadBooksFromDatabase();
        displayAllBooks();
    }

    private void loadBooksFromDatabase() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {

            booksList.clear();
            while (rs.next()) {
                Book book = Book.fromResultSet(rs);
                booksList.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load books from the database.");
        }
    }

    private void displayAllBooks() {
        booksContainer.getChildren().clear();
        for (Book book : booksList) {
            addBookToDisplay(book);
        }
    }

    private void addBookToDisplay(Book book) {
        VBox bookBox = new VBox();
        bookBox.setSpacing(5);
        bookBox.setAlignment(Pos.CENTER);
        bookBox.setStyle("-fx-border-color: #000; -fx-padding: 10; -fx-background-color: #F0F0F0;");

        ImageView bookImage = new ImageView(new Image(book.getImage()));
        bookImage.setFitHeight(200);
        bookImage.setFitWidth(160);
        Label title = new Label(book.getTitle());
        title.setStyle("-fx-font-weight: bold;");
        Label price = new Label("Giá: " + book.getPrice());


        Button detailsButton = new Button("Chi tiết");
        detailsButton.setOnAction(event -> showBookDetails(book));

        bookBox.getChildren().addAll(bookImage, title, price, detailsButton);
        booksContainer.getChildren().add(bookBox);
    }

    private void showBookDetails(Book book) {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Thông tin chi tiết sách");


        ImageView bookImage = new ImageView(new Image(book.getImage()));
        bookImage.setFitHeight(250);
        bookImage.setFitWidth(200);

        Label titleLabel = new Label("Tên sách: " + book.getTitle());
        Label authorLabel = new Label("Tác giả: " + book.getAuthor());
        Label priceLabel = new Label("Giá: " + book.getPrice() + " VND");
        Label quantityLabel = new Label("Số lượng: " + book.getAmount());
        Label publicationYearLabel = new Label("Năm xuất bản: " + book.getPublishedYear());

        Button addToCartButton = new Button("Thêm vào giỏ hàng");
        addToCartButton.setOnAction(event -> {
            dialog.close();
        });


        VBox content = new VBox(10, bookImage, titleLabel, authorLabel, priceLabel, quantityLabel, publicationYearLabel, addToCartButton);
        content.setAlignment(Pos.CENTER_LEFT);

        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goToHome() {

    }

    @FXML
    private void goToCart() {

    }

    @FXML
    private void updateUserInformation() {

    }

    @FXML
    private void updateUserPassword() {

    }

    @FXML
    private void deactivationUser() {

    }

    @FXML
    private void logout() {

    }
}
