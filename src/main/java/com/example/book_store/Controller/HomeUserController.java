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
    private BookOrderController bookOrderController = new BookOrderController();
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
            stage.close();
            try {
                addToCart(currentUser.getUserID(), book.getBookID(), Integer.parseInt(amount.getText()), book.getPrice());
                showAlert(Alert.AlertType.INFORMATION, "Successful", "Add to cart successful");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void addToCart(int userId, int bookId, int quantity, double price) throws SQLException {
        // 1. Kiểm tra hóa đơn hiện có hay chưa
        PreparedStatement stmt = connection.prepareStatement("SELECT BillID FROM Bills WHERE UserID = ? AND Status = 'pending'");
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        int billId;
        if (rs.next()) {
            billId = rs.getInt("BillID");
        } else {
            // Tạo hóa đơn mới
            stmt = connection.prepareStatement("INSERT INTO Bills (Date, TotalAmount, UserID, Status) VALUES (CURRENT_DATE, 0, ?, 'pending')", Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                billId = rs.getInt(1);
            } else {
                throw new SQLException("Failed to create bill");
            }
        }

        // 2. Kiểm tra xem có order nào với BillID và BookID đã tồn tại hay chưa
        stmt = connection.prepareStatement("SELECT OrderID, Amount FROM Orders WHERE BillID = ? AND BookID = ?");
        stmt.setInt(1, billId);
        stmt.setInt(2, bookId);
        rs = stmt.executeQuery();

        if (rs.next()) {
            // Nếu đã tồn tại order, cập nhật số lượng và tổng tiền
            int orderId = rs.getInt("OrderID");
            int existingAmount = rs.getInt("Amount");
            int newAmount = existingAmount + quantity;

            // Cập nhật số lượng trong bảng Orders
            stmt = connection.prepareStatement("UPDATE Orders SET Amount = ?, Price = ? WHERE OrderID = ?");
            stmt.setInt(1, newAmount);
            stmt.setDouble(2, price);
            stmt.setInt(3, orderId);
            stmt.executeUpdate();

            // Cập nhật số lượng trong bảng Books_Orders
            stmt = connection.prepareStatement("UPDATE Books_Orders SET Amount = ? WHERE OrderID = ? AND BookID = ?");
            stmt.setInt(1, newAmount);
            stmt.setInt(2, orderId);
            stmt.setInt(3, bookId);
            stmt.executeUpdate();

            // Cập nhật tổng tiền hóa đơn
            updateTotalAmount(stmt, billId);
        } else {
            // 3. Thêm sản phẩm vào Orders
            stmt = connection.prepareStatement("INSERT INTO Orders (BillID, BookID, Amount, Price) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, billId);
            stmt.setInt(2, bookId);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, price);
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            int orderId;
            if (rs.next()) {
                orderId = rs.getInt(1);
            } else {
                throw new SQLException("Failed to add order");
            }

            // 4. Cập nhật số lượng sách trong kho
            stmt = connection.prepareStatement("UPDATE Books SET Amount = Amount - ? WHERE BookID = ?");
            stmt.setInt(1, quantity);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();

            // Cập nhật số lượng trong bảng Books_Orders
            stmt = connection.prepareStatement("INSERT INTO Books_Orders (OrderID, BookID, Amount) VALUES (?, ?, ?)");
            stmt.setInt(1, orderId);
            stmt.setInt(2, bookId);
            stmt.setInt(3, quantity);
            stmt.executeUpdate();

            // 5. Cập nhật tổng tiền hóa đơn
            updateTotalAmount(stmt, billId);
        }
    }

    private void updateTotalAmount(PreparedStatement stmt, int billId) throws SQLException {
        // Cập nhật tổng tiền hóa đơn
        stmt = connection.prepareStatement("UPDATE Bills SET TotalAmount = (SELECT SUM(Price * Amount) FROM Orders WHERE BillID = ?) WHERE BillID = ?");
        stmt.setInt(1, billId);
        stmt.setInt(2, billId);
        stmt.executeUpdate();
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
