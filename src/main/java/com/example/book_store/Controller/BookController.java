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

public class BookController implements Initializable {
    private final User currentUser = Authentication.currentUser;
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
        actionColumn.setCellValueFactory(new PropertyValueFactory<Book, Void>(""));

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
        TextField bookTypeID = new TextField(String.valueOf(book.getBookTypeID()));
        TextField publisherID = new TextField(String.valueOf(book.getPublisherID()));
        TextField status = new TextField(String.valueOf(book.getPublishedYear()));

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

            String query = "update from books set Title = ?, Image = ?, Author = ?, PublishedYear = ?, Edition = ?, Price = ?, Amount = ?, BookTypeID = ?, PublisherID = ?, Status = ? where BookID = ?";
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
                int row = preparedStatement.executeUpdate();
                if (row != 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Successful", "Chang applied.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed", "Failed");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            bookTable.refresh();
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
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load book data");
        }
    }


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

    private boolean deactivationBook(int bookID) {
        String query = "update books set Status = false where BookID = ?";
        try {
            if (showConfirmation("Delete book", "Are you sure want to delete this book ?")) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, bookID);
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
}