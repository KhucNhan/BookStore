package com.example.book_store.Controller;

import com.example.book_store.ConnectDB;
import com.example.book_store.Entity.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

import static com.example.book_store.Controller.Authentication.currentUser;

public class StatisticalController {

    @FXML
    public TableColumn top;
    @FXML
    public HBox menuBar;
    @FXML
    public Button goToUser;
    @FXML
    public Button goToOrderConfirm;
    @FXML
    public Button goToCart;
    @FXML
    public Button goToOrder;
    @FXML
    private TableView bestsellingBooksTable;
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
    private UserController userController = new UserController();
    private final ConnectDB connectDB = new ConnectDB();
    private final Connection connection = connectDB.connectionDB();

    // Hàm khởi tạo
    public void initialize() {
        if (currentUser.getRole().equalsIgnoreCase("user")) {
            idColumn.setVisible(false);
            publishedYearColumn.setVisible(false);
            editionColumn.setVisible(false);
            amountColumn.setVisible(false);
            bookTypeColumn.setVisible(false);
            publisherColumn.setVisible(false);
            statusColumn.setVisible(false);
            menuBar.getChildren().remove(goToUser);
        } else{
            menuBar.getChildren().remove(goToCart);
        }
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

        top.setCellFactory(column -> new TableCell<Book, Void>() {
            private final Label label = new Label();

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Hiển thị thứ hạng sách (bắt đầu từ 1 thay vì 0)
                    int rank = getTableRow().getIndex() + 1;
                    label.setText("Top " + String.valueOf(rank));
                    setGraphic(label);
                }
            }
        });
        loadBestsellingBooks();
    }

    private void loadBestsellingBooks() {
        ObservableList<Book> bookList = FXCollections.observableArrayList();
        String query = """
                SELECT oi.BookID, bo.Image, bo.Title, bo.Author, bo.PublishedYear, bo.Edition, bo.Price, bo.Amount, bo.BookType, bo.Publisher, bo.Status, 
                    SUM(oi.Amount) AS TongSoSachDaBan
                FROM bills b 
                JOIN orderitems oi ON b.OrderID = oi.OrderID 
                JOIN books bo ON bo.BookID = oi.BookID 
                GROUP BY oi.BookID 
                ORDER BY TongSoSachDaBan DESC 
                LIMIT 5;
                """;
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

            bestsellingBooksTable.setItems(bookList);

        } catch (SQLException e) {
            e.printStackTrace();
//            showAlert(Alert.AlertType.ERROR, "Error", "Could not load book data");
        }
    }

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
        if (currentUser.getRole().equalsIgnoreCase("admin")) {
            goToScene(event, "/com/example/book_store/view/home.fxml");
        } else {
            goToScene(event, "/com/example/book_store/view/homeUser.fxml");
        }
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
}
