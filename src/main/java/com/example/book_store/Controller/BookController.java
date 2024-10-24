package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;
import com.example.book_store.Entity.Book;
import com.example.book_store.Entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class BookController implements Initializable {
    private final User currentUser = Authentication.currentUser;
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

    @FXML
    TableView bookTable;
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
    @FXML
    public TableColumn actionColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (currentUser.getRole().equalsIgnoreCase("admin")) {
            goToCart.setVisible(false);
            initializeAdmin();
        } else {
            initializeUser();
        }
    }

    private void initializeAdmin() {
        idColumn.setCellValueFactory(new PropertyValueFactory<Book, Integer>("bookID"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("image"));
        imageColumn.setCellFactory(column -> new TableCell<Book, String>() {

            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(new Image(imagePath));
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);
                    setGraphic(imageView);
                }
            }
        });
        titleColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));
        publishedYearColumn.setCellValueFactory(new PropertyValueFactory<Book, Integer>("publishedYear"));
        editionColumn.setCellValueFactory(new PropertyValueFactory<Book, Integer>("edition"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Book, Double>("price"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<Book, Integer>("amount"));
        bookTypeIDColumn.setCellValueFactory(new PropertyValueFactory<Book, Integer>("bookTypeID"));
        publisherIDColumn.setCellValueFactory(new PropertyValueFactory<Book, Integer>("publisherID"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<Book, Boolean>("status"));


        actionColumn.setCellFactory(column -> new TableCell<Book, Void>() {
            private final Button editBook = new Button("Edit");
            private final Button deleteBook = new Button("Delete");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    editBook.setOnAction(e -> {
                        Book book = getTableView().getItems().get(getIndex());
                        showEditDialog(book);
                    });

                    deleteBook.setOnAction(e -> {
                        Book book = getTableView().getItems().get(getIndex());
                        if (deactivationBook(book.getBookID())) {
                            loadBooks();
                        }
                    });

                    HBox hBox = new HBox(editBook, deleteBook);
                    hBox.setSpacing(10);
                    setGraphic(hBox);
                }
            }
        });

        loadBooks();
    }

    private void initializeUser() {
        addBook.setVisible(false);
        idColumn.setVisible(false);
        imageColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("image"));
        imageColumn.setCellFactory(column -> new TableCell<Book, String>() {

            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(new Image(imagePath));
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);
                    setGraphic(imageView);
                }
            }
        });
        titleColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));
        publishedYearColumn.setCellValueFactory(new PropertyValueFactory<Book, Integer>("publishedYear"));
        editionColumn.setCellValueFactory(new PropertyValueFactory<Book, Integer>("edition"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Book, Double>("price"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<Book, Integer>("amount"));
        bookTypeIDColumn.setCellValueFactory(new PropertyValueFactory<Book, Integer>("bookTypeID"));
        publisherIDColumn.setCellValueFactory(new PropertyValueFactory<Book, Integer>("publisherID"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<Book, Boolean>("status"));
        actionColumn.setCellFactory(column -> new TableCell<Book, Void>() {
            private final Button addToCartButton = new Button("Add to cart");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    addToCartButton.setOnAction(e -> {
                        Book book = getTableView().getItems().get(getIndex());
                        showAmountDialog(book);
                    });

                    HBox hBox = new HBox(addToCartButton);
                    hBox.setSpacing(10);
                    setGraphic(hBox);
                }
            }
        });


        loadBooks();
    }

    private void showAmountDialog(Book book) {
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
            loadBooks();
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

    private void showEditDialog(Book book) {
        TextField title = new TextField(book.getTitle());
        TextField img = new TextField(book.getImage());
        TextField author = new TextField(book.getAuthor());
        TextField publishedYear = new TextField(String.valueOf(book.getPublishedYear()));
        TextField edition = new TextField(String.valueOf(book.getEdition()));
        TextField price = new TextField(String.valueOf(book.getPrice()));
        TextField amount = new TextField(String.valueOf(book.getAmount()));
        TextField bookTypeID = new TextField(String.valueOf(book.getBookTypeID()));
        TextField publisherID = new TextField(String.valueOf(book.getPublisherID()));
        TextField status = new TextField(String.valueOf(book.isStatus()));

        Button saveButton = new Button("Save");

        VBox vbox = new VBox(title, img, author, publishedYear, edition, price, amount, bookTypeID, publisherID, status, saveButton);
        vbox.setSpacing(10);
        Scene scene = new Scene(vbox, 240, 480);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Edit");
        stage.show();

        saveButton.setOnAction(e -> {
            if (title.getText().isEmpty() || author.getText().isEmpty() || publishedYear.getText().isEmpty() || img.getText().isEmpty() || status.getText().isEmpty() || price.getText().isEmpty() || edition.getText().isEmpty() || amount.getText().isEmpty() || bookTypeID.getText().isEmpty() || publisherID.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "No blank!");
                return;
            }

            String query = "update books set Title = ?, Image = ?, Author = ?, PublishedYear = ?, Edition = ?, Price = ?, Amount = ?, BookTypeID = ?, PublisherID = ?, Status = ? where BookID = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, title.getText());
                preparedStatement.setString(2, img.getText());
                preparedStatement.setString(3, author.getText());
                preparedStatement.setInt(4, Integer.parseInt(publishedYear.getText()));
                preparedStatement.setInt(5, Integer.parseInt(edition.getText()));
                preparedStatement.setDouble(6, Double.parseDouble(price.getText()));
                preparedStatement.setInt(7, Integer.parseInt(amount.getText()));
                preparedStatement.setInt(8, Integer.parseInt(bookTypeID.getText()));
                preparedStatement.setInt(9, Integer.parseInt(publisherID.getText()));
                preparedStatement.setBoolean(10, Boolean.parseBoolean(status.getText()));
                preparedStatement.setInt(11, book.getBookID());
                int row = preparedStatement.executeUpdate();
                if (row != 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Successful", "Chang applied.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Failed");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            loadBooks();
            stage.close();
        });
    }

    private void loadBooks() {
        ObservableList<Book> bookList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Books";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getInt("BookID"),
                        resultSet.getString("Image"),
                        resultSet.getString("Title"),
                        resultSet.getString("Author"),
                        resultSet.getInt("PublishedYear"),
                        resultSet.getInt("Edition"),
                        resultSet.getDouble("Price"),
                        resultSet.getInt("Amount"),
                        resultSet.getInt("BookTypeID"),
                        resultSet.getInt("PublisherID"),
                        resultSet.getBoolean("Status")
                );
                bookList.add(book);
            }

            bookTable.setItems(bookList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load book data");
        }
    }

    @FXML
    private void showAddBookDialog() {
        TextField img = new TextField();
        img.setPromptText("Url");
        TextField title = new TextField();
        title.setPromptText("Title");
        TextField author = new TextField();
        author.setPromptText("Author");
        TextField publishedYear = new TextField();
        publishedYear.setPromptText("Public year");
        TextField edition = new TextField();
        edition.setPromptText("Edition");
        TextField price = new TextField();
        price.setPromptText("Price");
        TextField amount = new TextField();
        amount.setPromptText("Amount");
        TextField bookTypeID = new TextField();
        bookTypeID.setPromptText("Book type ID");
        TextField publisherID = new TextField();
        publisherID.setPromptText("Publisher ID");

        Button saveButton = new Button("Add Book");

        VBox vbox = new VBox(img, title, author, publishedYear, edition, price, amount, bookTypeID, publisherID, saveButton);
        vbox.setSpacing(10);
        Scene scene = new Scene(vbox, 240, 480);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Add Book");
        stage.show();

        saveButton.setOnAction(e -> {
            if (title.getText().isEmpty() || amount.getText().isEmpty() || publishedYear.getText().isEmpty() || img.getText().isEmpty() || price.getText().isEmpty() || edition.getText().isEmpty() || amount.getText().isEmpty() || bookTypeID.getText().isEmpty() || publisherID.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "No blank!");
                return;
            }

            String checkQuery = "SELECT * FROM books WHERE Title = ? AND Author = ?";
            try {
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, title.getText());
                checkStatement.setString(2, author.getText());
                ResultSet rs = checkStatement.executeQuery();

                if (rs.next()) { // Nếu sách đã tồn tại
                    int existingAmount = rs.getInt("Amount");
                    int newAmount = existingAmount + Integer.parseInt(amount.getText());

                    // Cập nhật số lượng sách
                    String updateQuery = "UPDATE books SET Amount = ? WHERE BookID = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setInt(1, newAmount);
                    updateStatement.setInt(2, rs.getInt("BookID"));
                    int row = updateStatement.executeUpdate();
                    if (row != 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Successful", "Book amount updated successfully.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Failed", "Book amount updated failed");
                    }
                } else { // Nếu sách chưa tồn tại, thêm mới
                    String insertQuery = "INSERT INTO books (Title, Image, Author, PublishedYear, Edition, Price, Amount, BookTypeID, PublisherID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                    preparedStatement.setString(1, img.getText());
                    preparedStatement.setString(2, title.getText());
                    preparedStatement.setString(3, author.getText());
                    preparedStatement.setInt(4, Integer.parseInt(publishedYear.getText()));
                    preparedStatement.setInt(5, Integer.parseInt(edition.getText()));
                    preparedStatement.setDouble(6, Double.parseDouble(price.getText()));
                    preparedStatement.setInt(7, Integer.parseInt(amount.getText()));
                    preparedStatement.setInt(8, Integer.parseInt(bookTypeID.getText()));
                    preparedStatement.setInt(9, Integer.parseInt(publisherID.getText()));
                    preparedStatement.executeUpdate();

                    showAlert(Alert.AlertType.INFORMATION, "Successful", "Book added successfully.");
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "SQL Error", "Error while adding/updating book: " + ex.getMessage());
            }
            loadBooks(); // Cập nhật lại danh sách sách
            stage.close(); // Đóng cửa sổ thêm sách
        });
    }

    private boolean deactivationBook(int bookID) {
        String query = "update books set Status = false where BookID = ?";
        try {
            if (showConfirmation("Delete book", "Are you sure want to delete this book ?")) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, bookID);
                int row = preparedStatement.executeUpdate();

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
        goToScene(event, "/com/example/book_store/view/home.fxml");
    }

    @FXML
    public void goToCart(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/cart.fxml");
    }

    public void goToAddBookScene(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/addBook.fxml");
    }
}