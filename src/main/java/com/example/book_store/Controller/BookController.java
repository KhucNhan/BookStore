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
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class BookController implements Initializable {
    private final User currentUser = Authentication.currentUser;
    @FXML
    public Button addBook;
    @FXML
    public Button goToHome;
    @FXML
    public Button goToUser;
    @FXML
    public Button goToOrder;
    @FXML
    public Button goToCart;
    @FXML
    public TextField searchField;
    @FXML
    public Button searchButton;
    private UserController userController = new UserController();
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
    public TableColumn bookTypeColumn;
    @FXML
    public TableColumn publisherColumn;
    @FXML
    public TableColumn statusColumn;
    @FXML
    public TableColumn actionColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchButton.setOnAction(e -> search());
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
        bookTypeColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("bookType"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("publisher"));
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

    private void showEditDialog(Book book) {
        TextField title = new TextField(book.getTitle());
        TextField img = new TextField(book.getImage());
        TextField author = new TextField(book.getAuthor());
        TextField publishedYear = new TextField(String.valueOf(book.getPublishedYear()));
        TextField edition = new TextField(String.valueOf(book.getEdition()));
        TextField price = new TextField(String.valueOf(book.getPrice()));
        TextField amount = new TextField(String.valueOf(book.getAmount()));
        TextField bookType = new TextField(String.valueOf(book.getBookType()));
        TextField publisher = new TextField(String.valueOf(book.getPublisher()));
        TextField status = new TextField(String.valueOf(book.isStatus()));

        Button saveButton = new Button("Save");

        VBox vbox = new VBox(title, img, author, publishedYear, edition, price, amount, bookType, publisher, status, saveButton);
        vbox.setSpacing(10);
        Scene scene = new Scene(vbox, 240, 480);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Edit");
        stage.show();

        saveButton.setOnAction(e -> {
            if (title.getText().isEmpty() || author.getText().isEmpty() || publishedYear.getText().isEmpty() || img.getText().isEmpty() || status.getText().isEmpty() || price.getText().isEmpty() || edition.getText().isEmpty() || amount.getText().isEmpty() || bookType.getText().isEmpty() || publisher.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "No blank!");
                return;
            }

            String query = "update books set Title = ?, Image = ?, Author = ?, PublishedYear = ?, Edition = ?, Price = ?, Amount = ?, BookType = ?, Publisher = ?, Status = ? where BookID = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, title.getText());
                preparedStatement.setString(2, img.getText());
                preparedStatement.setString(3, author.getText());
                preparedStatement.setInt(4, Integer.parseInt(publishedYear.getText()));
                preparedStatement.setInt(5, Integer.parseInt(edition.getText()));
                preparedStatement.setDouble(6, Double.parseDouble(price.getText()));
                preparedStatement.setInt(7, Integer.parseInt(amount.getText()));
                preparedStatement.setString(8, bookType.getText());
                preparedStatement.setString(9, publisher.getText());
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
                        resultSet.getString("BookType"),
                        resultSet.getString("Publisher"),
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
        TextField bookType = new TextField();
        bookType.setPromptText("Book type");
        TextField publisher = new TextField();
        publisher.setPromptText("Publisher");

        Button saveButton = new Button("Add Book");

        VBox vbox = new VBox(img, title, author, publishedYear, edition, price, amount, bookType, publisher, saveButton);
        vbox.setSpacing(10);
        Scene scene = new Scene(vbox, 240, 480);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Add Book");
        stage.show();

        saveButton.setOnAction(e -> {
            if (title.getText().isEmpty() || amount.getText().isEmpty() || publishedYear.getText().isEmpty() || img.getText().isEmpty() || price.getText().isEmpty() || edition.getText().isEmpty() || amount.getText().isEmpty() || bookType.getText().isEmpty() || publisher.getText().isEmpty()) {
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

                    String insertQuery = "INSERT INTO books ( Image, Title, Author, PublishedYear, Edition, Price, Amount, BookType, Publisher) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                    preparedStatement.setString(1, img.getText());
                    preparedStatement.setString(2, title.getText());
                    preparedStatement.setString(3, author.getText());
                    preparedStatement.setInt(4, Integer.parseInt(publishedYear.getText()));
                    preparedStatement.setInt(5, Integer.parseInt(edition.getText()));
                    preparedStatement.setDouble(6, Double.parseDouble(price.getText()));
                    preparedStatement.setInt(7, Integer.parseInt(amount.getText()));
                    preparedStatement.setString(8, bookType.getText());
                    preparedStatement.setString(9, publisher.getText());
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
    public void goToHome(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/homeUser.fxml");
    }

    @FXML
    public void goToCart(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/cart.fxml");
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
    public void logout(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/login.fxml");
    }

    @FXML
    public void goToUser(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/user.fxml");
    }

    @FXML
    public void goToOrder(ActionEvent event) throws IOException {
        goToScene(event, "/com/example/book_store/view/order.fxml");
    }

    @FXML
    public void goToHistory(ActionEvent actionEvent) throws IOException {
        goToScene(actionEvent, "/com/example/book_store/view/bill.fxml");
    }

    @FXML
    public void goToTop5(ActionEvent actionEvent) throws IOException {
        goToScene(actionEvent, "/com/example/book_store/view/statistical.fxml");
    }

    @FXML
    public void search() {

        String keyword = searchField.getText();
        if (keyword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a search keyword.");
            return;
        }

        ObservableList<Book> bookList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Books WHERE Title LIKE ? OR Author LIKE ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            String searchKeyword = "%" + keyword + "%";
            preparedStatement.setString(1, searchKeyword);
            preparedStatement.setString(2, searchKeyword);

            ResultSet resultSet = preparedStatement.executeQuery();

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
                        resultSet.getString("BookType"),
                        resultSet.getString("Publisher"),
                        resultSet.getBoolean("Status")
                );
                bookList.add(book);
            }

            bookTable.setItems(bookList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not search book data");
        }

    }
}