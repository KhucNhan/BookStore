package com.example.book_store.Controller;

import com.example.book_store.Entity.Book;
import com.example.book_store.ConnectDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.book_store.Controller.Authentication.currentUser;

public class HomeUserController {

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
    private CartController cartController = new CartController();
    private BookController bookController = new BookController();
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();

    public void initialize() {
        loadBooksFromDatabase();
        displayAllBooks();
    }

    private void loadBooksFromDatabase() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books where Status = true")) {

            booksList.clear();
            while (rs.next()) {
                Book book = Book.fromResultSet(rs);
                booksList.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.INFORMATION, "Database Error", "Failed to load books from the database.");
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

        Button addToCartButton = new Button("Thêm vào giỏ hàng");
        addToCartButton.setOnAction(event -> {
            showAmountDialog(book);
        });

        bookBox.getChildren().addAll(bookImage, title, price, detailsButton, addToCartButton);
        booksContainer.getChildren().add(bookBox);
    }

    public void showAmountDialog(Book book) {
        TextField amount = new TextField("1");
        Button saveButton = new Button("Save");

        VBox vbox = new VBox(amount, saveButton);
        vbox.setSpacing(10);
        Scene scene = new Scene(vbox, 240, 480);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Enter amount");
        stage.show();

        saveButton.setOnAction(e -> {
            if (Integer.parseInt(amount.getText()) < 1) {
                showAlert(Alert.AlertType.ERROR, "Failed", "Enter right amount");
                return;
            }
            stage.close();
            if (addToCart(currentUser.getUserID(), book.getBookID(), Integer.parseInt(amount.getText()))) {
                showAlert(Alert.AlertType.INFORMATION, "Successful", "Add to cart successful");
            }
        });
    }

    private boolean addToCart(int userID, int bookID, int amount) {
        String checkQuery = "SELECT CartItemID, Amount FROM CartItems " +
                "JOIN Cart ON CartItems.CartID = Cart.CartID " +
                "WHERE Cart.UserID = ? AND CartItems.BookID = ? and CartItems.OrderItemID is null ";

        String updateQuery = "UPDATE CartItems SET Amount = Amount + ? " +
                "WHERE CartItemID = ?";

        String insertQuery = "INSERT INTO CartItems (CartID, BookID, Amount, Price) " +
                "VALUES (" +
                "(SELECT CartID FROM Cart WHERE UserID = ?), " +
                "?, " +
                "?, " +
                "(SELECT Price FROM Books WHERE BookID = ?) )";

        try {
            // Bước 1: Kiểm tra nếu mục đã có trong giỏ hàng
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setInt(1, userID);
            checkStmt.setInt(2, bookID);

            ResultSet resultSet = checkStmt.executeQuery();
            int row = 0;

            if (resultSet.next()) {
                // Bước 2: Nếu mục đã tồn tại, cập nhật số lượng
                int cartItemID = resultSet.getInt("CartItemID");
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setInt(1, amount);
                updateStmt.setInt(2, cartItemID);
                row = updateStmt.executeUpdate();
                System.out.println("Updated quantity for existing item in cart.");
            } else {
                // Bước 3: Nếu mục chưa tồn tại, thêm mục mới
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setInt(1, userID);
                insertStmt.setInt(2, bookID);
                insertStmt.setInt(3, amount);
                insertStmt.setInt(4, bookID);
                row = insertStmt.executeUpdate();
                System.out.println("Added new item to cart.");
            }
            return row > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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


        VBox content = new VBox(10, bookImage, titleLabel, authorLabel, priceLabel, quantityLabel, publicationYearLabel);
        content.setAlignment(Pos.CENTER_LEFT);

        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    private void showAlert(Alert.AlertType information, String title, String message) {
        Alert alert = new Alert(information);
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
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            // Ép kiểu nguồn sự kiện từ MenuItem (không thuộc về root) về Node
            Node node = ((MenuItem) event.getSource()).getParentPopup().getOwnerNode();
            Stage stage = (Stage) node.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    public void logout(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/login.fxml");
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
    public void goToHome(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/homeUser.fxml");
    }

    @FXML
    public void goToCart(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/cart.fxml");
    }

    public void goToAddBookScene(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/addBook.fxml");
    }
}
